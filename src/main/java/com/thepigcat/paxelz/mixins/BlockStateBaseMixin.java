package com.thepigcat.paxelz.mixins;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void paxelz$getOcclusionShape(BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (paxelz$isPosPartOfWallPhase(pos)) {
            Paxelz.LOGGER.debug("deez, pos: {}", pos);
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void paxelz$getCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (paxelz$isPosPartOfWallPhase(pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
    private void paxelz$getVisualShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (paxelz$isPosPartOfWallPhase(pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;  ", at = @At("HEAD"), cancellable = true)
    private void paxelz$getShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (paxelz$isPosPartOfWallPhase(pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(
            method = "isSolidRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private void paxelz$isSolidRender(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (!FMLEnvironment.dist.isClient()) return;
        if (paxelz$isPosPartOfWallPhase(blockPos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getFaceOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void paxelz$getFaceOcclusionShape(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<VoxelShape> cir) {
        if (paxelz$isPosPartOfWallPhase(pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Unique
    private static boolean paxelz$isPosPartOfWallPhase(BlockPos pos) {
        return !ClientWallPhaseManager.WALL_PHASE_BLOCKS.isEmpty() && ClientWallPhaseManager.WALL_PHASE_BLOCKS.contains(pos);
    }

}
