package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.components.SpelunkerResultComponent;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class SpelunkerUpgrade implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_SPELUNKER.get();
    }

    public void performSpelunking(Player player, ItemStack stack) {
        Level level = player.level();
        BlockPos playerPos = player.getOnPos();
        int bottomY = level.getMinBuildHeight();

        AABB box = new AABB(playerPos.offset(-8, 0, -8).getCenter(), new BlockPos(playerPos.getX() + 8, bottomY, playerPos.getZ() + 8).getCenter());
        List<BlockPos> ores = BlockPos.betweenClosedStream(box).filter(pos -> level.getBlockState(pos).is(Tags.Blocks.ORES_IN_GROUND_STONE)).map(BlockPos::immutable).toList();
        stack.set(PaxelzComponents.SPELUNKER_RESULT.get(), new SpelunkerResultComponent(ores));
    }
}
