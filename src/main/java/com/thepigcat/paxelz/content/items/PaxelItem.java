package com.thepigcat.paxelz.content.items;

import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.WallPhaseManager;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
import com.thepigcat.paxelz.content.attachments.PassThroughBlocksAttachment;
import com.thepigcat.paxelz.content.components.UpgradesComponent;
import com.thepigcat.paxelz.registries.PaxelzAttachments;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import com.thepigcat.paxelz.utils.PaxelUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class PaxelItem extends Item {
    public static final int COLOR = ARGB.color(255, 215, 0, 0);
    public static final int ENERGY_USAGE = 8;
    private final ToolMaterial material;

    public PaxelItem(ToolMaterial material, Properties properties) {
        super(properties);
        this.material = material;
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

                // Check if upgrade is valid and not already installed
                if (upgrade.isEmpty() || upgrades.upgrades().contains(upgrade)) {
                    return false;
                } else {
                    // Check for incompatability
                    for (Upgrade upgrade1 : upgrades.upgrades()) {
                        if (upgrade1.isIncompatible(upgrade)) return false;
                    }
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
                if (!upgrades.upgrades().isEmpty()) {
                    Upgrade upgrade = upgrades.upgrades().getLast();
                    upgrade.onUpgradeRemoved(stack);
                    stack.set(PaxelzComponents.UPGRADES, upgrades.removeUpgrade());
                    player.inventoryMenu.setCarried(upgrade.upgradeItem().getDefaultInstance());
                    player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
                    return true;
                } else if (!upgrades.isEmpty()) {
                    for (int i = 0; i < upgrades.maxUpgrades(); i++) {
                        upgrades.upgrades().set(0, PaxelzUpgrades.EMPTY.get());
                    }
                }
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        if (PaxelUtils.hasUpgrade(itemInHand, PaxelzUpgrades.STORAGE_LINK) && context.getPlayer().isShiftKeyDown()) {
            boolean storageLinked = PaxelzUpgrades.STORAGE_LINK.get().linkStorage(context, itemInHand);
            if (storageLinked) {
                return InteractionResult.SUCCESS;
            }
        }

//        if (PaxelUtils.hasUpgrade(itemInHand, PaxelzUpgrades.WALL_PHASE) && !context.getPlayer().isShiftKeyDown()) {
//            PassThroughBlocksAttachment blocks = PassThroughBlocksAttachment.withBlocks(get3x3x3PhaseArea(context.getClickedPos(), context.getClickedFace()));
//            context.getPlayer().setData(PaxelzAttachments.PASS_THROUGH_BLOCKS.get(), blocks);
//            if (context.getLevel().isClientSide()) {
//                ClientWallPhaseManager.WALL_PHASE_BLOCKS.clear();
//                ClientWallPhaseManager.WALL_PHASE_BLOCKS.addAll(blocks.blocks());
//            }
//            for (BlockPos pos : blocks.blocks()) {
//                context.getLevel().sendBlockUpdated(pos, context.getLevel().getBlockState(pos), context.getLevel().getBlockState(pos), 3);
//                context.getLevel().updateNeighborsAt(pos, context.getLevel().getBlockState(pos).getBlock());
//                context.getLevel().setBlocksDirty(pos, context.getLevel().getBlockState(pos), context.getLevel().getBlockState(pos));
//                context.getLevel().getChunkAt(pos).markUnsaved();
//            }
//            context.getPlayer().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
//            context.getPlayer().getCooldowns().addCooldown(context.getItemInHand(), 200);
//            if (context.getLevel().isClientSide()) {
//                ClientWallPhaseManager.WALL_PHASE_TICKER = 160;
//            } else {
//                WallPhaseManager.WALL_PHASE_TICKER.put(context.getPlayer().getUUID(), 160);
//            }
//            return InteractionResult.SUCCESS;
//        }

        return InteractionResult.FAIL;
    }

//    @Override
//    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
//        if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
//            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
//            if (energyStorage.getEnergyStored() < ENERGY_USAGE) {
//                return false;
//            }
//        }
//        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
//    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (miningEntity instanceof Player player) {
            boolean hasUpgrade = PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE);
            int energyStored = 0;
            if (hasUpgrade) {
                EnergyHandler energyHandler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
                energyStored = energyHandler.getAmountAsInt();
            }

            if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.VEIN_MINER) && !PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.AREA_MINING)) {
                PaxelzUpgrades.VEIN_MINER.get().veinMine(stack, player, pos);
            }

            if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.AREA_MINING)) {
                BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if (!player.isShiftKeyDown()) {
                    mine3x3(player, pos, stack, hitResult.getDirection(), hasUpgrade ? energyStored : stack.getMaxDamage() - stack.getDamageValue(), hasUpgrade ? ENERGY_USAGE : 1, PaxelUtils::damageItem);
                }
            }
            if (hasUpgrade) {
                modifyEnergyAttributes(stack);
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    private void mine3x3(Player player, BlockPos pos, ItemStack stack, Direction hitFace, int stored, int costPerBlock, BiConsumer<Player, ItemStack> drainFunction) {
        Level level = player.level();
        int blocksToBreak = Math.min(stored / costPerBlock, 9);

        Iterable<BlockPos> blocksToMine = get3x3MiningArea(pos, hitFace);

        boolean drainedFirst = true;
        for (BlockPos targetPos : blocksToMine) {
            if (blocksToBreak > 0 && PaxelUtils.canMine(level, targetPos, level.getBlockState(targetPos))) {
                if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.VEIN_MINER)) {
                    PaxelzUpgrades.VEIN_MINER.get().veinMine(stack, player, pos);
                    blocksToBreak--;
                } else {
                    blocksToBreak = breakBlock(player, targetPos, stack, stored, blocksToBreak, PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE) ? ENERGY_USAGE : 1);
                }
                if (!drainedFirst) {
                    drainFunction.accept(player, stack);
                } else {
                    drainedFirst = false;
                }
            }
        }
    }

    public static Iterable<BlockPos> get3x3x3PhaseArea(BlockPos center, Direction hitFace) {
        return switch (hitFace) {
            case NORTH, SOUTH -> BlockPos.betweenClosed(center.offset(-1, -1, -2), center.offset(1, 1, 0));
            case EAST, WEST -> BlockPos.betweenClosed(center.offset(-2, -1, -1), center.offset(0, 1, 1));
            default -> BlockPos.betweenClosed(center.offset(-1, -2, -1), center.offset(1, 0, 1));
        };
    }

    // Method to get the 3x3 mining area based on the face the player hit
    public static Iterable<BlockPos> get3x3MiningArea(BlockPos center, Direction hitFace) {
        return switch (hitFace) {
            case NORTH, SOUTH -> BlockPos.betweenClosed(center.offset(-1, -1, 0), center.offset(1, 1, 0));
            case EAST, WEST -> BlockPos.betweenClosed(center.offset(0, -1, -1), center.offset(0, 1, 1));
            default -> BlockPos.betweenClosed(center.offset(-1, 0, -1), center.offset(1, 0, 1));
        };
    }

    private int breakBlock(Player player, BlockPos pos, ItemStack stack, int stored, int blocksToBreak, int usagePerBlock) {
        Level level = player.level();
        BlockState state = level.getBlockState(pos);

        if (!PaxelUtils.canMine(level, pos, state) || blocksToBreak <= 0 || stored < usagePerBlock) {
            return blocksToBreak;
        }

        boolean dropBlock = !PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.STORAGE_LINK);

        level.destroyBlock(pos, dropBlock);

        if (!dropBlock && level instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            int droppedExp = EnchantmentHelper.processBlockExperience(serverLevel, stack, state.getExpDrop(level, pos, blockEntity, player, stack));
            PaxelUtils.handleBlockDrops(player, stack, serverLevel, Block.getDrops(state, serverLevel, pos, blockEntity), pos, state, droppedExp);

            if (droppedExp > 0) {
                state.getBlock().popExperience(serverLevel, pos, droppedExp);
            }
        }

        return --blocksToBreak;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
            modifyEnergyAttributes(stack);
        }
    }

    public void initEnergyStorage(EnergyHandler energyHandler, ItemStack stack) {
        if (energyHandler.getAmountAsInt() < ENERGY_USAGE) {
            removeToolsAndAttributes(stack);
        } else {
            addToolsAndAttributes(this.material, stack);
        }
    }

    public void modifyEnergyAttributes(ItemStack stack) {
        EnergyHandler energyHandler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));

        int oldAmount = energyHandler.getAmountAsInt();
        try (Transaction tx = Transaction.openRoot()) {
            energyHandler.extract(ENERGY_USAGE, tx);
            tx.commit();
        }

        if (energyHandler.getAmountAsInt() < ENERGY_USAGE) {
            removeToolsAndAttributes(stack);
        } else if (oldAmount < ENERGY_USAGE) {
            addToolsAndAttributes(this.material, stack);
        }
    }

    public static void addToolsAndAttributes(ToolMaterial material, ItemStack stack) {
        HolderGetter<Block> registrationLookup = BuiltInRegistries.BLOCK;
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, createToolAttributes(material, 4f, -2.6f));
        stack.set(DataComponents.TOOL, new Tool(List.of(
                Tool.Rule.deniesDrops(registrationLookup.getOrThrow(material.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(registrationLookup.getOrThrow(PaxelzTags.Blocks.PAXEL_MINEABLE), material.speed())
        ), 1.0F, 1, true));
    }

    public static void removeToolsAndAttributes(ItemStack stack) {
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        stack.remove(DataComponents.TOOL);
    }

    private static ItemAttributeModifiers createToolAttributes(ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamageBaseline + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeedBaseline, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
            EnergyHandler energyHandler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
            float ratio = (float) energyHandler.getAmountAsInt() / energyHandler.getCapacityAsInt();
            return Math.round(13.0F - ((1 - ratio) * 13.0F));
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
            return COLOR;
        }
        return super.getBarColor(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE) || super.isBarVisible(stack);
    }

    @Override
    public @NonNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.get(PaxelzComponents.UPGRADES).maxUpgrades() > 0 ? new PaxelTooltipComponent(itemStack.get(PaxelzComponents.UPGRADES)) : null);
    }

    //    @Override
//    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
//        if (PaxelUtils.hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
//            addEnergyTooltip(builder, stack);
//        }
//        if (stack.has(PaxelzComponents.UPGRADES.get())) {
//            UpgradesComponent upgradesComponent = stack.get(PaxelzComponents.UPGRADES.get());
//            if (upgradesComponent.maxUpgrades() != 0) {
//                upgradesComponent.addTooltip(builder);
//            }
//        }
///    }

    private static void addEnergyTooltip(Consumer<Component> tooltip, ItemStack itemStack) {
        EnergyHandler energyHandler = itemStack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(itemStack));
        if (energyHandler != null) {
            tooltip.accept(
                    Component.translatable("tooltip.paxelz.energy_stored")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.translatable("tooltip.paxelz.energy_amount", energyHandler.getAmountAsInt(), energyHandler.getCapacityAsInt())
                                    .withColor(ARGB.color(255, 245, 192, 89)))
                            .append(" ")
                            .append(Component.literal("FE")
                                    .withColor(ARGB.color(255, 245, 192, 89)))
            );
        }
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }
}
