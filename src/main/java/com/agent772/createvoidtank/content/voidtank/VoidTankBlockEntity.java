package com.agent772.createvoidtank.content.voidtank;

import java.util.List;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.config.ModConfig;
import com.agent772.createvoidtank.config.ModConfig.ActivationMode;
import com.agent772.createvoidtank.config.ModConfig.MinimumHeatLevel;
import com.agent772.createvoidtank.content.VoidFluidHandler;
import com.agent772.createvoidtank.content.VoidTankBlock;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.createmod.catnip.lang.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;

public class VoidTankBlockEntity extends SmartBlockEntity
        implements IHaveGoggleInformation {

    private static final int TANK_CAPACITY = 1000;
    private static final int ACTIVATION_CHECK_INTERVAL = 20;

    protected SmartFluidTank tankInventory;
    protected VoidFluidHandler fluidHandler;
    protected boolean cachedActive;
    protected int activationCheckCooldown;
    protected FluidStack lastVoidedFluid = FluidStack.EMPTY;
    protected boolean window = true;

    public VoidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = new SmartFluidTank(TANK_CAPACITY, f -> {});
        fluidHandler = new VoidFluidHandler(() -> cachedActive, fluid -> {
            if (!FluidStack.isSameFluidSameComponents(lastVoidedFluid, fluid)) {
                lastVoidedFluid = fluid.copyWithAmount(1);
                setChanged();
                sendData();
            }
        });
        cachedActive = true;
        activationCheckCooldown = 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
    }

    // --- Activation ---

    public VoidFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    public boolean isActive() {
        return cachedActive;
    }

    public FluidStack getLastVoidedFluid() {
        return lastVoidedFluid;
    }

    private boolean evaluateActivation() {
        if (level == null)
            return true;

        ActivationMode mode = ModConfig.ACTIVATION_MODE.get();
        return switch (mode) {
            case ALWAYS_ACTIVE -> true;
            case REQUIRES_HEAT -> checkHeat();
            case REQUIRES_REDSTONE -> checkRedstone();
        };
    }

    private boolean checkHeat() {
        MinimumHeatLevel required = ModConfig.MINIMUM_HEAT_LEVEL.get();
        int requiredOrdinal = required.ordinal();

        BlockPos belowPos = worldPosition.below();
        int detectedLevel = detectHeatLevel(belowPos);
        return detectedLevel >= requiredOrdinal;
    }

    private int detectHeatLevel(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlazeBurnerBlock) {
            BlazeBurnerBlock.HeatLevel heat = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            return switch (heat) {
                case SMOULDERING -> MinimumHeatLevel.SMOULDERING.ordinal();
                case KINDLED -> MinimumHeatLevel.KINDLED.ordinal();
                case SEETHING -> MinimumHeatLevel.SEETHING.ordinal();
                default -> -1;
            };
        }

        if (block == Blocks.FIRE || block == Blocks.SOUL_FIRE
                || block == Blocks.LAVA || block == Blocks.MAGMA_BLOCK) {
            return MinimumHeatLevel.SMOULDERING.ordinal();
        }

        if (block instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
            return MinimumHeatLevel.SMOULDERING.ordinal();
        }

        return -1;
    }

    private boolean checkRedstone() {
        return level.hasNeighborSignal(worldPosition);
    }

    // --- Tick / Update ---

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide) {
            if (activationCheckCooldown <= 0) {
                cachedActive = evaluateActivation();
                activationCheckCooldown = ACTIVATION_CHECK_INTERVAL;
            } else {
                activationCheckCooldown--;
            }
        }
    }

    // --- Window toggling ---

    public void toggleWindows() {
        if (level == null || level.isClientSide)
            return;
        window = !window;
        FluidTankBlock.Shape shape = window
                ? FluidTankBlock.Shape.WINDOW
                : FluidTankBlock.Shape.PLAIN;
        level.setBlock(worldPosition,
                getBlockState().setValue(VoidTankBlock.SHAPE, shape),
                Block.UPDATE_ALL);
        setChanged();
        sendData();
    }

    // --- Goggle tooltip ---

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ActivationMode mode = ModConfig.ACTIVATION_MODE.get();

        if (mode == ActivationMode.ALWAYS_ACTIVE || isActive()) {
            Lang.builder(CreateVoidTank.MODID)
                    .translate("goggle.voiding")
                    .style(ChatFormatting.DARK_PURPLE)
                    .forGoggles(tooltip);
            if (!lastVoidedFluid.isEmpty()) {
                Lang.builder(CreateVoidTank.MODID)
                        .translate("goggle.voiding_fluid")
                        .add(Lang.builder(CreateVoidTank.MODID).add(lastVoidedFluid.getHoverName().copy()))
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip, 1);
            }
        } else {
            String key = switch (mode) {
                case REQUIRES_HEAT -> "goggle.inactive.heat";
                case REQUIRES_REDSTONE -> "goggle.inactive.redstone";
                default -> "goggle.inactive";
            };
            Lang.builder(CreateVoidTank.MODID)
                    .translate(key)
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip);
        }
        return true;
    }

    // --- Persistence ---

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Window", window);
        if (!lastVoidedFluid.isEmpty()) {
            compound.put("LastVoidedFluid", lastVoidedFluid.save(registries));
        }

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        window = !compound.contains("Window") || compound.getBoolean("Window");

        if (compound.contains("LastVoidedFluid")) {
            lastVoidedFluid = FluidStack.parse(registries, compound.getCompound("LastVoidedFluid"))
                    .orElse(FluidStack.EMPTY);
        } else {
            lastVoidedFluid = FluidStack.EMPTY;
        }
    }
}
