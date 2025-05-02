package com.thepigcat.paxelz.registries;

import com.mojang.serialization.Codec;
import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.content.components.UpgradesComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class PaxelzComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Paxelz.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradesComponent>> UPGRADES = COMPONENTS.registerComponentType("upgrades", builder -> builder
            .persistent(UpgradesComponent.CODEC)
            .networkSynchronized(UpgradesComponent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_STORAGE = COMPONENTS.registerComponentType("energy_storage", builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PREVIOUS_DAMAGE = COMPONENTS.registerComponentType("previous_damage", builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Optional<BlockPos>>> STORAGE_LINK = COMPONENTS.registerComponentType("storage_link", builder -> builder
            .persistent(BlockPos.CODEC.optionalFieldOf("linkedPos").codec())
            .networkSynchronized(ByteBufCodecs.optional(BlockPos.STREAM_CODEC)));
}
