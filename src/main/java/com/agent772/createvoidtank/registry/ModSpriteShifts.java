package com.agent772.createvoidtank.registry;

import com.agent772.createvoidtank.CreateVoidTank;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;

import net.minecraft.resources.ResourceLocation;

public class ModSpriteShifts {

    public static final CTSpriteShiftEntry VOID_TANK = getCT("void_tank");
    public static final CTSpriteShiftEntry VOID_TANK_TOP = getCT("void_tank_top");
    public static final CTSpriteShiftEntry VOID_TANK_INNER = getCT("void_tank_inner");

    private static CTSpriteShiftEntry getCT(String name) {
        return CTSpriteShifter.getCT(
                AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath(CreateVoidTank.MODID, "block/" + name),
                ResourceLocation.fromNamespaceAndPath(CreateVoidTank.MODID, "block/" + name));
    }

    public static void init() {
    }
}
