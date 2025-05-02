package com.thepigcat.paxelz.client;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.content.items.PaxelItem;
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
import net.neoforged.neoforge.event.level.BlockEvent;

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
                BlockPos blockPos = event.getTarget().getBlockPos();
                Vec3 cameraPos = event.getCamera().getPosition();
                Iterable<BlockPos> positions = PaxelItem.get3x3MiningArea(blockPos, event.getTarget().getDirection());
                for (BlockPos pos : positions) {
                    ((LevelRendererAccess) event.getLevelRenderer()).callRenderHitOutline(
                            event.getPoseStack(),
                            event.getMultiBufferSource().getBuffer(RenderType.lines()),
                            event.getCamera().getEntity(),
                            cameraPos.x(), cameraPos.y(), cameraPos.z(),
                            pos,
                            Minecraft.getInstance().level.getBlockState(pos)
                    );
                }
                event.setCanceled(true);
            }
        }
    }
}
