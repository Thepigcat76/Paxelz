package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class StorageLinkUpgrade implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_STORAGE_LINK.get();
    }

    @Override
    public void onUpgradeRemoved(ItemStack stack) {
        stack.set(PaxelzComponents.STORAGE_LINK, Optional.empty());
    }

}
