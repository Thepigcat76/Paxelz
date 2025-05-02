package com.thepigcat.paxelz.content.components;

import com.thepigcat.paxelz.content.items.PaxelItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

public class PaxelzEnergyComponent extends ComponentEnergyStorage {
    public PaxelzEnergyComponent(ItemStack parent, DataComponentType<Integer> energyComponent, int capacity) {
        super(parent, energyComponent, capacity);

        if (parent.getItem() instanceof PaxelItem paxelItem) {
            paxelItem.initEnergyStorage(this, parent);
        }
    }

}
