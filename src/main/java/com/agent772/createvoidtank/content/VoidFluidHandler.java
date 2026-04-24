package com.agent772.createvoidtank.content;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class VoidFluidHandler implements IFluidHandler {

    private final Supplier<Boolean> activeSupplier;
    private final Consumer<FluidStack> onVoid;

    public VoidFluidHandler(Supplier<Boolean> activeSupplier) {
        this(activeSupplier, null);
    }

    public VoidFluidHandler(Supplier<Boolean> activeSupplier, Consumer<FluidStack> onVoid) {
        this.activeSupplier = activeSupplier;
        this.onVoid = onVoid;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!activeSupplier.get()) {
            return 0;
        }
        if (action.execute() && onVoid != null && !resource.isEmpty()) {
            onVoid.accept(resource);
        }
        return resource.getAmount();
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
