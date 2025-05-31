package com.thepigcat.paxelz.utils;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class PaxelUtils {
    public static boolean canMine(Level level, BlockPos pos, BlockState state) {
        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE)) && level.getBlockEntity(pos) == null;
    }

    public static void handleBlockDrops(Player player, ItemStack toolStack, ServerLevel level, List<ItemStack> drops, BlockPos pos1, BlockState state, int droppedExperience) {
        Optional<BlockPos> _linkedPos = toolStack.get(PaxelzComponents.STORAGE_LINK);
        if (_linkedPos.isPresent()) {
            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, _linkedPos.get(), null);
            if (itemHandler != null) {
                List<ItemStack> remainders = new ArrayList<>();
                for (ItemStack stack : drops) {
                    ItemStack remainder = ItemHandlerHelper.insertItem(itemHandler, stack, false);
                    remainders.add(remainder);
                }
                for (ItemStack remainder : remainders) {
                    Block.popResource(player.level(), pos1, remainder);
                }
                if (level instanceof ServerLevel serverLevel) {
                    state.getBlock().popExperience(serverLevel, pos1, droppedExperience);
                }
            }
        }
    }

    public static boolean hasLinkedStorage(ItemStack stack) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.STORAGE_LINK.get())) {
            Optional<BlockPos> blockPos = stack.get(PaxelzComponents.STORAGE_LINK);
            return blockPos.isPresent();
        }
        return false;
    }

    public static boolean hasUpgrade(ItemStack stack, Supplier<? extends Upgrade> upgrade) {
        return stack.getItem() instanceof PaxelItem && stack.get(PaxelzComponents.UPGRADES).hasUpgrade(upgrade.get());
    }

    public static boolean canBeDamaged(ItemStack itemStack) {
        if (hasUpgrade(itemStack, PaxelzUpgrades.ENERGY_STORAGE)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            return energyStorage.getEnergyStored() >= PaxelItem.ENERGY_USAGE;
        } else {
            return itemStack.getMaxDamage() - itemStack.getDamageValue() >= 1;
        }
    }

    public static void damageItem(Player player1, ItemStack itemStack) {
        if (hasUpgrade(itemStack, PaxelzUpgrades.ENERGY_STORAGE)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            energyStorage.extractEnergy(PaxelItem.ENERGY_USAGE, false);
        } else {
            itemStack.hurtAndBreak(1, player1, EquipmentSlot.MAINHAND);
        }
    }

}
