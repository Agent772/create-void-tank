package com.agent772.createvoidtank.gametest;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.registry.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Drill Drain compat GameTests. They call Drill Drain's own fluid pickup on a fake contraption, so they fail if
 * our mixin stops hooking into it (its {@code require = 0} would otherwise make that a silent failure).
 * <p>
 * Drill Drain removes the fluid source block only when the contraption accepts all of the fluid. The fake
 * contraption has no fluid tanks, so the source block disappears only if the fluid gets voided.
 * <p>
 * In the run without Drill Drain these pass immediately (the presence check in {@link VoidTankGameTests} makes
 * sure each run has the setup it expects).
 */
@GameTestHolder(CreateVoidTank.MODID)
@PrefixGameTestTemplate(false)
public class DrillDrainGameTests {

    private static final String DRILL_BEHAVIOUR =
            "com.cake.drill_drain.content.replacements.DrillMovementBehaviourReplacement";

    @GameTest(template = "empty")
    public static void activeVoidTankVoidsDrillDrainFluid(GameTestHelper helper) {
        collectWater(helper, true, true);
    }

    @GameTest(template = "empty")
    public static void inactiveVoidTankDoesNotVoid(GameTestHelper helper) {
        collectWater(helper, false, false);
    }

    @GameTest(template = "empty")
    public static void noVoidTankDoesNotVoid(GameTestHelper helper) {
        collectWater(helper, null, false);
    }

    /**
     * @param tankActive saved "Active" state of the Void Tank on the contraption, or null for no Void Tank
     */
    private static void collectWater(GameTestHelper helper, @Nullable Boolean tankActive, boolean expectVoided) {
        if (!ModList.get().isLoaded("drill_drain")) {
            helper.succeed();
            return;
        }

        BlockPos water = new BlockPos(1, 1, 1);
        helper.setBlock(water, Blocks.WATER);

        Contraption contraption = new BearingContraption();
        if (tankActive != null) {
            CompoundTag tankNbt = new CompoundTag();
            tankNbt.putBoolean("Active", tankActive);
            contraption.getBlocks().put(BlockPos.ZERO,
                    new StructureBlockInfo(BlockPos.ZERO, ModBlocks.VOID_TANK.getDefaultState(), tankNbt));
        }
        contraption.getStorage().initialize(); // no fluid tanks on board: fills that aren't voided accept 0 mB

        CompoundTag drillNbt = new CompoundTag();
        drillNbt.putBoolean("DrillDrainParent", true); // marks the drill as having a Drill Drain attached
        BlockPos drillPos = new BlockPos(0, 1, 0);
        MovementContext context = new MovementContext(helper.getLevel(),
                new StructureBlockInfo(drillPos, AllBlocks.MECHANICAL_DRILL.getDefaultState(), drillNbt), contraption);

        try {
            Class<?> behaviourClass = Class.forName(DRILL_BEHAVIOUR);
            Object behaviour = behaviourClass.getDeclaredConstructor().newInstance();
            Method tryCollectFluids = behaviourClass.getDeclaredMethod("tryCollectFluids", MovementContext.class,
                    BlockPos.class);
            tryCollectFluids.setAccessible(true);
            tryCollectFluids.invoke(behaviour, context, helper.absolutePos(water));
        } catch (ReflectiveOperationException e) {
            CreateVoidTank.LOGGER.error("Calling Drill Drain's fluid pickup failed", e);
            helper.fail("Could not call Drill Drain's fluid pickup (did Drill Drain change?): " + e);
            return;
        }

        boolean voided = !helper.getLevel().getFluidState(helper.absolutePos(water)).isSource();
        helper.assertTrue(voided == expectVoided, expectVoided
                ? "Water source is still there: the Void Tank did not void the Drill Drain fluid (mixin not applied?)"
                : "Water source was picked up although nothing should have accepted it");
        helper.succeed();
    }
}
