package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;

public final class PaxelzRecipeProvider extends RecipeProvider {
    public PaxelzRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput p_recipeOutput, HolderLookup.Provider holderLookup) {
        paxelRecipe(p_recipeOutput, PaxelzItems.WOODEN_PAXEL, Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE);
        paxelRecipe(p_recipeOutput, PaxelzItems.STONE_PAXEL, Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_PICKAXE);
        paxelRecipe(p_recipeOutput, PaxelzItems.IRON_PAXEL, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_PICKAXE);
        paxelRecipe(p_recipeOutput, PaxelzItems.GOLD_PAXEL, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE);
        paxelRecipe(p_recipeOutput, PaxelzItems.DIAMOND_PAXEL, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE);
        netheriteSmithing(p_recipeOutput, PaxelzItems.DIAMOND_PAXEL.asItem(), RecipeCategory.COMBAT, PaxelzItems.NETHERITE_PAXEL.asItem());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PaxelzItems.UPGRADE_BASE)
                .pattern(" I ")
                .pattern("ISI")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(p_recipeOutput);

        paxelUpgradeRecipe(p_recipeOutput, PaxelzItems.UPGRADE_SPELUNKER, Items.AMETHYST_SHARD);
        paxelUpgradeRecipe(p_recipeOutput, PaxelzItems.UPGRADE_VEIN_MINER, Items.DIAMOND_PICKAXE);
        paxelUpgradeRecipe(p_recipeOutput, PaxelzItems.UPGRADE_ENERGY_STORAGE, Items.REDSTONE);
        paxelUpgradeRecipe(p_recipeOutput, PaxelzItems.UPGRADE_STORAGE_LINK, Items.CHEST);
        paxelUpgradeRecipe(p_recipeOutput, PaxelzItems.UPGRADE_AREA_MINING, Items.TNT);
    }

    private static void paxelUpgradeRecipe(RecipeOutput p_recipeOutput, DeferredItem<Item> upgrade, Item upgradeItem) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, upgrade.asItem())
                .requires(PaxelzItems.UPGRADE_BASE)
                .requires(upgradeItem)
                .unlockedBy("has_upgrade_base", has(PaxelzItems.UPGRADE_BASE))
                .save(p_recipeOutput);
    }

    private static void paxelRecipe(RecipeOutput p_recipeOutput, DeferredItem<PaxelItem> paxel, Item axe, Item shovel, Item pickaxe) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, paxel)
                .pattern("ASP")
                .pattern(" I ")
                .pattern(" I ")
                .define('A', axe)
                .define('S', shovel)
                .define('P', pickaxe)
                .define('I', Items.STICK)
                .unlockedBy("has_tools", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(axe, shovel, pickaxe)))
                .save(p_recipeOutput);
    }
}
