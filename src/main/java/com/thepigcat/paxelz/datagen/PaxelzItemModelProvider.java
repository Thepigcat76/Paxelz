package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public final class PaxelzItemModelProvider extends ItemModelProvider {
    public PaxelzItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Paxelz.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(PaxelzItems.WOODEN_PAXEL);
        handheldItem(PaxelzItems.STONE_PAXEL);
        handheldItem(PaxelzItems.IRON_PAXEL);
        handheldItem(PaxelzItems.GOLD_PAXEL);
        handheldItem(PaxelzItems.DIAMOND_PAXEL);
        handheldItem(PaxelzItems.NETHERITE_PAXEL);

        basicItem(PaxelzItems.UPGRADE_BASE.get());

        basicItem(PaxelzItems.UPGRADE_AREA_MINING.get());
        basicItem(PaxelzItems.UPGRADE_ENERGY_STORAGE.get());
        basicItem(PaxelzItems.UPGRADE_STORAGE_LINK.get());
        basicItem(PaxelzItems.UPGRADE_VEIN_MINER.get());
        basicItem(PaxelzItems.UPGRADE_WALL_PHASE.get());
    }

    public ItemModelBuilder handheldItem(ItemLike itemLike) {
        return handheldItem(itemLike, "");
    }

    public ItemModelBuilder handheldItem(ItemLike item, String suffix) {
        ResourceLocation location = key(item);
        return getBuilder(location + suffix)
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + suffix));
    }

    private static @NotNull ResourceLocation key(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

}
