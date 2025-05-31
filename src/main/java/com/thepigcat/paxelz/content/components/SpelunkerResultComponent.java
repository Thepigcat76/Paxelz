package com.thepigcat.paxelz.content.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SpelunkerResultComponent(List<BlockPos> detectedOres) {
    public static final Codec<SpelunkerResultComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
       BlockPos.CODEC.listOf().fieldOf("detected_ores").forGetter(SpelunkerResultComponent::detectedOres)
    ).apply(inst, SpelunkerResultComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpelunkerResultComponent> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SpelunkerResultComponent::detectedOres,
            SpelunkerResultComponent::new
    );
}
