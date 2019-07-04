package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum GUIItem {

    FILL_ITEM(XMaterial.BLACK_STAINED_GLASS_PANE, 1, "&a", new String[]{}),
    CLOSE_GUI(XMaterial.BARRIER, 1, "&cClose GUI", new String[]{"§7Click to close this GUI"}),
    NEXT_PAGE(XMaterial.PAPER, 1, "&eNext Page", new String[]{"§7Click to open up next page"}),
    PREV_PAGE(XMaterial.PAPER, 1, "&ePrevious Page", new String[]{"§7Click to open up previous page"});

    private ItemStack itemStack;

    GUIItem(XMaterial m, int amount, String displayName, String[] lore) {
        this.itemStack = ItemUtil.create(m,amount,displayName, Arrays.asList(lore),null,null);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
