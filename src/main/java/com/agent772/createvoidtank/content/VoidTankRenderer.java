package com.agent772.createvoidtank.content;

import com.agent772.createvoidtank.content.voidtank.VoidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

public class VoidTankRenderer extends SafeBlockEntityRenderer<VoidTankBlockEntity> {

    public VoidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(VoidTankBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;
        // No fluid rendering — the void tank consumes all fluids
    }

    @Override
    public boolean shouldRenderOffScreen(VoidTankBlockEntity be) {
        return be.isController();
    }

    @Override
    public AABB getRenderBoundingBox(VoidTankBlockEntity be) {
        if (be.isController()) {
            return new AABB(be.getBlockPos())
                    .expandTowards(be.getWidth() - 1, be.getHeight() - 1, be.getWidth() - 1);
        }
        return super.getRenderBoundingBox(be);
    }
}
