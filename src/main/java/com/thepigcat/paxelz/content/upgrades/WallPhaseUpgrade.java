package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.world.item.Item;

public class WallPhaseUpgrade implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_SPELUNKER.get();
    }
}
