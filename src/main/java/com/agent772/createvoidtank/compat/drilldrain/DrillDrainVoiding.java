package com.agent772.createvoidtank.compat.drilldrain;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.agent772.createvoidtank.config.ModConfig;
import com.agent772.createvoidtank.registry.ModBlocks;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * Soft compat with Drill Drain: decides whether fluid a Drill Drain collects on a moving contraption should be
 * voided. Only used through {@code DrillMovementBehaviourMixin}, which is only applied when Drill Drain is
 * installed.
 * <p>
 * A contraption voids Drill Drain fluid if it carries at least one Void Tank that was active when the
 * contraption was assembled (the tank's saved "Active" state: heat and redstone cannot be checked while
 * moving). The blocks of a contraption do not change while it moves, so the answer is cached per contraption.
 */
public final class DrillDrainVoiding {

    // Drill Drain only collects fluid on the server thread today; synchronized in case that ever changes.
    private static final Map<Contraption, Boolean> HAS_ACTIVE_VOID_TANK =
            Collections.synchronizedMap(new WeakHashMap<>());

    private DrillDrainVoiding() {
    }

    public static boolean voids(MovementContext context) {
        if (!ModConfig.VOID_DRILL_DRAIN_FLUID.get() || context.contraption == null) {
            return false;
        }
        return HAS_ACTIVE_VOID_TANK.computeIfAbsent(context.contraption, DrillDrainVoiding::hasActiveVoidTank);
    }

    private static boolean hasActiveVoidTank(Contraption contraption) {
        for (StructureBlockInfo info : contraption.getBlocks().values()) {
            if (info.state().is(ModBlocks.VOID_TANK.get()) && wasActive(info.nbt())) {
                return true;
            }
        }
        return false;
    }

    /** Same default as VoidTankBlockEntity#read: a tank without a saved state counts as active. */
    private static boolean wasActive(CompoundTag nbt) {
        return nbt == null || !nbt.contains("Active") || nbt.getBoolean("Active");
    }
}
