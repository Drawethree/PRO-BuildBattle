package me.drawe.buildbattle.utils;

import org.bukkit.ChatColor;
import org.bukkit.block.banner.PatternType;

import java.util.List;

public class StringUtils {

    public static String getDisplayNameOfPattern(PatternType type) {
        String oldName = type.name();
        String returnName = "";
        oldName = oldName.replace("_", " ").toLowerCase();
        String[] allNames = oldName.split(" ");
        for(String s : allNames) {
            returnName = returnName + s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
        }
        return returnName;
    }

    public static List<String> colorize(List<String> list) {
        for(int i = 0; i < list.size(); i++) {
            list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
        }
        return list;
    }
}
