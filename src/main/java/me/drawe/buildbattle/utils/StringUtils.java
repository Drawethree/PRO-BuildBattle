package me.drawe.buildbattle.utils;

import org.bukkit.block.banner.PatternType;

public class StringUtils {

    public static String getDisplayNameOfPattern(PatternType type) {
        String oldName = type.name();
        String returnName = "";
        oldName = oldName.replaceAll("_", " ").toLowerCase();
        String[] allNames = oldName.split(" ");
        for(String s : allNames) {
            returnName = returnName + s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
        }
        return returnName;
    }
}
