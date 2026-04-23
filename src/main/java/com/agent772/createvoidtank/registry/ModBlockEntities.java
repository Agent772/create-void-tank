package com.agent772.createvoidtank.registry;

import com.agent772.createvoidtank.content.VoidFluidHandler;
import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import com.agent772.createvoidtank.CreateVoidTank;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModBlockEntities {

    public static final BlockEntityEntry<VoidTankBlockEntity> VOID_TANK = CreateVoidTank.REGISTRATE
            .blockEntity("void_tank", VoidTankBlockEntity::new)
            .validBlocks(ModBlocks.VOID_TANK)
            .register();

    public static void init() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                VOID_TANK.get(),
                (be, context) -> {
                    VoidTankBlockEntity controller = be.getControllerBE();
                    if (controller == null)
                        return null;
                    return VoidFluidHandler.INSTANCE;
                }
        );
    }
}
