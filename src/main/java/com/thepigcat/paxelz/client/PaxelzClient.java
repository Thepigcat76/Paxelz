package com.thepigcat.paxelz.client;

import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.WallPhaseManager;
import com.thepigcat.paxelz.content.attachments.PassThroughBlocksAttachment;
import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.mixins.LevelRendererAccess;
import com.thepigcat.paxelz.registries.PaxelzAttachments;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

@Mod(value = PaxelzClient.MODID, dist = Dist.CLIENT)
public final class PaxelzClient {
    public static final String MODID = "paxelz";

    public PaxelzClient(IEventBus eventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::renderHitbox);
        NeoForge.EVENT_BUS.addListener(this::onDimensionChanged);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::renderBlockOverlay);
        NeoForge.EVENT_BUS.addListener(this::playerTick);
    }

    private void renderBlockOverlay(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.BLOCK) return;

        if (ClientWallPhaseManager.WALL_PHASE_BLOCKS.contains(event.getBlockPos())) {
            event.setCanceled(true);
        }
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

    private void playerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            if (ClientWallPhaseManager.WALL_PHASE_TICKER > 0) {
                ClientWallPhaseManager.WALL_PHASE_TICKER--;
                if (ClientWallPhaseManager.WALL_PHASE_TICKER == 0) {
                    Level level = event.getEntity().level();
                    Collection<BlockPos> blocks = new ArrayList<>(ClientWallPhaseManager.WALL_PHASE_BLOCKS);
                    ClientWallPhaseManager.WALL_PHASE_BLOCKS.clear();
                    setBlocks(level, blocks);
                }
            }
        } else {
            UUID uuid = event.getEntity().getUUID();
            Integer tickerTick = WallPhaseManager.WALL_PHASE_TICKER.get(uuid);
            if (tickerTick != null && tickerTick > 0) {
                WallPhaseManager.WALL_PHASE_TICKER.put(uuid, tickerTick - 1);
                if (tickerTick - 1 == 0) {
                    Level level = event.getEntity().level();
                    List<BlockPos> blocks = new ArrayList<>(event.getEntity().getData(PaxelzAttachments.PASS_THROUGH_BLOCKS.get()).blocks());
                    event.getEntity().setData(PaxelzAttachments.PASS_THROUGH_BLOCKS.get(), new PassThroughBlocksAttachment(Collections.emptyList()));
                    setBlocks(level, blocks);
                }
            }
        }
    }

    private static void setBlocks(Level level, Collection<BlockPos> blocks) {
        for (BlockPos pos : blocks) {
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
            level.setBlocksDirty(pos, level.getBlockState(pos), level.getBlockState(pos));
            level.getChunkAt(pos).setUnsaved(true);
        }
    }

    private void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        ClientWallPhaseManager.WALL_PHASE_BLOCKS.clear();
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ClientWallPhaseManager.WALL_PHASE_BLOCKS.clear();
    }

}
