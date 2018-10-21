package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.utils.ItemCreator;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum GuiItem {

    FILL_ITEM(CompMaterial.BLACK_STAINED_GLASS_PANE, 1, "&a", new String[]{}),
    CLOSE_GUI(CompMaterial.BARRIER, 1, "&cClose GUI", new String[]{"§7Click to close this GUI"}),
    NEXT_PAGE(CompMaterial.PAPER, 1, "&eNext Page", new String[]{"§7Click to open up next page"}),
    PREV_PAGE(CompMaterial.PAPER, 1, "&ePrevious Page", new String[]{"§7Click to open up previous page"});

    private ItemStack itemStack;
    GuiItem(CompMaterial m, int amount, String displayName, String[] lore) {
        this.itemStack = ItemCreator.create(m,amount,displayName, Arrays.asList(lore),null,null);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
