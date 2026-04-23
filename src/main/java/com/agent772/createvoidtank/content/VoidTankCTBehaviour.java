package com.agent772.createvoidtank.content;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class VoidTankCTBehaviour extends HorizontalCTBehaviour {

    private CTSpriteShiftEntry innerShift;

    public VoidTankCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift,
                               CTSpriteShiftEntry innerShift) {
        super(layerShift, topShift);
        this.innerShift = innerShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        if (sprite != null && direction.getAxis() == Direction.Axis.Y
                && innerShift.getOriginal() == sprite)
            return innerShift;
        return super.getShift(state, direction, sprite);
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader,
                              BlockPos pos, BlockPos otherPos, Direction face) {
        return state.getBlock() == other.getBlock()
                && ConnectivityHandler.isConnected(reader, pos, otherPos);
    }
}
