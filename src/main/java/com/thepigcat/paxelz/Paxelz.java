package com.thepigcat.paxelz;

import com.thepigcat.paxelz.content.components.PaxelzEnergyComponent;
import com.thepigcat.paxelz.content.items.PaxelItem;
import com.thepigcat.paxelz.registries.*;
import com.thepigcat.paxelz.utils.PaxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod(Paxelz.MODID)
public final class Paxelz {
    public static final String MODID = "paxelz";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Paxelz(IEventBus modEventBus, ModContainer modContainer) {
        PaxelzItems.ITEMS.register(modEventBus);
        PaxelzTabs.TABS.register(modEventBus);
        PaxelzUpgrades.UPGRADES.register(modEventBus);
        PaxelzComponents.COMPONENTS.register(modEventBus);
        PaxelzAttachments.ATTACHMENTS.register(modEventBus);

        modEventBus.addListener(this::onCapabilityAttached);
        modEventBus.addListener(this::registerRegistry);

        NeoForge.EVENT_BUS.addListener(this::onBlockDrops);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerRegistry(NewRegistryEvent event) {
        event.register(PaxelzRegistries.UPGRADE);
    }

    private void onCapabilityAttached(RegisterCapabilitiesEvent event) {
        for (ItemLike item : PaxelzItems.PAXELS) {
            event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)
                    ? new PaxelzEnergyComponent(stack, PaxelzComponents.ENERGY_STORAGE.get(), 1000)
                    : null, item.asItem());
        }
    }

    private void onBlockDrops(BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        if (breaker != null) {
            if (breaker instanceof Player player) {
                ItemStack toolStack = event.getTool();
                if (toolStack.is(PaxelzTags.Items.PAXEL)) {
                    if (PaxelUtils.hasUpgrade(toolStack, PaxelzUpgrades.STORAGE_LINK) && PaxelUtils.hasLinkedStorage(toolStack)) {
                        BlockState state = event.getState();
                        ServerLevel level = event.getLevel();
                        BlockPos pos1 = event.getPos();
                        List<ItemEntity> drops = event.getDrops();
                        int droppedExperience = event.getDroppedExperience();

                        PaxelUtils.handleBlockDrops(player, toolStack, level, drops.stream().map(ItemEntity::getItem).toList(), pos1, state, droppedExperience);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
