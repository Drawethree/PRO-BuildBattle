package me.drawe.buildbattle.heads.util;

import me.drawe.buildbattle.BuildBattle;

import java.util.logging.Level;

/**
 * This class contains a few utilities for adding and removing heads to the
 * heads.yml file.
 *
 * @author McJeffr
 */
public class HeadUtil {

    /**
     * This method adds a playerhead to the heads.yml file. It does NOT reload
     * the HeadInventory object.
     *
     * @param category The category that the head needs to be added to.
     * @param name The name of the owner of the head.
     * @param description The description of the head, which will be the
     * displayname.
     * @return True if the category exists, false otherwise.
     */
    public static boolean addHead(String category, String name, String description) {
        try {
            BuildBattle.getFileManager().getConfig("heads.yml").set("categories." + category + ".heads." + name + ".description", description);
        } catch (Exception e) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE,  "§cThe heads.yml could not be loaded.", e);
            return false;
        }
        return true;
    }

    /**
     * This method removes a playerhead from the heads.yml file. It does NOT
     * reload the HeadInventory object.
     *
     * @param category The category that he head needs to be removed from.
     * @param name The name of the owner of the head.
     * @return True if the head in the category exists, false otherwise.
     */
    public static boolean removeHead(String category, String name) {
        try {
            BuildBattle.getFileManager().getConfig("heads.yml").set("categories." + category + ".heads." + name, null);
        } catch (Exception e) {
            BuildBattle.getInstance().getLogger().log(Level.SEVERE, "§cThe heads.yml could not be loaded.", e);
            return false;
        }
        return true;
    }
}