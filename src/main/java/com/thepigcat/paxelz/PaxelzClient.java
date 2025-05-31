package com.thepigcat.paxelz;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thepigcat.paxelz.client.PaxelzRenderTypes;
import com.thepigcat.paxelz.content.components.SpelunkerResultComponent;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;

@Mod(Paxelz.MODID)
public final class PaxelzClient {
    public PaxelzClient(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(this::renderOres);
    }

    private void renderOres(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(PaxelzTags.Items.PAXEL)) return;

        SpelunkerResultComponent spelunkerResult = stack.get(PaxelzComponents.SPELUNKER_RESULT);
        if (spelunkerResult == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(PaxelzRenderTypes.TEST_RENDER_TYPE);

        for (BlockPos pos : spelunkerResult.detectedOres()) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());
            poseStack.translate(-0.1, -0.1, -0.1);  // slight offset
            poseStack.scale(1.2f, 1.2f, 1.2f);      // slight scale

            renderCube(consumer, poseStack.last().pose(), 0, 255, 0, 80);
            poseStack.popPose();
        }

        mc.renderBuffers().bufferSource().endBatch(PaxelzRenderTypes.TEST_RENDER_TYPE);
    }

    private static void renderCube(VertexConsumer consumer, Matrix4f matrix, int r, int g, int b, int a) {
        consumer.addVertex(matrix, 0, 1, 0).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 0, 1, 1).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 1, 1, 1).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 1, 1, 0).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 1, 0, 0).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 1, 0, 1).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 0, 0, 1).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 0, 0, 1).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 1, 0, 1).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 1, 1, 1).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 0, 1, 1).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 0, 1, 0).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 1, 1, 0).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 1, 0, 0).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 0, 0, 1).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 0, 1, 1).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 0, 1, 0).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1, 0, 0).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1, 1, 0).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1, 1, 1).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1, 0, 1).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
    }

}
