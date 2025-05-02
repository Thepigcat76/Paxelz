package com.thepigcat.paxelz.content.attachments;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record PassThroughBlocksAttachment(List<BlockPos> blocks) {
    public static final Codec<PassThroughBlocksAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.listOf().fieldOf("blocks").forGetter(PassThroughBlocksAttachment::blocks)
    ).apply(inst, PassThroughBlocksAttachment::new));

    public PassThroughBlocksAttachment() {
        this(new ArrayList<>(27));
    }

    public static PassThroughBlocksAttachment withBlocks(Iterable<BlockPos> positions) {
        List<BlockPos> blocks = new ArrayList<>();
        for (BlockPos pos : positions) {
            blocks.add(pos.immutable());
        }
        return new PassThroughBlocksAttachment(blocks);
    }
}
