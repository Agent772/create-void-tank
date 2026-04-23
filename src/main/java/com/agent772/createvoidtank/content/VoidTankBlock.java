package com.agent772.createvoidtank.content;

import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;
import com.agent772.createvoidtank.registry.ModBlockEntities;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class VoidTankBlock extends Block implements IWrenchable, IBE<VoidTankBlockEntity> {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<FluidTankBlock.Shape> SHAPE = EnumProperty.create("shape", FluidTankBlock.Shape.class);

    public VoidTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, FluidTankBlock.Shape.WINDOW));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, SHAPE);
    }

    // --- Placement / Removal ---

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, VoidTankBlockEntity::updateConnectivity);

        BlockState newState = world.getBlockState(pos);
        if (state != newState && newState.getBlock() == this) {
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), oldState, newState, UPDATE_ALL_IMMEDIATE, 512);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            var be = world.getBlockEntity(pos);
            if (!(be instanceof VoidTankBlockEntity tankBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }

    // --- Interactions ---

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        VoidTankBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), level, pos);
        if (be == null)
            return ItemInteractionResult.FAIL;

        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (fluidHandler == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        FluidHelper.FluidExchange exchange = null;

        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack))
                return ItemInteractionResult.SUCCESS;
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        SoundEvent soundevent = null;
        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(0);
            soundevent = FluidHelper.getEmptySound(fluidInTank);
        }

        if (soundevent != null && !level.isClientSide) {
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, 1f);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    // --- Wrench ---

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), VoidTankBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    // --- Redstone ---

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return 0;
    }

    // --- IBE ---

    @Override
    public Class<VoidTankBlockEntity> getBlockEntityClass() {
        return VoidTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends VoidTankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.VOID_TANK.get();
    }
}
