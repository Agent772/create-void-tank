package com.agent772.createvoidtank.content;

import com.agent772.createvoidtank.config.ModConfig;
import com.agent772.createvoidtank.config.ModConfig.ActivationMode;
import com.agent772.createvoidtank.config.ModConfig.MinimumHeatLevel;
import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;
import com.agent772.createvoidtank.registry.ModBlockEntities;
import com.simibubi.create.api.connectivity.ConnectivityHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        for (int i = 1; i <= 3; i++) {
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
                    case SMOULDERING -> "createvoidtank.tooltip.activation.heat.smouldering";
                    case KINDLED -> "createvoidtank.tooltip.activation.heat.kindled";
                    case SEETHING -> "createvoidtank.tooltip.activation.heat.seething";
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

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, Player player,
                                                 ItemStack itemStack, BlockState blockState) {
        MinecraftServer server = level.getServer();
        if (server == null)
            return false;
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        if (!face.getAxis().isVertical())
            return;
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!(placedOnState.getBlock() instanceof VoidTankBlock))
            return;

        VoidTankBlockEntity tankAt = ConnectivityHandler.partAt(
                ModBlockEntities.VOID_TANK.get(), world, placedOnPos);
        if (tankAt == null)
            return;
        VoidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.getWidth();
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN
                ? controllerBE.getBlockPos().below()
                : controllerBE.getBlockPos().above(controllerBE.getHeight());

        if (startPos.getY() != pos.getY())
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (blockState.getBlock() instanceof VoidTankBlock)
                    continue;
                if (!blockState.canBeReplaced())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (blockState.getBlock() instanceof VoidTankBlock)
                    continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
                super.place(context);
            }
        }
    }
}
