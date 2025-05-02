package com.thepigcat.paxelz.client;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.mixins.LevelRendererAccess;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PaxelzClient.MODID, dist = Dist.CLIENT)
public final class PaxelzClient {
    public static final String MODID = "paxelz";

    public PaxelzClient(IEventBus eventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::renderHitbox);
    }

    private void renderHitbox(RenderHighlightEvent.Block event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack itemStack = player.getMainHandItem();
        if (!player.isShiftKeyDown() && itemStack.is(PaxelzTags.Items.PAXEL)) {
            if (itemStack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.AREA_MINING.get())) {
                Vec3 renderView = event.getCamera().getPosition();
                BlockPos blockPos = event.getTarget().getBlockPos();
                int minOffsetX = 0;
                int minOffsetY = 0;
                int minOffsetZ = 0;
                int maxOffsetX = 0;
                int maxOffsetY = 0;
                int maxOffsetZ = 0;
                switch (event.getTarget().getDirection().getAxis()) {
                    case X -> {
                        minOffsetY = -1;
                        minOffsetZ = -1;
                        maxOffsetX = 1;
                        maxOffsetY = 2;
                        maxOffsetZ = 2;
                    }
                    case Y -> {
                        minOffsetX = -1;
                        minOffsetZ = -1;
                        maxOffsetX = 2;
                        maxOffsetY = 1;
                        maxOffsetZ = 2;
                    }
                    case Z -> {
                        minOffsetX = -1;
                        minOffsetY = -1;
                        maxOffsetX = 2;
                        maxOffsetY = 2;
                        maxOffsetZ = 1;
                    }
                };
                AABB box = new AABB(
                        blockPos.getX() + minOffsetX - renderView.x(), blockPos.getY() + minOffsetY - renderView.y, blockPos.getZ() + minOffsetZ - renderView.z(),
                        blockPos.getX() + maxOffsetX - renderView.x(), blockPos.getY() + maxOffsetY - renderView.y, blockPos.getZ() + maxOffsetZ - renderView.z()
                );

                ((LevelRendererAccess) event.getLevelRenderer()).callRenderLineBox(
                        event.getPoseStack(),
                        event.getMultiBufferSource().getBuffer(RenderType.lines()),
                        box,
                        0.0F, 0.0F, 0.0F, 0.4F
                );
                event.setCanceled(true);
            }
        }
    }
}
