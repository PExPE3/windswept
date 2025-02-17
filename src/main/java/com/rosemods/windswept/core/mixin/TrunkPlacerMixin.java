package com.rosemods.windswept.core.mixin;

import com.rosemods.windswept.core.WindsweptConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {

    @Inject(method = "setDirtAt", at = @At("HEAD"), cancellable = true)
    private static void setDirtAt(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> states, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo info) {
        if (!(((LevelReader) level).getBlockState(pos).onTreeGrow((LevelReader) level, states, random, pos, config)) && (config.forceDirt
                || ! level.isStateAtPosition(pos, (state) -> Feature.isDirt(state) && !state.is(Blocks.GRASS_BLOCK) && !state.is(Blocks.MYCELIUM)))) {
            states.accept(pos, config.dirtProvider.getState(random, pos));

            if (((LevelReader) level).isEmptyBlock(pos.below()) && WindsweptConfig.COMMON.roots.get())
                states.accept(pos.below(), Blocks.HANGING_ROOTS.defaultBlockState());
        }

        info.cancel();
    }
}
