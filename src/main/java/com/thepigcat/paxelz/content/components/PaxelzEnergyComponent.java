package com.thepigcat.paxelz.content.components;

import com.thepigcat.paxelz.content.items.PaxelItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;

public class PaxelzEnergyComponent extends ItemAccessEnergyHandler {
    public PaxelzEnergyComponent(ItemAccess parent, ItemStack item, DataComponentType<Integer> energyComponent, int capacity) {
        super(parent, energyComponent, capacity);

        if (item.getItem() instanceof PaxelItem paxelItem) {
            paxelItem.initEnergyStorage(this, item);
        }
    }

}
