package com.agent772.createvoidtank;

import org.slf4j.Logger;

import com.agent772.createvoidtank.registry.ModBlockEntities;
import com.agent772.createvoidtank.registry.ModBlocks;
import com.agent772.createvoidtank.registry.ModCreativeTabs;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CreateVoidTank.MODID)
public class CreateVoidTank {
    public static final String MODID = "createvoidtank";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public CreateVoidTank(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModCreativeTabs.register(modEventBus);
        ModBlocks.init();
        ModBlockEntities.init();

        modEventBus.addListener(ModBlockEntities::registerCapabilities);

        LOGGER.info("Create Void Tank initialized!");
    }
}
