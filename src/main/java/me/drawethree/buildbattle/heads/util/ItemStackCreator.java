package me.drawethree.buildbattle.heads.util;

import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the utility of ItemStackCreator. This utility is used to
 * quickly generate an ItemStack without having to duplicate the fairly large
 * portion of code needed to create one.
 *
 * @author McJeffr
 */
public class ItemStackCreator {

    /**
     * This method creates an ItemStack with the provided material, amount and
     * name.
     *
     * @param material The material of the ItemStack.
     * @param amount The amount of items in the ItemStack.
     * @param displayName The name of the items in the ItemStack.
     * @return An ItemStack with the provided parameters.
     */
    public static ItemStack createItem(CompMaterial material, int amount, String displayName) {
        ItemStack itemStack = material.toItem();
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * This method creates an ItemStack with the provided material, amount and
     * name.
     *
     * @param material The material of the ItemStack.
     * @param amount The amount of items in the ItemStack.
     * @param displayName The name of the items in the ItemStack.
     * @param lore The lore attached to the itmes in the ItemStack.
     * @return An ItemStack with the provided parameters.
     */
    public static ItemStack createItem(CompMaterial material, int amount, String displayName, String[] lore) {
        ItemStack itemStack = material.toItem();
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> loreList = new ArrayList();
        for (int i = 0; i < lore.length; i++) {
            String loreLine = lore[i];
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * This method creates an ItemStack containing playerheads of the provided
     * player.
     *
     * @param amount The amount of playerheads in the ItemStack.
     * @param displayName The name of the items in the ItemStack.
     * @param playerName The owner of the playerheads in the ItemStack.
     * @return An ItemStack with the provided parameters.
     */
    public static ItemStack createPlayerhead(int amount, String displayName, String playerName) {
        ItemStack itemStack = CompMaterial.PLAYER_HEAD.toItem(amount);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(playerName);
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    /**
     * This method creates an ItemStack containing playerheads of the provided
     * player.
     *
     * @param amount The amount of playerheads in the ItemStack.
     * @param displayName The name of the items in the ItemStack.
     * @param lore The lore attached to the playerheads in the ItemStack.
     * @param playerName The owner of the playerheads in the ItemStack.
     * @return An ItemStack with the provided parameters.
     */
    public static ItemStack createPlayerhead(int amount, String displayName, String[] lore, String playerName) {
        ItemStack itemStack = CompMaterial.PLAYER_HEAD.toItem(amount);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(playerName);
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> loreList = new ArrayList();
        for (int i = 0; i < lore.length; i++) {
            String loreLine = lore[i];
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        skullMeta.setLore(loreList);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

}