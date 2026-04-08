package com.thepigcat.paxelz.client.items;

import com.thepigcat.paxelz.Paxelz;
import com.thepigcat.paxelz.api.upgrades.Upgrade;
import com.thepigcat.paxelz.content.items.PaxelTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ClientPaxelTooltipComponent(PaxelTooltipComponent component,
                                          List<ItemStack> upgradeItems) implements ClientTooltipComponent {
    public static final Identifier MODULE_BACKGROUND_SPRITE = Paxelz.id("upgrade_background");

    public ClientPaxelTooltipComponent(PaxelTooltipComponent component) {
        this(component, new ArrayList<>());
        for (int i = 0; i < component.component().maxUpgrades(); i++) {
            if (i < component.component().upgrades().size()) {
                this.upgradeItems.add(this.component.component().upgrades().get(i).upgradeItem().getDefaultInstance());
            } else {
                this.upgradeItems.add(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor guiGraphics) {
        List<ItemStack> items = this.upgradeItems();
        if (!Minecraft.getInstance().hasShiftDown() || this.component().component().upgrades().isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.isEmpty()) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, MODULE_BACKGROUND_SPRITE, x + i * 16, y, 16, 16);
                } else {
                    guiGraphics.fakeItem(item, x + i * 16, y);
                }
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item.isEmpty()) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, MODULE_BACKGROUND_SPRITE, x, y + i * 16, 16, 16);
                } else {
                    guiGraphics.fakeItem(item, x, y + i * 16);
                }
            }
        }
    }

    @Override
    public void extractText(GuiGraphicsExtractor guiGraphics, Font font, int x, int y) {
        List<ItemStack> items = this.upgradeItems();
        if (Minecraft.getInstance().hasShiftDown() && !this.component().component().upgrades().isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                if (i < this.component().component().upgrades().size()) {
                    MutableComponent text = Component.literal(" - ").withStyle(ChatFormatting.GRAY);
                    guiGraphics.text(font, text, x + 15, y + 4 + i * 16, -1);
                    ActiveTextCollector activeTextCollector = guiGraphics.textRenderer();
                    Component displayName = component.component().upgrades().get(i).getDisplayName().copy().withStyle(ChatFormatting.GRAY);
                    guiGraphics.drawScrollingString(activeTextCollector, font, displayName, x + 15 + font.width(text), x + 15 + font.width(text) + this.getWidth(font), y + 4 + i * 16);
                }
            }
        }
    }

    @Override
    public int getHeight(Font font) {
        return 18 + (Minecraft.getInstance().hasShiftDown() && !this.component().component().upgrades().isEmpty() ? (this.component.component().maxUpgrades() - 1) * 16 : 0);
    }

    @Override
    public int getWidth(Font font) {
        if (!Minecraft.getInstance().hasShiftDown() || this.component().component().upgrades().isEmpty()) {
            return this.component().component().maxUpgrades() * 16;
        }
        return 128;
    }

}