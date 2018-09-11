package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum GuiItem {

    FILL_ITEM(Material.LEGACY_STAINED_GLASS_PANE, 1, (byte) 15, "&a", new String[]{}),
    CLOSE_GUI(Material.BARRIER, 1, (byte) 0, "&cClose GUI", new String[]{"§7Click to close this GUI"}),
    NEXT_PAGE(Material.PAPER, 1, (byte) 0, "&eNext Page", new String[]{"§7Click to open up next page"}),
    PREV_PAGE(Material.PAPER, 1, (byte) 0, "&ePrevious Page", new String[]{"§7Click to open up previous page"});

    private ItemStack itemStack;
    GuiItem(Material m, int amount, byte data, String displayName, String[] lore) {
        this.itemStack = ItemCreator.create(m,amount,data,displayName, Arrays.asList(lore),null,null);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
