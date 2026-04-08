package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public final class PaxelzItemModelProvider {
    public void registerModels(ItemModelGenerators generators) {
        generators.generateFlatItem(PaxelzItems.WOODEN_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.STONE_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.COPPER_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.IRON_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.GOLD_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.DIAMOND_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(PaxelzItems.NETHERITE_PAXEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        generators.generateFlatItem(PaxelzItems.UPGRADE_BASE.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(PaxelzItems.UPGRADE_AREA_MINING.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(PaxelzItems.UPGRADE_ENERGY_STORAGE.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(PaxelzItems.UPGRADE_STORAGE_LINK.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(PaxelzItems.UPGRADE_VEIN_MINER.get(), ModelTemplates.FLAT_ITEM);
        //generators.generateFlatItem(PaxelzItems.UPGRADE_WALL_PHASE.get(), ModelTemplates.FLAT_ITEM);
    }


}
