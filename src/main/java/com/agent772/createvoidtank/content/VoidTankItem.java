package com.agent772.createvoidtank.content;

import com.agent772.createvoidtank.config.ModConfig;
import com.agent772.createvoidtank.config.ModConfig.ActivationMode;
import com.agent772.createvoidtank.config.ModConfig.MinimumHeatLevel;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class VoidTankItem extends BlockItem {

    public VoidTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("createvoidtank.tooltip.mod_name")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("createvoidtank.tooltip.summary")
                .withStyle(ChatFormatting.GRAY));

        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable("createvoidtank.tooltip.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .withStyle(ChatFormatting.ITALIC));
            return;
        }

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("createvoidtank.tooltip.behaviours")
                .withStyle(ChatFormatting.GRAY));
        for (int i = 1; i <= 2; i++) {
            tooltipComponents.add(formatWithEmphasis(
                    Component.translatable("block.createvoidtank.void_tank.tooltip.behaviour" + i).getString(),
                    ChatFormatting.GRAY, ChatFormatting.GOLD));
        }

        if (!ModConfig.SPEC.isLoaded())
            return;

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("createvoidtank.tooltip.activation")
                .withStyle(ChatFormatting.GRAY));

        ActivationMode mode = ModConfig.ACTIVATION_MODE.get();

        switch (mode) {
            case ALWAYS_ACTIVE -> tooltipComponents.add(
                    Component.literal(" ")
                            .append(Component.translatable("createvoidtank.tooltip.activation.always_active")
                                    .withStyle(ChatFormatting.DARK_GREEN)));
            case REQUIRES_HEAT -> {
                tooltipComponents.add(
                        Component.literal(" ")
                                .append(Component.translatable("createvoidtank.tooltip.activation.requires_heat")
                                        .withStyle(ChatFormatting.GOLD)));
                MinimumHeatLevel heatLevel = ModConfig.MINIMUM_HEAT_LEVEL.get();
                String heatKey = switch (heatLevel) {
                    case PASSIVE -> "createvoidtank.tooltip.activation.heat.passive";
                    case BLAZE_BURNER -> "createvoidtank.tooltip.activation.heat.blaze_burner";
                    case SUPERHEATED -> "createvoidtank.tooltip.activation.heat.superheated";
                };
                tooltipComponents.add(
                        Component.literal(" ")
                                .append(Component.translatable("createvoidtank.tooltip.activation.heat_level",
                                                Component.translatable(heatKey))
                                        .withStyle(ChatFormatting.GRAY)));
            }
            case REQUIRES_REDSTONE -> tooltipComponents.add(
                    Component.literal(" ")
                            .append(Component.translatable("createvoidtank.tooltip.activation.requires_redstone")
                                    .withStyle(ChatFormatting.RED)));
        }
    }

    private static Component formatWithEmphasis(String text, ChatFormatting normalStyle, ChatFormatting emphasisStyle) {
        MutableComponent result = Component.literal(" ");
        String[] parts = text.split("_");
        boolean emphasis = false;
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Component.literal(part)
                        .withStyle(emphasis ? emphasisStyle : normalStyle));
            }
            emphasis = !emphasis;
        }
        return result;
    }
}
