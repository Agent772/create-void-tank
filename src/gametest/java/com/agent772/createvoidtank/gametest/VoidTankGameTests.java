package com.agent772.createvoidtank.gametest;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Basic GameTests that run in every GameTest run (with and without Drill Drain). The world loading at all and
 * these passing means the mod (and its mixin config) loads fine in that setup.
 */
@GameTestHolder(CreateVoidTank.MODID)
@PrefixGameTestTemplate(false)
public class VoidTankGameTests {

    /** Set per run in build.gradle, so a misconfigured run fails instead of silently testing the wrong setup. */
    static final boolean EXPECT_DRILL_DRAIN = Boolean.getBoolean("createvoidtank.gametest.drillDrain");

    @GameTest(template = "empty")
    public static void drillDrainPresenceMatchesRun(GameTestHelper helper) {
        boolean loaded = ModList.get().isLoaded("drill_drain");
        helper.assertTrue(loaded == EXPECT_DRILL_DRAIN,
                "drill_drain loaded=" + loaded + " but this run expects " + EXPECT_DRILL_DRAIN);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void voidTankVoidsPumpedFluid(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.VOID_TANK.get());

        IFluidHandler handler = helper.getLevel()
                .getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(pos), Direction.UP);
        helper.assertTrue(handler != null, "Void Tank exposes no fluid handler");

        int filled = handler.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(filled == 1000, "Void Tank accepted " + filled + " mB instead of 1000");
        helper.assertTrue(handler.getFluidInTank(0).isEmpty(), "Void Tank kept fluid instead of voiding it");
        helper.succeed();
    }
}
