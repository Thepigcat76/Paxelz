package com.thepigcat.paxelz.datagen;

import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class PaxelzTagsProvider {
    public static final class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
            super(output, lookupProvider, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(PaxelzTags.Items.PAXEL)
                    .add(PaxelzItems.WOODEN_PAXEL.get())
                    .add(PaxelzItems.STONE_PAXEL.get())
                    .add(PaxelzItems.IRON_PAXEL.get())
                    .add(PaxelzItems.GOLD_PAXEL.get())
                    .add(PaxelzItems.DIAMOND_PAXEL.get())
                    .add(PaxelzItems.NETHERITE_PAXEL.get());
        }
    }

    public static final class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Paxelz.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(PaxelzTags.Blocks.PAXEL_MINEABLE)
                    .addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL);
        }
    }

}
