package com.thepigcat.paxelz.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
import com.thepigcat.paxelz.registries.PaxelzAttachments;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRendererMixin {
    @Inject(
            method = "renderBatched*",
            at = @At("HEAD"),
            cancellable = true
    )
    private void paxelz$renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack,
                                      VertexConsumer consumer, boolean checkSides, RandomSource random, ModelData modelData,
                                      RenderType renderType, CallbackInfo ci) {
        if (!ClientWallPhaseManager.WALL_PHASE_BLOCKS.isEmpty() && ClientWallPhaseManager.WALL_PHASE_BLOCKS.contains(pos)) {
            ci.cancel();
        }

    }
}
