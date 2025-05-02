package com.thepigcat.paxelz.content.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import com.thepigcat.paxelz.utils.CodecUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record UpgradesComponent(List<Upgrade> upgrades, int maxUpgrades) {
    public static final Codec<UpgradesComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CodecUtils.registryCodec(PaxelzRegistries.UPGRADE).listOf().fieldOf("upgrades").forGetter(UpgradesComponent::upgrades),
            Codec.INT.fieldOf("maxUpgrades").forGetter(UpgradesComponent::maxUpgrades)
    ).apply(inst, UpgradesComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradesComponent> STREAM_CODEC = StreamCodec.composite(
            CodecUtils.registryStreamCodec(PaxelzRegistries.UPGRADE).apply(ByteBufCodecs.list()),
            UpgradesComponent::upgrades,
            ByteBufCodecs.INT,
            UpgradesComponent::maxUpgrades,
            UpgradesComponent::new
    );

    public UpgradesComponent(int maxUpgrades) {
        this(new ArrayList<>(), maxUpgrades);
    }

    public UpgradesComponent addUpgrade(Upgrade upgrade) {
        if (this.upgrades.size() < maxUpgrades) {
            List<Upgrade> upgrades = new ArrayList<>(this.upgrades);
            upgrades.add(upgrade);
            return new UpgradesComponent(upgrades, this.maxUpgrades);
        }
        return this;
    }

    public UpgradesComponent removeUpgrade() {
        if (!this.upgrades.isEmpty()) {
            List<Upgrade> upgrades = new ArrayList<>(this.upgrades);
            upgrades.removeLast();
            return new UpgradesComponent(upgrades, this.maxUpgrades);
        }
        return this;
    }

    public boolean hasUpgrade(Upgrade upgrade) {
        return this.upgrades.contains(upgrade);
    }

    public void addTooltip(List<Component> components) {
        components.add(Component.translatable("tooltip.paxelz.paxel_item.upgrades")
                .withStyle(this.upgrades.isEmpty() ? ChatFormatting.RED : (this.upgrades.size() == this.maxUpgrades ? ChatFormatting.GREEN : ChatFormatting.GOLD)));
        for (int i = 0; i < this.maxUpgrades; i++) {
            Upgrade upgrade = PaxelzUpgrades.EMPTY.get();
            if (i < this.upgrades.size()) {
                upgrade = this.upgrades.get(i);
            }
            components.add(Component.literal("| [%s] - ".formatted(upgrade.isEmpty() ? " " : "+")).append(Component.translatable("upgrade.paxelz." + PaxelzRegistries.UPGRADE.getKey(upgrade).getPath())).withStyle(upgrade.isEmpty() ? ChatFormatting.RED : ChatFormatting.GREEN));
        }
    }

    private static NonNullList<Upgrade> copyOf(List<Upgrade> entries) {
        return NonNullList.of(Upgrade.EMPTY, entries.toArray(Upgrade[]::new));
    }

    public boolean isEmpty() {
        for (Upgrade upgrade : this.upgrades) {
            if (!upgrade.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
