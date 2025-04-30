package com.thepigcat.paxelz.api.upgrades;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface Upgrade {
    Upgrade EMPTY = new Upgrade() {
        @Override
        public Item upgradeItem() {
            return Items.AIR;
        }
    };

    default boolean isEmpty() {
        return this == EMPTY;
    }

    Item upgradeItem();

    default void onUpgradeAdded(ItemStack stack) {
    }

    default void onUpgradeRemoved(ItemStack stack) {
    }

}
