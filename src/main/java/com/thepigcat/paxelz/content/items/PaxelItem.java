package com.thepigcat.paxelz.content.items;

import com.thepigcat.paxelz.PaxelzRegistries;
import com.thepigcat.paxelz.PaxelzTags;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.client.ClientWallPhaseManager;
import com.thepigcat.paxelz.content.attachments.PassThroughBlocksAttachment;
import com.thepigcat.paxelz.content.components.UpgradesComponent;
import com.thepigcat.paxelz.registries.PaxelzAttachments;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzUpgrades;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.function.*;

public class PaxelItem extends DiggerItem {
    // FROM FTB Ultimine :3, thanks to the FTB Team
    private static final List<BlockPos> NEIGHBOR_POSITIONS = new ArrayList<>(26);
    // all blocks in 5x5 square around block's Y-level, plus blocks directly above & below
    private static final List<BlockPos> NEIGHBOR_POSITIONS_PLANT = new ArrayList<>(26);

    public static final int COLOR = FastColor.ARGB32.color(255, 215, 0, 0);
    public static final int ENERGY_USAGE = 8;
    private final Tier tier;

    public PaxelItem(Tier tier, Properties properties) {
        super(tier, PaxelzTags.Blocks.PAXEL_MINEABLE, properties);
        this.tier = tier;
    }

    private static void damageItem(Player player1, ItemStack itemStack) {
        if (hasUpgrade(itemStack, PaxelzUpgrades.ENERGY_STORAGE)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            energyStorage.extractEnergy(ENERGY_USAGE, false);
        } else {
            itemStack.hurtAndBreak(1, player1, EquipmentSlot.MAINHAND);
        }
    }

