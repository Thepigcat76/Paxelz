package com.thepigcat.paxelz.content.items;

import com.thepigcat.paxelz.content.components.UpgradesComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record PaxelTooltipComponent(UpgradesComponent component) implements TooltipComponent {
}
