package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.world.item.Item;

public class StorageLinkUpgrade implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_STORAGE_LINK.get();
    }
}
