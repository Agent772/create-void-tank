package com.agent772.createvoidtank.ponder;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.registry.ModBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreateVoidTank.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> scenes =
                helper.withKeyFunction(ItemProviderEntry::getId);

        scenes.forComponents(ModBlocks.VOID_TANK)
                .addStoryBoard("void_tank/usage", VoidTankScenes::usage);
    }
}
