package com.thepigcat.paxelz.registries;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.content.attachments.PassThroughBlocksAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class PaxelzAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Paxelz.MODID);

    public static final Supplier<AttachmentType<PassThroughBlocksAttachment>> PASS_THROUGH_BLOCKS = ATTACHMENTS.register("pass_through_blocks",
            () -> AttachmentType.builder(() -> new PassThroughBlocksAttachment())
                    .build());
}
