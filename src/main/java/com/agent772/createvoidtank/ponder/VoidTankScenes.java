package com.agent772.createvoidtank.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class VoidTankScenes {

    public static void usage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("void_tank", "Voiding Fluids with the Void Tank");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos tankPos = util.grid().at(2, 1, 2);
        BlockPos leverPos = util.grid().at(4, 1, 2);
        BlockPos belowTank = util.grid().at(2, 0, 2);

        // Section 1: Basic voiding behavior
        scene.idle(5);
        scene.world().showSection(util.select().position(tankPos), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .text("The Void Tank accepts fluids from any side and destroys them instantly")
                .pointAt(util.vector().centerOf(tankPos))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(50);

        scene.overlay().showText(60)
                .text("It never fills up — all fluids are voided on contact")
                .pointAt(util.vector().topOf(tankPos))
                .placeNearTarget();
        scene.idle(70);

        // Section 2: Heat activation mode
        scene.overlay().showText(80)
                .text("When configured to require heat, a heat source must be placed below")
                .pointAt(util.vector().centerOf(tankPos))
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(40);

        scene.world().setBlock(belowTank,
                AllBlocks.BLAZE_BURNER.getDefaultState()
                        .setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED),
                false);
        scene.world().showSection(util.select().position(belowTank), Direction.UP);
        scene.idle(10);

        scene.effects().indicateSuccess(tankPos);
        scene.overlay().showText(60)
                .text("A lit Blaze Burner provides sufficient heat")
                .pointAt(util.vector().blockSurface(belowTank, Direction.WEST))
                .colored(PonderPalette.GREEN)
                .placeNearTarget();
        scene.idle(70);

        // Reset heat section
        scene.world().hideSection(util.select().position(belowTank), Direction.DOWN);
        scene.idle(5);
        scene.world().restoreBlocks(util.select().position(belowTank));
        scene.idle(10);

        // Section 3: Redstone activation mode
        scene.overlay().showText(80)
                .text("When configured to require redstone, a signal must be provided")
                .pointAt(util.vector().centerOf(tankPos))
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(40);

        scene.world().showSection(util.select().position(leverPos), Direction.DOWN);
        scene.idle(10);

        scene.world().modifyBlock(leverPos, s -> s.setValue(LeverBlock.POWERED, true), false);
        scene.effects().indicateRedstone(leverPos);
        scene.idle(5);
        scene.effects().indicateSuccess(tankPos);

        scene.overlay().showText(60)
                .text("Any redstone signal will activate the tank")
                .pointAt(util.vector().blockSurface(leverPos, Direction.UP))
                .colored(PonderPalette.GREEN)
                .placeNearTarget();
        scene.idle(70);

        scene.markAsFinished();
    }
}
