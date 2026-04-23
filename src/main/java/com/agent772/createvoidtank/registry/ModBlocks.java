package com.agent772.createvoidtank.registry;

import com.agent772.createvoidtank.CreateVoidTank;
import com.agent772.createvoidtank.content.VoidTankBlock;
import com.agent772.createvoidtank.content.VoidTankItem;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

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
            .item(VoidTankItem::new)
            .build()
            .register();

    public static void init() {
    }
}