    private static boolean canBeDamaged(ItemStack itemStack) {
        if (hasUpgrade(itemStack, PaxelzUpgrades.ENERGY_STORAGE)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            return energyStorage.getEnergyStored() >= ENERGY_USAGE;
        } else {
            return itemStack.getMaxDamage() - itemStack.getDamageValue() >= 1;
        }
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
        if (hasUpgrade(itemInHand, PaxelzUpgrades.STORAGE_LINK) && context.getPlayer().isShiftKeyDown()) {
            if (context.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, context.getClickedPos(), null) != null) {
                itemInHand.set(PaxelzComponents.STORAGE_LINK.get(), Optional.of(context.getClickedPos()));
                if (!context.getLevel().isClientSide()) {
                    context.getPlayer().sendSystemMessage(Component.translatable("message.paxelz.storage_link").withStyle(ChatFormatting.YELLOW));
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (hasUpgrade(itemInHand, PaxelzUpgrades.SPELUNKER) && !context.getPlayer().isShiftKeyDown()) {
            PassThroughBlocksAttachment blocks = PassThroughBlocksAttachment.withBlocks(get3x3MiningArea(context.getClickedPos(), context.getClickedFace()));
            context.getPlayer().setData(PaxelzAttachments.PASS_THROUGH_BLOCKS.get(), blocks);
            if (context.getLevel().isClientSide()) {
                ClientWallPhaseManager.WALL_PHASE_BLOCKS.clear();
                ClientWallPhaseManager.WALL_PHASE_BLOCKS.addAll(blocks.blocks());
            }
            context.getPlayer().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        if (hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
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
        if (miningEntity instanceof Player player) {
            boolean hasUpgrade = hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE);
            int energyStored = 0;
            if (hasUpgrade) {
                IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                energyStored = energyStorage.getEnergyStored();
            }

            if (hasUpgrade(stack, PaxelzUpgrades.VEIN_MINER) && !hasUpgrade(stack, PaxelzUpgrades.AREA_MINING)) {
                veinMine(stack, player, pos);
            }

            if (hasUpgrade(stack, PaxelzUpgrades.AREA_MINING)) {
                BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if (!player.isShiftKeyDown()) {
                    mine3x3(player, pos, stack, hitResult.getDirection(), hasUpgrade ? energyStored : stack.getMaxDamage() - stack.getDamageValue(), hasUpgrade ? ENERGY_USAGE : 1, PaxelItem::damageItem);
                }
            }
            if (hasUpgrade) {
                modifyEnergyAttributes(stack);
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    private void veinMine(ItemStack stack, Player player, BlockPos pos) {
        veinMine(player, pos, stack, PaxelItem::canBeDamaged, PaxelItem::damageItem);
    }

    private void veinMine(Player player, BlockPos pos, ItemStack stack, Predicate<ItemStack> canBeDamagedFunction, BiConsumer<Player, ItemStack> damageFunction) {
        Set<BlockPos> known = new ObjectArraySet<>();
        Level level = player.level();
        walk(level, pos, known, checkCrop(level, pos));
        for (BlockPos knownPos : known) {
            BlockState blockState = level.getBlockState(knownPos);
            if (canBeDamagedFunction.test(stack) && canMine(level, knownPos, blockState)) {
                level.destroyBlock(knownPos, true);
                damageFunction.accept(player, stack);
            }
        }
    }

    private void walk(Level level, BlockPos pos, Set<BlockPos> known, boolean cropLike) {
        Set<BlockPos> traversed = new HashSet<>();
        Deque<BlockPos> openSet = new ArrayDeque<>();
        openSet.add(pos);
        traversed.add(pos);

        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();

            if (check(level, pos, ptr) && known.add(ptr)) {
                if (known.size() >= 64) {
                    return;
                }

                for (BlockPos side : cropLike ? NEIGHBOR_POSITIONS_PLANT : NEIGHBOR_POSITIONS) {
                    BlockPos offset = ptr.offset(side);

                    if (traversed.add(offset)) {
                        openSet.add(offset);
                    }
                }
            }
        }
    }

    private boolean checkCrop(Level level, BlockPos originalPos) {
        BlockState state = level.getBlockState(originalPos);
        return state.is(BlockTags.CROPS) || state.is(Blocks.COCOA) || state.is(Blocks.SUGAR_CANE) || state.is(Blocks.NETHER_WART);
    }

    private boolean check(Level level, BlockPos originalPos, BlockPos pos) {
        return level.getBlockState(originalPos).is(level.getBlockState(pos).getBlock());
    }

    private void mine3x3(Player player, BlockPos pos, ItemStack stack, Direction hitFace, int stored, int costPerBlock, BiConsumer<Player, ItemStack> drainFunction) {
        Level level = player.level();
        int blocksToBreak = Math.min(stored / costPerBlock, 9);

        Iterable<BlockPos> blocksToMine = get3x3MiningArea(pos, hitFace);

        boolean drainedFirst = true;
        for (BlockPos targetPos : blocksToMine) {
            if (blocksToBreak > 0 && canMine(level, targetPos, level.getBlockState(targetPos))) {
                if (hasUpgrade(stack, PaxelzUpgrades.VEIN_MINER)) {
                    veinMine(stack, player, pos);
                    blocksToBreak--;
                } else {
                    blocksToBreak = breakBlock(player, targetPos, stack, stored, blocksToBreak, hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE) ? ENERGY_USAGE : 1);
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

    private boolean canMine(Level level, BlockPos pos, BlockState state) {
        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE)) && level.getBlockEntity(pos) == null;
    }

    private int breakBlock(Player player, BlockPos pos, ItemStack stack, int stored, int blocksToBreak, int usagePerBlock) {
        Level level = player.level();
        BlockState state = level.getBlockState(pos);

        if (!canMine(level, pos, state) || blocksToBreak <= 0 || stored < usagePerBlock) {
            return blocksToBreak;
        }

        boolean dropBlock = !hasUpgrade(stack, PaxelzUpgrades.STORAGE_LINK);

        level.destroyBlock(pos, dropBlock);

        if (!dropBlock && level instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            int droppedExp = EnchantmentHelper.processBlockExperience(serverLevel, stack, state.getExpDrop(level, pos, blockEntity, player, stack));
            handleBlockDrops(player, stack, serverLevel, Block.getDrops(state, serverLevel, pos, blockEntity), pos, state, droppedExp);

            if (droppedExp > 0) {
                state.getBlock().popExperience(serverLevel, pos, droppedExp);
            }
        }

        return --blocksToBreak;
    }

    private static boolean hasUpgrade(ItemStack stack, Supplier<? extends Upgrade> upgrade) {
        return stack.get(PaxelzComponents.UPGRADES).hasUpgrade(upgrade.get());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
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
        } else if (oldAmount < ENERGY_USAGE) {
            stack.set(DataComponents.TOOL, tier.createToolProperties(PaxelzTags.Blocks.PAXEL_MINEABLE));
        }
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            float ratio = (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
            return Math.round(13.0F - ((1 - ratio) * 13.0F));
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
            return COLOR;
        }
        return super.getBarColor(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE) || super.isBarVisible(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasUpgrade(stack, PaxelzUpgrades.ENERGY_STORAGE)) {
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

    public static void handleBlockDrops(Player player, ItemStack toolStack, ServerLevel level, List<ItemStack> drops, BlockPos pos1, BlockState state, int droppedExperience) {
        Optional<BlockPos> _linkedPos = toolStack.get(PaxelzComponents.STORAGE_LINK);
        if (_linkedPos.isPresent()) {
            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, _linkedPos.get(), null);
            if (itemHandler != null) {
                List<ItemStack> remainders = new ArrayList<>();
                for (ItemStack stack : drops) {
                    ItemStack remainder = ItemHandlerHelper.insertItem(itemHandler, stack, false);
                    remainders.add(remainder);
                }
                for (ItemStack remainder : remainders) {
                    Block.popResource(player.level(), pos1, remainder);
                }
                if (level instanceof ServerLevel serverLevel) {
                    state.getBlock().popExperience(serverLevel, pos1, droppedExperience);
                }
            }
        }
    }

    static {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) NEIGHBOR_POSITIONS.add(new BlockPos(x, y, z));
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x != 0 || z != 0) NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(x, 0, z));
            }
        }
        NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(0, 1, 0));
        NEIGHBOR_POSITIONS_PLANT.add(new BlockPos(0, -1, 0));
    }

}
