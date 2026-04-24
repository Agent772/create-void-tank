package com.agent772.createvoidtank.compat.jade;

import com.agent772.createvoidtank.config.ModConfig;
import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.fluids.FluidStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum VoidTankDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof VoidTankBlockEntity be))
            return;

        data.putBoolean("Active", be.isActive());
        data.putString("Mode", ModConfig.ACTIVATION_MODE.get().name());

        FluidStack lastFluid = be.getLastVoidedFluid();
        if (!lastFluid.isEmpty()) {
            data.put("LastFluid", lastFluid.save(accessor.getLevel().registryAccess()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return VoidTankJadePlugin.VOID_TANK;
    }
}
