package me.drawethree.buildbattle.heads;


import me.drawethree.buildbattle.heads.util.ItemStackCreator;
import me.drawethree.buildbattle.objects.Message;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * This class contains the object of CategoryPage. This object is a page in the
 * Category object. Each Category can have multiple CategoryPage objects. A
 * CategoryPage contains an inventory on which playerheads are displayed.
 *
 * @author McJeffr
 */
public class CategoryPage {

    /* Constants */
    private static final int PREVIOUS_PAGE_SLOT = 45;
    private static final int MAIN_PAGE_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 53;

    /* Attributes */
    private final Inventory PAGE;
    private final int PAGE_NUMBER;

    /**
     * Constructor for a CategoryPage object.
     *
     * @param heads A List containing the playerheads that need to be in the
     * inventory of this CategoryPage object.
     * @param description The description of this CategoryPage object.
     * @param pageNumber The page number of this CategoryPage object within the
     * Category object.
     */
    public CategoryPage(List<ItemStack> heads, String description, int pageNumber) {
        this.PAGE_NUMBER = pageNumber;
        this.PAGE = Bukkit.createInventory(null, 54, Message.GUI_HEADS_TITLE.getMessage() + ": "  + description);
        for (int i = 0; i < heads.size(); i++) {
            PAGE.setItem(i, heads.get(i));
        }
        setDefaults();
    }

    /**
     * Getter for the Inventory object that resides within this CategoryPage
     * object.
     *
     * @return The Inventory object.
     */
    public Inventory getPage() {
        return PAGE;
    }

    /**
     * Getter for the PAGE number that resides within this CategoryPage object.
     *
     * @return The PAGE number.
     */
    public int getPageNumber() {
        return PAGE_NUMBER;
    }

    /**
     * This method sets the defaults of each page. These are the previous page,
     * back to main menu and next page buttons.
     */
    private void setDefaults() {
        /* Previous page button */
            ItemStack icon = ItemStackCreator.createPlayerhead(1, Message.GUI_HEADS_PREV_PAGE.getMessage(), "MHF_ArrowLeft");
            PAGE.setItem(PREVIOUS_PAGE_SLOT, icon);
        /* Back to main menu button */
            icon = ItemStackCreator.createPlayerhead(1, Message.GUI_HEADS_MAIN_PAGE.getMessage(), "MHF_ArrowUp");
            PAGE.setItem(MAIN_PAGE_SLOT, icon);
        /* Next page button */
            icon = ItemStackCreator.createPlayerhead(1, Message.GUI_HEADS_NEXT_PAGE.getMessage(), "MHF_ArrowRight");
            PAGE.setItem(NEXT_PAGE_SLOT, icon);
    }

}