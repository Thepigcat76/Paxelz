package com.thepigcat.paxelz;

import com.thepigcat.paxelz.content.components.PaxelzEnergyComponent;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import com.thepigcat.paxelz.registries.PaxelzTabs;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Paxelz.MODID)
public final class Paxelz {
    public static final String MODID = "paxelz";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Paxelz(IEventBus modEventBus, ModContainer modContainer) {
        PaxelzItems.ITEMS.register(modEventBus);
        PaxelzTabs.TABS.register(modEventBus);
        PaxelzUpgrades.UPGRADES.register(modEventBus);
        PaxelzComponents.COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::onCapabilityAttached);
        modEventBus.addListener(this::registerRegistry);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerRegistry(NewRegistryEvent event) {
        event.register(PaxelzRegistries.UPGRADE);
    }

    private void onCapabilityAttached(RegisterCapabilitiesEvent event) {
        for (ItemLike item : PaxelzItems.PAXELS) {
            event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> stack.get(PaxelzComponents.UPGRADES.get()).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())
                    ? new PaxelzEnergyComponent(stack, PaxelzComponents.ENERGY_STORAGE.get(), 1000)
                    : null, item.asItem());
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
