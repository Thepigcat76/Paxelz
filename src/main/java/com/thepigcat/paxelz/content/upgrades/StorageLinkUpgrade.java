package com.thepigcat.paxelz.content.upgrades;

import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.registries.PaxelzComponents;
import com.thepigcat.paxelz.registries.PaxelzItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.Optional;

public class StorageLinkUpgrade implements Upgrade {
    public boolean linkStorage(UseOnContext context, ItemStack itemInHand) {
        if (context.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, context.getClickedPos(), null) != null) {
            itemInHand.set(PaxelzComponents.STORAGE_LINK.get(), Optional.of(context.getClickedPos()));
            if (!context.getLevel().isClientSide()) {
                context.getPlayer().sendSystemMessage(Component.translatable("message.paxelz.storage_link").withStyle(ChatFormatting.YELLOW));
                return true;
            }
        }
        return false;
    }

    @Override
    public Item upgradeItem() {
        return PaxelzItems.UPGRADE_STORAGE_LINK.get();
    }

    @Override
    public void onUpgradeRemoved(ItemStack stack) {
        stack.set(PaxelzComponents.STORAGE_LINK, Optional.empty());
    }

}
