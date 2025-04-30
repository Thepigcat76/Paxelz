package com.thepigcat.paxelz.utils;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class CodecUtils {
    public static <R> Codec<R> registryCodec(Registry<R> registry) {
        return ResourceLocation.CODEC.xmap(registry::get, registry::getKey);
    }

    public static <R> StreamCodec<ByteBuf, R> registryStreamCodec(Registry<R> registry) {
        return ResourceLocation.STREAM_CODEC.map(registry::get, registry::getKey);
    }
}
