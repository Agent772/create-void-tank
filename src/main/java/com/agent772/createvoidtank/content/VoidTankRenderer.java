package com.agent772.createvoidtank.content;

import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class VoidTankRenderer extends SafeBlockEntityRenderer<VoidTankBlockEntity> {

    public VoidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(VoidTankBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource bufferSource, int light, int overlay) {
        // No fluid rendering — the void tank consumes all fluids
    }
}
