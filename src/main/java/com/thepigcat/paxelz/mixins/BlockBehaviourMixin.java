package com.thepigcat.paxelz.mixins;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
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
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void paxelz$getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof Player player) {
            Collection<BlockPos> blocks;
            if (!FMLEnvironment.dist.isClient()) {
                blocks = player.getData(PaxelzAttachments.PASS_THROUGH_BLOCKS).blocks();
            } else {
                blocks = ClientWallPhaseManager.WALL_PHASE_BLOCKS;
            }
            if (!blocks.isEmpty() && blocks.contains(pos)) {
                cir.setReturnValue(Shapes.empty());
            }
        }
    }

}
