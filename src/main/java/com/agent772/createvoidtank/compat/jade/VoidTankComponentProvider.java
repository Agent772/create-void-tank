package com.agent772.createvoidtank.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.fluids.FluidStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum VoidTankComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("Active"))
            return;

        boolean active = data.getBoolean("Active");
        String mode = data.getString("Mode");

        if (active) {
            tooltip.add(Component.translatable("createvoidtank.jade.voiding")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            if (data.contains("LastFluid")) {
                FluidStack fluid = FluidStack.parse(
                        accessor.getLevel().registryAccess(),
                        data.getCompound("LastFluid")
                ).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    tooltip.add(Component.translatable("createvoidtank.jade.voiding_fluid",
                            fluid.getHoverName()).withStyle(ChatFormatting.GRAY));
                }
            }
        } else {
            String key = switch (mode) {
                case "REQUIRES_HEAT" -> "createvoidtank.jade.inactive.heat";
                case "REQUIRES_REDSTONE" -> "createvoidtank.jade.inactive.redstone";
                default -> "createvoidtank.jade.inactive";
            };
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return VoidTankJadePlugin.VOID_TANK;
    }
}
