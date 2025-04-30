package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.world.item.Item;

public record AreaMiningUpgrade(int area) implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_AREA_MINING.get();
    }
}
