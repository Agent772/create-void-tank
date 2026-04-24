package com.agent772.createvoidtank.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VoidTankScenes {

    public static void usage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("void_tank", "Voiding Fluids with the Void Tank");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        CreateSceneBuilder createScene = new CreateSceneBuilder(scene);

        BlockPos tankPos = util.grid().at(2, 2, 2);
        BlockPos leverPos = util.grid().at(3, 2, 2);
        BlockPos belowTank = util.grid().at(2, 1, 2);
        BlockPos pumpPos = util.grid().at(1, 2, 2);
        BlockPos pipePos = util.grid().at(0, 2, 2);

        // Section 1: Basic voiding behavior
        scene.idle(5);
        scene.world().showSection(util.select().position(tankPos), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .text("The Void Tank accepts fluids from any side and destroys them instantly")
                .pointAt(util.vector().centerOf(tankPos))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(90);

        scene.overlay().showText(60)
                .text("It never fills up — all fluids are voided on contact")
                .pointAt(util.vector().topOf(tankPos))
                .placeNearTarget();
        scene.idle(70);

        // Section 2: Pipe and pump demonstration
        BlockState pipeState = AllBlocks.FLUID_PIPE.get().getAxisState(Direction.Axis.X);
        scene.world().setBlock(pipePos, pipeState, false);

        BlockState pumpState = AllBlocks.MECHANICAL_PUMP.getDefaultState()
                .setValue(DirectionalBlock.FACING, Direction.EAST);
        scene.world().setBlock(pumpPos, pumpState, false);

        scene.world().showSection(util.select().fromTo(pipePos, pumpPos), Direction.EAST);
        createScene.world().setKineticSpeed(util.select().position(pumpPos), 32f);
        scene.idle(10);

        scene.overlay().showText(80)
                .text("Fluids can be pumped into the Void Tank through pipes")
                .pointAt(util.vector().centerOf(pumpPos))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(90);

        // Section 3: Heat activation mode
        scene.overlay().showText(80)
                .text("When configured to require heat, a heat source must be placed below")
                .pointAt(util.vector().centerOf(tankPos))
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(90);

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

        // Section 4: Redstone activation mode
        scene.overlay().showText(80)
                .text("When configured to require redstone, a signal must be provided")
                .pointAt(util.vector().centerOf(tankPos))
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(90);

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
