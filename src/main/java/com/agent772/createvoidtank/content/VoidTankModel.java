package com.agent772.createvoidtank.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.agent772.createvoidtank.registry.ModSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTModel;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class VoidTankModel extends CTModel {

    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();

    public static VoidTankModel standard(BakedModel originalModel) {
        return new VoidTankModel(originalModel);
    }

    private VoidTankModel(BakedModel originalModel) {
        super(originalModel, new VoidTankCTBehaviour(
                ModSpriteShifts.VOID_TANK,
                ModSpriteShifts.VOID_TANK_TOP,
                ModSpriteShifts.VOID_TANK_INNER));
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world,
                                                BlockPos pos, BlockState state, ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        for (Direction d : Iterate.horizontalDirections)
            cullData.setCulled(d, ConnectivityHandler.isConnected(world, pos, pos.relative(d)));
        return builder.with(CULL_PROPERTY, cullData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand,
                                    ModelData extraData, RenderType renderType) {
        if (side != null)
            return List.of();
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction d : Iterate.directions) {
            if (extraData.has(CULL_PROPERTY) && extraData.get(CULL_PROPERTY).isCulled(d))
                continue;
            quads.addAll(super.getQuads(state, d, rand, extraData, renderType));
        }
        quads.addAll(super.getQuads(state, null, rand, extraData, renderType));
        return quads;
    }

    static class CullData {
        boolean[] culledFaces;

        CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean culled) {
            if (face.getAxis().isVertical())
                return;
            culledFaces[face.get2DDataValue()] = culled;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis().isVertical())
                return false;
            return culledFaces[face.get2DDataValue()];
        }
    }
}
