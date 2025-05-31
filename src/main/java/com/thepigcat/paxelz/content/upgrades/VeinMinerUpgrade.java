package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzItems;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import com.thepigcat.paxelz.utils.PaxelUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class VeinMinerUpgrade implements Upgrade {
    // FROM FTB Ultimine :3, thanks to the FTB Team
    private static final List<BlockPos> NEIGHBOR_POSITIONS = new ArrayList<>(26);
    // all blocks in 5x5 square around block's Y-level, plus blocks directly above & below
    private static final List<BlockPos> NEIGHBOR_POSITIONS_PLANT = new ArrayList<>(26);

    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_VEIN_MINER.get();
    }

    public void veinMine(ItemStack stack, Player player, BlockPos pos) {
        veinMine(player, pos, stack, PaxelUtils::canBeDamaged, PaxelUtils::damageItem);
    }

    private void veinMine(Player player, BlockPos pos, ItemStack stack, Predicate<ItemStack> canBeDamagedFunction, BiConsumer<Player, ItemStack> damageFunction) {
        Set<BlockPos> known = new ObjectArraySet<>();
        Level level = player.level();
        boolean dropBlock = !PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.STORAGE_LINK) || !PaxelUtils.hasLinkedStorage(stack);
        if (level instanceof ServerLevel serverLevel) {
            walk(level, pos, known, checkCrop(level, pos));
            for (BlockPos knownPos : known) {
                BlockState blockState = level.getBlockState(knownPos);
                if (canBeDamagedFunction.test(stack) && PaxelUtils.canMine(level, knownPos, blockState)) {

                    level.destroyBlock(knownPos, dropBlock);

                    if (!dropBlock) {
                        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                        int droppedExp = EnchantmentHelper.processBlockExperience(serverLevel, stack, blockState.getExpDrop(level, pos, blockEntity, player, stack));
                        PaxelUtils.handleBlockDrops(player, stack, serverLevel, Block.getDrops(blockState, serverLevel, pos, blockEntity), pos, blockState, droppedExp);

                        if (droppedExp > 0) {
                            blockState.getBlock().popExperience(serverLevel, pos, droppedExp);
                        }
                    }
                    damageFunction.accept(player, stack);
                }
            }
        }
    }

    private void walk(Level level, BlockPos pos, Set<BlockPos> known, boolean cropLike) {
        Set<BlockPos> traversed = new HashSet<>();
        Deque<BlockPos> openSet = new ArrayDeque<>();
        openSet.add(pos);
        traversed.add(pos);

        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();

            if (check(level, pos, ptr) && known.add(ptr)) {
                if (known.size() >= 64) {
                    return;
                }

                for (BlockPos side : cropLike ? NEIGHBOR_POSITIONS_PLANT : NEIGHBOR_POSITIONS) {
                    BlockPos offset = ptr.offset(side);

                    if (traversed.add(offset)) {
                        openSet.add(offset);
                    }
                }
            }
        }
    }

    private boolean checkCrop(Level level, BlockPos originalPos) {
        BlockState state = level.getBlockState(originalPos);
        return state.is(BlockTags.CROPS) || state.is(Blocks.COCOA) || state.is(Blocks.SUGAR_CANE) || state.is(Blocks.NETHER_WART);
    }

    private boolean check(Level level, BlockPos originalPos, BlockPos pos) {
        return level.getBlockState(originalPos).is(level.getBlockState(pos).getBlock());
    }

    static {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) NEIGHBOR_POSITIONS.add(new BlockPos(x, y, z));
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x != 0 || z != 0) NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(x, 0, z));
            }
        }
        NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(0, 1, 0));
        NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(0, -1, 0));
    }

}
