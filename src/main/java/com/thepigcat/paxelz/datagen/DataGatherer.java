package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.Paxelz;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Paxelz.MODID)
public final class DataGatherer {
    @SubscribeEvent
    public static void onGather(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        event.addProvider(new ModelProvider(event.getGenerator().getPackOutput(), Paxelz.MODID) {
            private final PaxelzItemModelProvider itemModelProvider = new PaxelzItemModelProvider();

            @Override
            protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
                this.itemModelProvider.registerModels(itemModels);
            }
        });
        event.addProvider(new PaxelzLangProvider(generator.getPackOutput()));

        event.addProvider(new PaxelzTagsProvider.Blocks(generator.getPackOutput(), event.getLookupProvider()));
        event.addProvider(new PaxelzTagsProvider.Items(generator.getPackOutput(), event.getLookupProvider()));
        event.addProvider(new RecipeProvider.Runner(generator.getPackOutput(), event.getLookupProvider()) {
            @Override
            public String getName() {
                return "Paxelz' Recipes";
            }

            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
                return new PaxelzRecipeProvider(provider, recipeOutput);
            }
        });
    }
}
