package com.thepigcat.paxelz;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class PaxelzTags {
    public static final class Blocks {
        public static final TagKey<Block> PAXEL_MINEABLE = bind("paxel_mineable");

        public static TagKey<Block> bind(String value) {
            return TagKey.create(Registries.BLOCK, Paxelz.rl(value));
        }
    }

    public static final class Items {
        public static final TagKey<Item> PAXEL = bind("paxel");

        public static TagKey<Item> bind(String value) {
            return TagKey.create(Registries.ITEM, Paxelz.rl(value));
        }
    }
}
