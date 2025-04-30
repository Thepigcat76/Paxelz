package com.thepigcat.paxelz.registries;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.upgrades.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class PaxelzUpgrades {
    public static final DeferredRegister<Upgrade> UPGRADES = DeferredRegister.create(PaxelzRegistries.UPGRADE, Paxelz.MODID);

    public static final Supplier<Upgrade> EMPTY = UPGRADES.register("empty", () -> Upgrade.EMPTY);
    public static final Supplier<AreaMiningUpgrade> AREA_MINING = UPGRADES.register("area_mining", () -> new AreaMiningUpgrade(3));
    public static final Supplier<EnergyStorageUpgrade> ENERGY_STORAGE = UPGRADES.register("energy_storage", () -> new EnergyStorageUpgrade());
    public static final Supplier<StorageLinkUpgrade> STORAGE_LINK = UPGRADES.register("storage_link", () -> new StorageLinkUpgrade());
    public static final Supplier<VeinMinerUpgrade> VEIN_MINER = UPGRADES.register("vein_miner", () -> new VeinMinerUpgrade());
    public static final Supplier<WallPhaseUpgrade> WALL_PHASE = UPGRADES.register("wall_phase", () -> new WallPhaseUpgrade());

}
