package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class EnergyStorageUpgrade implements Upgrade {
    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_ENERGY_STORAGE.get();
    }

    @Override
    public void onUpgradeAdded(ItemStack stack) {
        int damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
        stack.set(PaxelzComponents.PREVIOUS_DAMAGE, damage);
        stack.remove(DataComponents.MAX_DAMAGE);

        if (stack.getItem() instanceof PaxelItem paxelItem) {
            int energyStored = stack.getOrDefault(PaxelzComponents.ENERGY_STORAGE, 0);
            if (energyStored < PaxelItem.ENERGY_USAGE) {
                PaxelItem.removeToolsAndAttributes(stack);
            } else {
                ToolMaterial material = paxelItem.getMaterial();
                PaxelItem.addToolsAndAttributes(material, stack);
            }
        }
    }

    @Override
    public void onUpgradeRemoved(ItemStack stack) {
        if (stack.getItem() instanceof PaxelItem paxelItem) {
            int maxDamage = paxelItem.getMaterial().durability();
            stack.set(DataComponents.DAMAGE, stack.remove(PaxelzComponents.PREVIOUS_DAMAGE));
            stack.set(PaxelzComponents.PREVIOUS_DAMAGE, 0);
            stack.set(DataComponents.MAX_DAMAGE, maxDamage);
            PaxelItem.addToolsAndAttributes(paxelItem.getMaterial(), stack);
        }
    }

}
