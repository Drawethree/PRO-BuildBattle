package me.drawe.buildbattle.utils;

import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.ReportManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawe.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.BBTheme;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemUtil {

    public static int getInventorySizeBasedOnList(List<?> list) {
        int size = 9;
        while (list.size() > size) {
            if (size == 54) {
                break;
            }
            size += 9;
        }
        return size;
    }


    public static List<String> colorizeLore(List<String> list) {
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            lore.add(ChatColor.translateAlternateColorCodes('&', list.get(i)));
        }
        return lore;
    }

    public static List<String> convertThemeLore(BBTheme theme, List<String> list, int timeLeft) {
        List<String> lore = new ArrayList<>();
        for (String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%theme%", theme.getName()).replaceAll("%theme_percentage%", String.valueOf(theme.getPercentage())).replaceAll("%time_left%", String.valueOf(timeLeft)));
        }
        return lore;
    }

    public static List<String> convertWeatherLore(BBPlot plot, List<String> list) {
        List<String> lore = new ArrayList<>();
        for (String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%weather%", plot.getOptions().getCurrentWeather().name()));
        }
        return lore;
    }


    public static List<String> makeLore(String... string) {
        return Arrays.asList(string);
    }

    /*
     * Easy creating an itemstack
     */
    public static ItemStack create(CompMaterial material, int amount, String displayName, List<String> lore, String[] enchantments, int[] levels) {
        ItemStack item = material.toItem(amount);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        meta.setLore(colorizeLore(lore));
        if (enchantments != null) {
            for (int i = 0; i < enchantments.length; i++)
                meta.addEnchant(ench(enchantments[i]), levels[i], true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(CompMaterial material, int amount, String displayName, List<String> lore, boolean glow) {
        ItemStack item = material.toItem(amount);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        meta.setLore(colorizeLore(lore));
        item.setItemMeta(meta);
        if(glow) {
            addGlowEffect(item);
        }
        return item;
    }

    /*
     * replacing enchantment with string
     */
    private static Enchantment ench(String n) {
        n = n.toLowerCase();
        n = n.replace("alldamage", "DAMAGE_ALL").replace("alldmg", "DAMAGE_ALL").replace("sharpness", "DAMAGE_ALL");
        n = n.replace("arthropodsdamage", "DAMAGE_ARTHROPODS").replace("ardmg", "DAMAGE_ARTHROPODS").replace("baneofarthropods", "DAMAGE_ARTHROPODS");
        n = n.replace("undeaddamage", "DAMAGE_UNDEAD").replace("smite ", "DAMAGE_UNDEAD");
        n = n.replace("digspeed", "DIG_SPEED").replace("efficiency", "DIG_SPEED");
        n = n.replace("durability", "DURABILITY").replace("dura", "DURABILITY").replace("unbreaking ", "DURABILITY");
        n = n.replace("fireaspect", "FIRE_ASPECT").replace("fire", "FIRE_ASPECT");
        n = n.replace("knockback ", "KNOCKBACK");
        n = n.replace("blockslootbonus", "LOOT_BONUS_BLOCKS").replace("fortune", "LOOT_BONUS_BLOCKS");
        n = n.replace("mobslootbonus", "LOOT_BONUS_MOBS").replace("mobloot", "LOOT_BONUS_MOBS").replace("looting", "LOOT_BONUS_MOBS");
        n = n.replace("oxygen", "OXYGEN").replace("respiration", "OXYGEN");
        n = n.replace("protection", "PROTECTION_ENVIRONMENTAL").replace("prot", "PROTECTION_ENVIRONMENTAL");
        n = n.replace("explosionsprotection", "PROTECTION_EXPLOSIONS").replace("expprot", "PROTECTION_EXPLOSIONS").replace("blastprotection", "PROTECTION_EXPLOSIONS");
        n = n.replace("fallprotection", "PROTECTION_FALL").replace("fallprot", "PROTECTION_FALL").replace("featherfall", "PROTECTION_FALL").replace("featherfalling", "PROTECTION_FALL");
        n = n.replace("fireprotection", "PROTECTION_FIRE").replace("fireprot", "PROTECTION_FIRE");
        n = n.replace("projectileprotection", "PROTECTION_PROJECTILE").replace("projprot", "PROTECTION_PROJECTILE");
        n = n.replace("silktouch", "SILK_TOUCH");
        n = n.replace("waterworker", "WATER_WORKER").replace("aquainfinity", "WATER_WORKER");
        n = n.replace("firearrow", "ARROW_FIRE").replace("flame", "ARROW_FIRE");
        n = n.replace("arrowdamage", "ARROW_DAMAGE").replace("power", "ARROW_DAMAGE");
        n = n.replace("arrowknockback", "ARROW_KNOCKBACK").replace("arrowkb", "ARROW_KNOCKBACK").replace("punch", "ARROW_KNOCKBACK");
        n = n.replace("infinitearrows", "ARROW_INFINITE").replace("infarrows", "ARROW_INFINITE").replace("infinity", "ARROW_INFINITE");
        n = n.toUpperCase();
        Enchantment ench = Enchantment.getByName(n);
        return ench;
    }

    public static List<String> createTeamLore(BBTeam team) {
        List<String> returnList = new ArrayList<>();
        for (int i = 0; i < team.getMaxPlayers(); i++) {
            returnList.add(team.getPlayerName(i));
        }
        return returnList;
    }


    public static ItemStack getSuperVoteItem(int amountOfSuperVotes, BBTheme theme) {
        return create(CompMaterial.PAPER, 1, Message.GUI_THEME_VOTING_INVENTORY_SUPER_VOTE_DISPLAYNAME.getMessage(), convertSuperVoteLore(BBSettings.getSuperVoteLore(), theme, amountOfSuperVotes), null, null);
    }

    private static List<String> convertSuperVoteLore(List<String> list, BBTheme theme, int amount) {
        List<String> lore = new ArrayList<>();
        for (String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%theme%", theme.getName()).replaceAll("%supervotes%", String.valueOf(amount)));
        }
        return lore;
    }

    public static ItemStack createReportItem(BBBuildReport report) {
        return create(report.getReportStatus().getStatusMaterial(), 1, report.getReportStatus().getStatusColor() + report.getReportID(), ItemUtil.colorizeLore(ItemUtil.makeReportLore(report)), null, null);

    }

    private static List<String> makeReportLore(BBBuildReport report) {
        List<String> reportLore = new ArrayList<>();
        OfflinePlayer reportedBy = Bukkit.getOfflinePlayer(report.getReportedBy());
        reportLore.add("");
        reportLore.add("§eReported by: §f" + reportedBy.getName());
        reportLore.add("§eReported players:");
        report.getReportedPlayers().forEach(uuid -> reportLore.add(" §f- " + Bukkit.getOfflinePlayer(uuid).getName()));
        reportLore.add("§eDate: §f" + ReportManager.reportDateformat.format(report.getReportDate()));
        reportLore.add("§eSchematic name: §f" + report.getSchematicFile().getName());
        reportLore.add("§eStatus: " + report.getReportStatus().getStatusColor() + report.getReportStatus().name().toUpperCase());
        reportLore.add("");
        reportLore.add("§eRight-Click §7to mark as " + BBReportStatus.SOLVED.getStatusColor() + BBReportStatus.SOLVED.name());
        reportLore.add("§eLeft-Click §7to §aLOAD §7schematic to your clipboard");
        reportLore.add("§eMiddle-Click §7to §cDELETE §7this report");
        return reportLore;
    }

    public static ItemStack create(CompMaterial m, int i, String s) {
        ItemStack item = m.toItem(i);
        ItemMeta meta = item.getItemMeta();
        if (s != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', s));
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void addGlowEffect(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    public static ItemStack create(CompMaterial material, int amount, String displayName, List<String> lore) {
        return create(material,amount,displayName,lore,null,null);
    }
}