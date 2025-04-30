package com.thepigcat.paxelz.registries;

import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.content.components.UpgradesComponent;
import com.thepigcat.paxelz.content.items.PaxelItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PaxelzItems {
    public static final List<ItemLike> PAXELS = new ArrayList<>();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Paxelz.MODID);

    public static final DeferredItem<PaxelItem> WOODEN_PAXEL = paxelItem("wooden_paxel", Tiers.WOOD, 0);
    public static final DeferredItem<PaxelItem> STONE_PAXEL = paxelItem("stone_paxel", Tiers.STONE, 0);
    public static final DeferredItem<PaxelItem> IRON_PAXEL = paxelItem("iron_paxel", Tiers.IRON, 1);
    public static final DeferredItem<PaxelItem> GOLD_PAXEL = paxelItem("gold_paxel", Tiers.GOLD, 3);
    public static final DeferredItem<PaxelItem> DIAMOND_PAXEL = paxelItem("diamond_paxel", Tiers.DIAMOND, 2);
    public static final DeferredItem<PaxelItem> NETHERITE_PAXEL = paxelItem("netherite_paxel", Tiers.NETHERITE, 3);

    public static final DeferredItem<Item> UPGRADE_BASE = upgradeItem("upgrade_base");

    public static final DeferredItem<Item> UPGRADE_AREA_MINING = upgradeItem("upgrade_area_mining");
    public static final DeferredItem<Item> UPGRADE_ENERGY_STORAGE = upgradeItem("upgrade_energy_storage");
    public static final DeferredItem<Item> UPGRADE_STORAGE_LINK = upgradeItem("upgrade_storage_link");
    public static final DeferredItem<Item> UPGRADE_VEIN_MINER = upgradeItem("upgrade_vein_miner");
    public static final DeferredItem<Item> UPGRADE_WALL_PHASE = upgradeItem("upgrade_wall_phase");

    private static @NotNull DeferredItem<Item> upgradeItem(String name) {
        return ITEMS.registerSimpleItem(name);
    }

    private static @NotNull DeferredItem<PaxelItem> paxelItem(String name, Tier tier, int maxUpgrades) {
        DeferredItem<PaxelItem> item = ITEMS.register(name, () -> new PaxelItem(tier, new Item.Properties()
                .component(DataComponents.TOOL, tier.createToolProperties(PaxelzTags.Blocks.PAXEL_MINEABLE))
                .component(PaxelzComponents.UPGRADES.get(), new UpgradesComponent(maxUpgrades))
                .component(PaxelzComponents.ENERGY_STORAGE.get(), 0)
                .component(PaxelzComponents.PREVIOUS_DAMAGE.get(), 0)
                .attributes(DiggerItem.createAttributes(tier, 4f, -2.6f))));
        PAXELS.add(item);
        return item;
    }
}
