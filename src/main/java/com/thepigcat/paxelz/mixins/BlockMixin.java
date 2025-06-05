package com.thepigcat.paxelz.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
            method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void checkFacadeOcclusion(BlockState state, BlockGetter level, BlockPos pos, Direction side, BlockPos sidePos, CallbackInfoReturnable<Boolean> ci, @Local(index = 5) BlockState bState) {
        if (state == null || level == null || pos == null || side == null || sidePos == null || ci == null) {
            return;
        }

        if (FMLEnvironment.dist.isClient() && ClientWallPhaseManager.WALL_PHASE_BLOCKS.contains(pos)) {
            ci.setReturnValue(false);
        }
    }

    @ModifyVariable(
            method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), index = 5
    )
    private static BlockState useFacadeAsNeighbor(BlockState value, BlockState state, BlockGetter level, BlockPos pos, Direction side, BlockPos sidePos) {
        return value;
    }

}
