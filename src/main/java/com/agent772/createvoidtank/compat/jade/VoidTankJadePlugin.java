package com.agent772.createvoidtank.compat.jade;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.content.VoidTankBlock;
import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;

import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class VoidTankJadePlugin implements IWailaPlugin {

    public static final ResourceLocation VOID_TANK =
            ResourceLocation.fromNamespaceAndPath(CreateVoidTank.MODID, "void_tank");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(VoidTankDataProvider.INSTANCE, VoidTankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(VoidTankComponentProvider.INSTANCE, VoidTankBlock.class);
    }
}
