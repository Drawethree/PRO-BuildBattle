package me.drawe.buildbattle.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.ReportManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;


public class ItemCreator {

    public static ItemStack getPlayerSkull(Player player, String title, List<String> lore){
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(title);
        meta.setLore(convertLore(lore));
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getPlayerSkull(OfflinePlayer player, String title, List<String> lore){
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(title);
        meta.setLore(convertLore(lore));
        skull.setItemMeta(meta);
        return skull;
    }

    public static int getInventorySizeBasedOnList(List<?> list) {
        int size = 9;
        while(list.size() > size) {
            if(size == 54) {
                break;
            }
            size += 9;
        }
        return size;
    }

    public static ItemStack getItemStack(String value) {
        Material mat = null;
        int data = 0;

        String[] obj = value.split(":");

        if (obj.length == 2) {
            try {
                mat = Material.matchMaterial(obj[0]);
            } catch (Exception e) {
                return null; // material name doesn't exist
            }

            try {
                data = Integer.valueOf(obj[1]);
            } catch (NumberFormatException e) {
                return null; // data not a number
            }
        } else {
            try {
                mat = Material.matchMaterial(value);
            } catch (Exception e) {
                return null; // material name doesn't exist
            }
        }

        ItemStack item = new ItemStack(mat, 1);
        item.setDurability((short) data);
        return item;
    }

    public static List<String> convertLore(List<String> list)
    {
        List<String> lore = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            lore.add(ChatColor.translateAlternateColorCodes('&', (String)list.get(i)));
        }
        return lore;
    }

    public static List<String> convertThemeLore(BBTheme theme, List<String> list) {
        List<String> lore = new ArrayList<>();
        for(String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%theme%", theme.getName()).replaceAll("%theme_percentage%", String.valueOf(theme.getPercentage())));
        }
        return lore;
    }

    public static List<String> convertWeatherLore(BBPlot plot,List<String> list) {
        List<String> lore = new ArrayList<>();
        for(String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%weather%", plot.getOptions().getCurrentWeather().name()));
        }
        return lore;
    }


    public static List<String> makeLore(String... string)
    {
        return Arrays.asList(string);
    }

    /*
     * Easy creating an itemstack
     */
    public static ItemStack create(Material material, int amount, byte data, String displayName, List<String> lore, String[] enchantments, int[] levels)
    {
        ItemStack item = new ItemStack(material, amount, (short)data);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        meta.setLore(convertLore(lore));
        if (enchantments != null) {
            for (int i = 0;i<enchantments.length;i++)
                meta.addEnchant(ench(enchantments[i]), levels[i], true);
        }
        item.setItemMeta(meta);
        return item;
    }

    /*
     * replacing enchantment with string
     */
    private static Enchantment ench(String n)
    {
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
        for(int i = 0;i < team.getMaxPlayers();i++) {
            returnList.add(team.getPlayerName(i));
        }
        return returnList;
    }

    public static List<String> makeTeamLore(String... string) {
        return Arrays.asList(string);
    }

    public static int getInventorySize(int arenasAmount) {
        int size = 9;
        while(arenasAmount > size) {
            if(size == 54) {
                break;
            }
            size += 9;
        }
        return size;
    }

    public static ItemStack getSuperVoteItem(int amountOfSuperVotes, BBTheme theme) {
        return create(Material.PAPER, 1, (byte) 0, Message.GUI_THEME_VOTING_INVENTORY_SUPER_VOTE_DISPLAYNAME.getMessage(), convertSuperVoteLore(GameManager.getSuperVoteLore(), theme, amountOfSuperVotes), null,null);
    }

    private static List<String> convertSuperVoteLore(List<String> list, BBTheme theme, int amount) {
        List<String> lore = new ArrayList<>();
        for(String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%theme%", theme.getName()).replaceAll("%supervotes%", String.valueOf(amount)));
        }
        return lore;
    }

    public static ItemStack createReportItem(BBBuildReport report) {
        return create(Material.STAINED_CLAY, 1, report.getReportStatus().getData(), report.getReportStatus().getStatusColor() + report.getReportID(), ItemCreator.convertLore(ItemCreator.makeReportLore(report)), null,null);

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
}