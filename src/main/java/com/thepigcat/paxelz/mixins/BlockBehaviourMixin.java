package com.thepigcat.paxelz.mixins;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.registries.PaxelzAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void parcel$getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof Player player) {
            List<BlockPos> blocks = player.getData(PaxelzAttachments.PASS_THROUGH_BLOCKS).blocks();
            if (!blocks.isEmpty() && blocks.contains(pos)) {
                Paxelz.LOGGER.debug("No COLISSION, pos: {}", pos);
                cir.setReturnValue(Shapes.empty());
            }
        }
    }
}
