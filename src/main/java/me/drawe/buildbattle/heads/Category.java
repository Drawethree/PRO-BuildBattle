package me.drawe.buildbattle.heads;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.heads.util.ItemStackCreator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Default constructor of a Category object.
     *
     * @param icon The icon of this category.
     * @param description The description of this category.
     * @param pages The pages containing the heads in this category.
     */
    public Category(ItemStack icon, String description, List<CategoryPage> pages) {
        this.icon = icon;
        this.description = description;
        this.pages = pages;
    }

    /**
     * Constructor for a Category object. This constructor accepts the name of
     * the category from the heads.yml file to load in all the data
     * automatically.
     *
     * @param category The name of the category on the heads.yml file.
     */
    public Category(String category) {
        List<ItemStack> heads = new ArrayList<>();
        try {
            String categoryName = BuildBattle.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".description");
            Material material = Material.getMaterial(BuildBattle.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".icon.material"));
            short data = (short) BuildBattle.getFileManager().getConfig("heads.yml").get().getInt("categories." + category + ".icon.data");
            this.icon = ItemStackCreator.createItem(material, data, 1, "&e" + categoryName);
            this.description = categoryName;
            this.pages = new ArrayList<>();

            ConfigurationSection section = BuildBattle.getFileManager().getConfig("heads.yml").get().getConfigurationSection("categories." + category + ".heads");
            for (String playerName : section.getKeys(false)) {
                String displayName = BuildBattle.getFileManager().getConfig("heads.yml").get().getString("categories." + category + ".heads." + playerName + ".description");
                ItemStack playerhead = ItemStackCreator.createPlayerhead(1, "&r" + displayName, new String[]{"&9Skull (" + categoryName + ")"}, playerName);
                heads.add(playerhead);
            }
        } catch (Exception e) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cThe heads.yml could not be loaded.", e);
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
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cPage index is out of bounds.", ex);
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