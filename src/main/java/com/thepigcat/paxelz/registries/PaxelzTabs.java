package com.thepigcat.paxelz.registries;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.content.items.PaxelItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PaxelzTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Paxelz.MODID);

    static {
        TABS.register("main", () -> CreativeModeTab.builder()
                .icon(PaxelzItems.DIAMOND_PAXEL::toStack)
                .title(Component.translatable("tab.paxelz.main"))
                .displayItems((params, output) -> {
                    addPaxel(output, PaxelzItems.WOODEN_PAXEL);
                    addPaxel(output, PaxelzItems.STONE_PAXEL);
                    addPaxel(output, PaxelzItems.IRON_PAXEL);
                    addPaxel(output, PaxelzItems.GOLD_PAXEL);
                    addPaxel(output, PaxelzItems.DIAMOND_PAXEL);
                    addPaxel(output, PaxelzItems.NETHERITE_PAXEL);

                    output.accept(PaxelzItems.UPGRADE_BASE);

                    output.accept(PaxelzItems.UPGRADE_AREA_MINING);
                    output.accept(PaxelzItems.UPGRADE_ENERGY_STORAGE);
                    output.accept(PaxelzItems.UPGRADE_STORAGE_LINK);
                    output.accept(PaxelzItems.UPGRADE_VEIN_MINER);
                    output.accept(PaxelzItems.UPGRADE_SPELUNKER);
                })
                .build());
    }

    private static void addPaxel(CreativeModeTab.Output output, DeferredItem<PaxelItem> item) {
        output.accept(item);
    }

}
