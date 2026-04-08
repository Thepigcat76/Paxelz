package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;

public final class PaxelzRecipeProvider extends RecipeProvider {
    public PaxelzRecipeProvider(HolderLookup.Provider lookup, RecipeOutput recipeOutput) {
        super(lookup, recipeOutput);
    }

    @Override
    protected void buildRecipes() {
        paxelRecipe(PaxelzItems.WOODEN_PAXEL, Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE);
        paxelRecipe(PaxelzItems.STONE_PAXEL, Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_PICKAXE);
        paxelRecipe(PaxelzItems.IRON_PAXEL, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_PICKAXE);
        paxelRecipe(PaxelzItems.COPPER_PAXEL, Items.COPPER_AXE, Items.COPPER_SHOVEL, Items.COPPER_PICKAXE);
        paxelRecipe(PaxelzItems.GOLD_PAXEL, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE);
        paxelRecipe(PaxelzItems.DIAMOND_PAXEL, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE);
        netheriteSmithing(PaxelzItems.DIAMOND_PAXEL.asItem(), RecipeCategory.COMBAT, PaxelzItems.NETHERITE_PAXEL.asItem());

        shaped(RecipeCategory.MISC, PaxelzItems.UPGRADE_BASE)
                .pattern(" I ")
                .pattern("ISI")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(this.output);

        //paxelUpgradeRecipe(PaxelzItems.UPGRADE_WALL_PHASE, Items.AMETHYST_SHARD);
        paxelUpgradeRecipe(PaxelzItems.UPGRADE_VEIN_MINER, Items.DIAMOND_PICKAXE);
        paxelUpgradeRecipe(PaxelzItems.UPGRADE_ENERGY_STORAGE, Items.REDSTONE);
        paxelUpgradeRecipe(PaxelzItems.UPGRADE_STORAGE_LINK, Items.CHEST);
        paxelUpgradeRecipe(PaxelzItems.UPGRADE_AREA_MINING, Items.TNT);
    }

    private void paxelUpgradeRecipe(DeferredItem<Item> upgrade, Item upgradeItem) {
        shapeless(RecipeCategory.MISC, upgrade.asItem())
                .requires(PaxelzItems.UPGRADE_BASE)
                .requires(upgradeItem)
                .unlockedBy("has_upgrade_base", has(PaxelzItems.UPGRADE_BASE))
                .save(this.output);
    }

    private void paxelRecipe(DeferredItem<PaxelItem> paxel, Item axe, Item shovel, Item pickaxe) {
        shaped(RecipeCategory.TOOLS, paxel)
                .pattern("ASP")
                .pattern(" I ")
                .pattern(" I ")
                .define('A', axe)
                .define('S', shovel)
                .define('P', pickaxe)
                .define('I', Items.STICK)
                .unlockedBy("has_tools", inventoryTrigger(ItemPredicate.Builder.item().of(this.registries.lookupOrThrow(Registries.ITEM), axe, shovel, pickaxe)))
                .save(this.output);
    }
}
