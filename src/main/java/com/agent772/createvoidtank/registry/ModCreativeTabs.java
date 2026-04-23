package com.agent772.createvoidtank.registry;

import com.agent772.createvoidtank.CreateVoidTank;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateVoidTank.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB =
            REGISTER.register("main",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.createvoidtank"))
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .icon(() -> ModBlocks.VOID_TANK.asStack())
                            .displayItems((params, output) -> output.accept(ModBlocks.VOID_TANK.asStack()))
                            .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
