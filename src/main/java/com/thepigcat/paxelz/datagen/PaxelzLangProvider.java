package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzItems;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class PaxelzLangProvider extends LanguageProvider {
    public PaxelzLangProvider(PackOutput output) {
        super(output, Paxelz.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("tab.paxelz.main", "Paxelz");
        add("tooltip.paxelz.paxel_item.upgrades", "Upgrades");
        add("tooltip.paxelz.energy_stored", "Stored: ");
        add("tooltip.paxelz.energy_amount", "%d/%d");

        add("message.paxelz.storage_link", "Storage Linked!");

        add(PaxelzItems.WOODEN_PAXEL.get(), "Wooden Paxel");
        add(PaxelzItems.STONE_PAXEL.get(), "Stone Paxel");
        add(PaxelzItems.IRON_PAXEL.get(), "Iron Paxel");
        add(PaxelzItems.GOLD_PAXEL.get(), "Gold Paxel");
        add(PaxelzItems.DIAMOND_PAXEL.get(), "Diamond Paxel");
        add(PaxelzItems.NETHERITE_PAXEL.get(), "Netherite Paxel");

        add(PaxelzItems.UPGRADE_BASE.get(), "Upgrade Base");

        add(PaxelzItems.UPGRADE_AREA_MINING.get(), "Upgrade Area Mining");
        add(PaxelzItems.UPGRADE_ENERGY_STORAGE.get(), "Upgrade Energy Storage");
        add(PaxelzItems.UPGRADE_STORAGE_LINK.get(), "Upgrade Storage Link");
        add(PaxelzItems.UPGRADE_VEIN_MINER.get(), "Upgrade Vein Miner");
        add(PaxelzItems.UPGRADE_SPELUNKER.get(), "Upgrade Spelunker");

        addUpgrade(PaxelzUpgrades.EMPTY.get(), "Empty");
        addUpgrade(PaxelzUpgrades.AREA_MINING.get(), "Area Mining");
        addUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get(), "Energy Storage");
        addUpgrade(PaxelzUpgrades.STORAGE_LINK.get(), "Storage Link");
        addUpgrade(PaxelzUpgrades.VEIN_MINER.get(), "Vein Miner");
        addUpgrade(PaxelzUpgrades.SPELUNKER.get(), "Spelunker");
    }

    private void addUpgrade(Upgrade upgrade, String name) {
        add("upgrade.paxelz." + PaxelzRegistries.UPGRADE.getKey(upgrade).getPath(), name);
    }
}
