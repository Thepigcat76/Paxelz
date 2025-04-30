package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.Paxelz;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Paxelz.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGatherer {
    @SubscribeEvent
    public static void onGather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        event.addProvider(new PaxelzItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
        event.addProvider(new PaxelzLangProvider(generator.getPackOutput()));

        PaxelzTagsProvider.Blocks blockTagsProvider = event.addProvider(new PaxelzTagsProvider.Blocks(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        event.addProvider(new PaxelzTagsProvider.Items(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter()));

    }
}
