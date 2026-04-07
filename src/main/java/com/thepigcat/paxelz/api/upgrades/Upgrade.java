package com.thepigcat.paxelz.api.upgrades;

import com.thepigcat.paxelz.PaxelzRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface Upgrade {
    Upgrade EMPTY = () -> Items.AIR;

    default boolean isEmpty() {
        return this == EMPTY;
    }

    Item upgradeItem();

    default void onUpgradeAdded(ItemStack stack) {
    }

    default void onUpgradeRemoved(ItemStack stack) {
    }

    default boolean isIncompatible(Upgrade other) {
        return false;
    }

    default Component getDisplayName() {
        return Component.translatable("upgrade.paxelz." + PaxelzRegistries.UPGRADE.getKey(this).getPath());
    }
}
