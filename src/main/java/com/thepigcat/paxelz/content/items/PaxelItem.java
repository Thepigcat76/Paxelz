package com.thepigcat.paxelz.content.items;

import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.components.UpgradesComponent;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class PaxelItem extends DiggerItem {
    public static final int COLOR = FastColor.ARGB32.color(255, 215, 0, 0);
    public static final int ENERGY_USAGE = 8;
    private final Tier tier;

    public PaxelItem(Tier tier, Properties properties) {
        super(tier, PaxelzTags.Blocks.PAXEL_MINEABLE, properties);
        this.tier = tier;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (stack.is(PaxelzTags.Items.PAXEL) && stack.has(PaxelzComponents.UPGRADES)) {
            UpgradesComponent upgrades = stack.get(PaxelzComponents.UPGRADES);
            if (upgrades.maxUpgrades() > 0 && !other.isEmpty()) {
                Upgrade upgrade = PaxelzUpgrades.EMPTY.get();
                for (Upgrade upgrade1 : PaxelzRegistries.UPGRADE) {
                    if (upgrade1.upgradeItem() == other.getItem()) {
                        upgrade = upgrade1;
                    }
                }
                if (upgrade.isEmpty()) {
                    return false;
                }

                if (action == ClickAction.SECONDARY) {
                    upgrade.onUpgradeAdded(stack);
                    stack.set(PaxelzComponents.UPGRADES, upgrades.addUpgrade(upgrade));
                    other.shrink(1);
                    player.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
                    return true;
                }
            }

            if (!upgrades.isEmpty() && action == ClickAction.SECONDARY && other.isEmpty()) {
                Upgrade upgrade = upgrades.upgrades().get(upgrades.upgradesAmount() - 1);
                upgrade.onUpgradeRemoved(stack);
                stack.set(PaxelzComponents.UPGRADES, upgrades.removeUpgrade());
                player.inventoryMenu.setCarried(upgrade.upgradeItem().getDefaultInstance());
                player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage.getEnergyStored() < ENERGY_USAGE) {
                return false;
            }
        }
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            modifyEnergyAttributes(stack);
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            modifyEnergyAttributes(stack);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public void initEnergyStorage(IEnergyStorage energyStorage, ItemStack itemStack) {
        if (energyStorage.getEnergyStored() < ENERGY_USAGE) {
            itemStack.remove(DataComponents.TOOL);
        } else {
            itemStack.set(DataComponents.TOOL, tier.createToolProperties(PaxelzTags.Blocks.PAXEL_MINEABLE));
        }
    }

    public void modifyEnergyAttributes(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        int oldAmount = energyStorage.getEnergyStored();
        energyStorage.extractEnergy(ENERGY_USAGE, false);

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, DiggerItem.createAttributes(
                tier,
                energyStorage.getEnergyStored() >= ENERGY_USAGE
                        ? 4.0f
                        : -1f,
                -2.6f
        ));
        if (energyStorage.getEnergyStored() < ENERGY_USAGE) {
            stack.remove(DataComponents.TOOL);
        } else if (oldAmount  < ENERGY_USAGE) {
            stack.set(DataComponents.TOOL, tier.createToolProperties(PaxelzTags.Blocks.PAXEL_MINEABLE));
        }
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            float ratio = (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
            return Math.round(13.0F - ((1 - ratio) * 13.0F));
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            return COLOR;
        }
        return super.getBarColor(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get()) || super.isBarVisible(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.get(PaxelzComponents.UPGRADES).hasUpgrade(PaxelzUpgrades.ENERGY_STORAGE.get())) {
            addEnergyTooltip(tooltipComponents, stack);
        }
        if (stack.has(PaxelzComponents.UPGRADES.get())) {
            UpgradesComponent upgradesComponent = stack.get(PaxelzComponents.UPGRADES.get());
            if (upgradesComponent.maxUpgrades() != 0) {
                upgradesComponent.addTooltip(tooltipComponents);
            }
        }
    }

    private static void addEnergyTooltip(List<Component> tooltip, ItemStack itemStack) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage != null) {
            tooltip.add(
                    Component.translatable("tooltip.paxelz.energy_stored")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.translatable("tooltip.paxelz.energy_amount", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored())
                                    .withColor(FastColor.ARGB32.color(255, 245, 192, 89)))
                            .append(" ")
                            .append(Component.literal("FE")
                                    .withColor(FastColor.ARGB32.color(255, 245, 192, 89)))
            );
        }
    }

}
