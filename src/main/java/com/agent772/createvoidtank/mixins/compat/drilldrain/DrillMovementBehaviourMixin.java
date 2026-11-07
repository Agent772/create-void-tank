package com.agent772.createvoidtank.mixins.compat.drilldrain;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.agent772.createvoidtank.compat.drilldrain.DrillDrainVoiding;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Drill Drain soft compat: voids the fluid a Drill Drain collects on a contraption that carries an active Void
 * Tank. Only applied when Drill Drain is installed (see {@code CreateVoidTankMixinPlugin}).
 * <p>
 * Drill Drain's {@code tryCollectFluids} fills the contraption's fluid storage twice (simulate, then execute)
 * and only picks the fluid up if all of it fits. This wraps both calls: on a contraption with an active Void
 * Tank the fluid skips the contraption's fluid tanks entirely and is reported as accepted, so it is voided at
 * the source. Fluid from any other source (e.g. Portable Fluid Interfaces) is never touched and fills the
 * contraption's tanks as usual; the Void Tank is not part of the contraption's fluid storage.
 */
@Pseudo
@Mixin(targets = "com.cake.drill_drain.content.replacements.DrillMovementBehaviourReplacement", remap = false)
public abstract class DrillMovementBehaviourMixin {

    // require = 0: if a future Drill Drain version changes this method, the compat simply turns off.
    @WrapOperation(
            method = "tryCollectFluids",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/contraption/storage/fluid/MountedFluidStorageWrapper;fill(Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)I"),
            require = 0)
    private int createvoidtank$voidCollectedFluid(MountedFluidStorageWrapper storage, FluidStack stack,
            IFluidHandler.FluidAction action, Operation<Integer> original, @Local(argsOnly = true) MovementContext context) {
        if (DrillDrainVoiding.voids(context)) {
            return stack.getAmount(); // voided by the Void Tank on board; the contraption's tanks get none of it
        }
        return original.call(storage, stack, action);
    }
}
