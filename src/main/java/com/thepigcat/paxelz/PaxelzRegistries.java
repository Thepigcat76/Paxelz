package com.thepigcat.paxelz;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class PaxelzRegistries {
    public static final ResourceKey<Registry<Upgrade>> UPGRADE_KEY = ResourceKey.createRegistryKey(Paxelz.rl("upgrade"));
    public static final Registry<Upgrade> UPGRADE = new RegistryBuilder<>(UPGRADE_KEY).create();
}
