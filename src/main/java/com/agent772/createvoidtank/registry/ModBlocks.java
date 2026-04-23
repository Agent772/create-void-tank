package com.agent772.createvoidtank.registry;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.content.VoidTankBlock;
import com.agent772.createvoidtank.content.VoidTankItem;
import com.agent772.createvoidtank.content.VoidTankModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

    static {
        CreateVoidTank.REGISTRATE.setCreativeTab(ModCreativeTabs.CREATIVE_TAB);
    }

    public static final BlockEntry<VoidTankBlock> VOID_TANK = CreateVoidTank.REGISTRATE
            .block("void_tank", VoidTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noOcclusion()
                    .sound(SoundType.METAL))
            .blockstate((ctx, prov) -> {
                // Blockstate JSON is hand-written; skip datagen generation
            })
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(CreateRegistrate.blockModel(() -> VoidTankModel::standard))
            .item(VoidTankItem::new)
            .model((ctx, prov) -> {
                // Item model JSON is hand-written; skip datagen generation
            })
            .build()
            .register();

    public static void init() {
    }
}
