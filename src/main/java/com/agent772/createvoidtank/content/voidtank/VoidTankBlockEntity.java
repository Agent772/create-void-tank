package com.agent772.createvoidtank.content.voidtank;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.agent772.createvoidtank.content.VoidFluidHandler;
import com.agent772.createvoidtank.content.VoidTankBlock;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public class VoidTankBlockEntity extends SmartBlockEntity
        implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {

    private static final int MAX_SIZE = 3;
    private static final int MAX_HEIGHT = 16;
    private static final int CAPACITY_MULTIPLIER = 64000;

    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean window;
    protected int width;
    protected int height;

    protected SmartFluidTank tankInventory;

    public VoidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = new SmartFluidTank(CAPACITY_MULTIPLIER, f -> {});
        updateConnectivity = false;
        window = true;
        width = 1;
        height = 1;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    // --- Tick / Update ---

    @Override
    public void tick() {
        super.tick();

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    // --- Multi-block: IMultiBlockEntityContainer ---

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.equals(controller);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidTankBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof VoidTankBlockEntity voidTank)
            return voidTank;
        return null;
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        invalidateCapabilities();
        setChanged();
        sendData();
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        BlockState state = getBlockState();
        if (state.getBlock() instanceof VoidTankBlock) {
            state = state.setValue(VoidTankBlock.BOTTOM, true);
            state = state.setValue(VoidTankBlock.TOP, true);
            state = state.setValue(VoidTankBlock.SHAPE, window ? FluidTankBlock.Shape.WINDOW : FluidTankBlock.Shape.PLAIN);
            getLevel().setBlock(worldPosition, state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }

        setChanged();
        sendData();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof VoidTankBlock) {
            state = state.setValue(VoidTankBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(VoidTankBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        }
        if (isController())
            setWindows(window);
        setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean b)
            window = b;
    }

    @Nullable
    @Override
    public Object getExtraData() {
        return window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean windows) {
            windows |= window;
            return windows;
        }
        return data;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return MAX_HEIGHT;
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    // --- Fluid: IMultiBlockEntityContainer.Fluid ---

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return CAPACITY_MULTIPLIER;
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        tankInventory.setCapacity(blocks * CAPACITY_MULTIPLIER);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return tankInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return FluidStack.EMPTY;
    }

    public SmartFluidTank getTankInventory() {
        return tankInventory;
    }

    // --- Window toggling ---

    public void toggleWindows() {
        VoidTankBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.setWindows(!be.window);
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!(blockState.getBlock() instanceof VoidTankBlock))
                        continue;

                    FluidTankBlock.Shape shape = FluidTankBlock.Shape.PLAIN;
                    if (window) {
                        if (width == 1)
                            shape = FluidTankBlock.Shape.WINDOW;
                        if (width == 2)
                            shape = xOffset == 0
                                    ? zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SW
                                    : zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SE;
                        if (width == 3 && Math.abs(Math.abs(xOffset) - Math.abs(zOffset)) == 1)
                            shape = FluidTankBlock.Shape.WINDOW;
                    }

                    level.setBlock(pos, blockState.setValue(VoidTankBlock.SHAPE, shape),
                            Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
                    level.getChunkSource().getLightEngine().checkBlock(pos);
                }
            }
        }
    }

    public boolean isWindow() {
        return window;
    }

    // --- Goggle tooltip ---

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(" ")
                .append(Component.translatable("createvoidtank.goggle.voiding")
                        .withStyle(ChatFormatting.DARK_PURPLE)));
        return true;
    }

    // --- Persistence ---

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));

        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));

        if (isController()) {
            compound.putBoolean("Window", window);
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound, "LastKnownPos").orElse(null);

        controller = null;
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound, "Controller").orElse(null);

        if (isController()) {
            window = compound.getBoolean("Window");
            width = compound.getInt("Size");
            height = compound.getInt("Height");
        }

        if (!clientPacket)
            return;

        boolean changeOfController = controllerBefore == null
                ? controller != null
                : !controllerBefore.equals(controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            invalidateRenderBoundingBox();
        }
    }
}
