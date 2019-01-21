package me.drawe.buildbattle.heads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.OptionsManager;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * This class contains the attributes, contructors and methods of a
 * HeadInventory object. This object contains a special constructor that creates
 * the inventory for heads.
 *
 * @author McJeffr
 */
public class HeadInventory {

    /* Attributes */
    private final Inventory MAIN_PAGE;
    private final ArrayList<Category> CATEGORIES;

    /* Instance */
    private static HeadInventory instance;

    /**
     * Constructor for a HeadInventory object. This constructor makes a new main
     * GUI that contains the categories.
     */
    private HeadInventory() {
        this.MAIN_PAGE = Bukkit.createInventory(null, 9, Message.GUI_HEADS_TITLE.getMessage());
        this.CATEGORIES = new ArrayList<>();
        try {
            ConfigurationSection section = BuildBattle.getFileManager().getConfig("heads.yml").get().getConfigurationSection("categories");
            int i = 0;
            for (String name : section.getKeys(false)) {
                Category category = new Category(name);
                CATEGORIES.add(category);
                MAIN_PAGE.setItem(i, category.getIcon());
                i++;
                }
            MAIN_PAGE.setItem(8, OptionsManager.getBackItem());
        } catch (Exception e) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cThe heads.yml could not be loaded.", e);
        }
    }

    /**
     * This method returns the instance of HeadInventory. This is the main
     * inventory containing all the categories and pages containing heads. If
     * the instance has not yet been initialized, it will initialize a new
     * HeadInventory object as the instance.
     *
     * @return The HeadInventory object that has been initialized.
     */
    public static HeadInventory getInstance() {
        if (instance == null) {
            instance = new HeadInventory();
        }
        return instance;
    }

    public static void loadHeads() {
        new BukkitRunnable() {
            @Override
            public void run() {
                instance = new HeadInventory();
            }
        }.runTaskAsynchronously(BuildBattle.getInstance());
    }

    /**
     * Getter for the main page of the GUI.
     *
     * @return The main page of the GUI.
     */
    public Inventory getMainPage() {
        return MAIN_PAGE;
    }

    /**
     * Getter for the amount of categories in the GUI.
     *
     * @return The amount of categories in the GUI.
     */
    public int getAmountOfCategories() {
        return CATEGORIES.size();
    }

    /**
     * Getter for a category based of the index. Used in looping over all
     * categories.
     *
     * @param index The index of the category.
     * @return The Category object on the index, null if the index is out of
     * bounds.
     */
    public Category getCategory(int index) {
        try {
            return CATEGORIES.get(index);
        } catch (IndexOutOfBoundsException ex) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cCategory index is out of bounds.", ex);
            return null;
        }
    }

    /**
     * This method reloads the HeadInventory's values to update the heads from
     * the yml file. This method should be used when a new head has been added
     * or an old head has been removed.
     */
    public void reload() {
        MAIN_PAGE.clear();
        CATEGORIES.clear();
        try {
            ConfigurationSection section = BuildBattle.getFileManager().getConfig("heads.yml").get().getConfigurationSection("categories");
            int i = 0;
            for (String name : section.getKeys(false)) {
                Category category = new Category(name);
                CATEGORIES.add(category);
                MAIN_PAGE.setItem(i, category.getIcon());
                i++;
            }
        } catch (Exception e) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cThe heads.yml could not be loaded.", e);
        }
    }

}