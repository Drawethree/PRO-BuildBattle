package me.drawe.buildbattle.heads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.heads.util.ItemStackCreator;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This object contains the attributes, constructors and methods of a Category
 * object. This object contains the information of a category as well as a list
 * of all the heads inside that category.
 *
 * @author McJeffr
 */
public class Category {

    /* Attributes */
    private ItemStack icon;
    private String description;
    private List<CategoryPage> pages;
    private BuildBattle plugin;

    /**
     * Constructor for a Category object. This constructor accepts the name of
     * the category from the heads.yml file to load in all the data
     * automatically.
     *
     * @param plugin
     * @param category The name of the category on the heads.yml file.
     */
    public Category(BuildBattle plugin, String category) {
        this.plugin = plugin;
        List<ItemStack> heads = new ArrayList<>();
        try {
            String categoryName = this.plugin.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".description");
            CompMaterial material = CompMaterial.fromStringStrict(this.plugin.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".icon.material"));
            this.icon = ItemStackCreator.createItem(material, 1, "&e" + categoryName);
            this.description = categoryName;
            this.pages = new ArrayList<>();

            ConfigurationSection section = this.plugin.getFileManager().getConfig("heads.yml").get().getConfigurationSection("categories." + category + ".heads");
            for (String playerName : section.getKeys(false)) {
                String displayName = this.plugin.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".heads." + playerName + ".description");
                ItemStack playerhead = ItemStackCreator.createPlayerhead(1, "&r" + displayName, new String[]{"&9Skull (" + categoryName + ")"}, playerName);
                heads.add(playerhead);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cThe heads.yml could not be loaded.", e);
        }

        int amountOfPages = (int) Math.ceil((heads.size() + 1) / 45.0);
        for (int i = 0; i < amountOfPages; i++) {
            int start = (i * 45);
            int end = ((i + 1) * 45);
            CategoryPage page;
            if (heads.size() >= end) {
                List<ItemStack> sub = heads.subList(start, end);
                page = new CategoryPage(sub, description, i);
            } else {
                List<ItemStack> sub = heads.subList(start, heads.size());
                page = new CategoryPage(sub, description, i);
            }
            pages.add(page);
        }
    }

    /**
     * Getter for the icon of the category.
     *
     * @return The icon of this category.
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Getter for the description of the category.
     *
     * @return The description of this category.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the page at a specific index.
     *
     * @param index The index of the page.
     * @return The CategoryPage object containing the inventory, null if the
     * index is out of bounds.
     */
    public CategoryPage getPage(int index) {
        try {
            return pages.get(index);
        } catch (IndexOutOfBoundsException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "§cPage index is out of bounds.", ex);
            return null;
        }
    }

    /**
     * Getter for the amount of pages in the category.
     *
     * @return The amount of pages in the category.
     */
    public int getAmountOfPages() {
        return pages.size();
    }

}