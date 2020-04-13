package me.drawethree.buildbattle.objects.bbobjects.gui;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ConfirmationGUI implements Listener {

    private static final ItemStack YES_ITEM = ItemUtil.create(CompMaterial.LIME_STAINED_GLASS_PANE, 1, "&aYES");
    private static final ItemStack NO_ITEM = ItemUtil.create(CompMaterial.RED_STAINED_GLASS_PANE, 1, "&cNO");

    protected ItemStack item;

    @Getter
    protected Inventory inventory;

    public ConfirmationGUI(String title, ItemStack item) {
        this.item = item;
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', title));

        this.inventory.setItem(13, item);
        this.inventory.setItem(27, YES_ITEM);
        this.inventory.setItem(28, YES_ITEM);
        this.inventory.setItem(29, YES_ITEM);
        this.inventory.setItem(36, YES_ITEM);
        this.inventory.setItem(37, YES_ITEM);
        this.inventory.setItem(38, YES_ITEM);
        this.inventory.setItem(45, YES_ITEM);
        this.inventory.setItem(46, YES_ITEM);
        this.inventory.setItem(47, YES_ITEM);

        this.inventory.setItem(33, NO_ITEM);
        this.inventory.setItem(34, NO_ITEM);
        this.inventory.setItem(35, NO_ITEM);
        this.inventory.setItem(42, NO_ITEM);
        this.inventory.setItem(43, NO_ITEM);
        this.inventory.setItem(44, NO_ITEM);
        this.inventory.setItem(51, NO_ITEM);
        this.inventory.setItem(52, NO_ITEM);
        this.inventory.setItem(53, NO_ITEM);

        BuildBattle.getInstance().getServer().getPluginManager().registerEvents(this, BuildBattle.getInstance());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().equals(this.inventory)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().isSimilar(YES_ITEM)) {
                    onYesClick((Player) e.getWhoClicked());
                } else if (e.getCurrentItem().isSimilar(NO_ITEM)) {
                    onNoClick((Player) e.getWhoClicked());
                }
            }
        }
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent e) {
        if (e.getInventory() != null && e.getInventory().equals(this.inventory)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }


    protected abstract void onYesClick(Player player);

    protected abstract void onNoClick(Player player);


}
