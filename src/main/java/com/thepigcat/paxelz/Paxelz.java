package com.thepigcat.paxelz;

import com.thepigcat.paxelz.content.components.PaxelzEnergyComponent;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import com.thepigcat.paxelz.registries.PaxelzTabs;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.Nullable;
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
            event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> stack.get(PaxelzComponents.UPGRADES.get()).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())
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
                    if (toolStack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.STORAGE_LINK.get())) {
                        Optional<BlockPos> _linkedPos = toolStack.get(PaxelzComponents.STORAGE_LINK);
                        if (_linkedPos.isPresent()) {
                            IItemHandler itemHandler = event.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, _linkedPos.get(), null);
                            if (itemHandler != null) {
                                List<ItemStack> remainders = new ArrayList<>();
                                for (ItemEntity itemEntity : event.getDrops()) {
                                    ItemStack remainder = ItemHandlerHelper.insertItem(itemHandler, itemEntity.getItem(), false);
                                    remainders.add(remainder);
                                }
                                for (ItemStack remainder : remainders) {
                                    BlockPos pos = event.getPos();
                                    Block.popResource(player.level(), pos, remainder);
                                }
                                if (event.getLevel() instanceof ServerLevel serverLevel) {
                                    event.getState().getBlock().popExperience(serverLevel, event.getPos(), event.getDroppedExperience());
                                }
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
