package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.*;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.BBPlotTime;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.particles.BBParticle;
import me.drawe.buildbattle.particles.PlotParticle;
import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OptionsManager {
    private static OptionsManager ourInstance = new OptionsManager();

    public static OptionsManager getInstance() {
        return ourInstance;
    }

    private OptionsManager() {
    }

    private static ItemStack backItem = ItemCreator.create(Material.ARROW, 1, (byte) 0, Message.ITEMS_BACK_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("items.back_item.lore")), null,null);
    private static ItemStack reportItem = ItemCreator.create(Material.ENCHANTED_BOOK, 1, (byte) 0, Message.ITEMS_REPORT_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("items.report_item.lore")), null,null);
    private static ItemStack clearPlotItem = ItemCreator.create(Material.BARRIER, 1, (byte) 0, Message.GUI_OPTIONS_CLEAR_PLOT_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.clear_plot_item.lore")), null,null);
    private static ItemStack removeParticlesItem = ItemCreator.create(Material.CHEST, 1, (byte) 0, Message.GUI_OPTIONS_PARTICLE_LIST_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.particle_list_item.lore")), null,null);
    private static ItemStack particlesItem = ItemCreator.create(Material.BLAZE_POWDER, 1, (byte) 0, Message.GUI_OPTIONS_PARTICLES_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.particles_item.lore")), null,null);
    private static ItemStack biomesItem = ItemCreator.create(Material.EMPTY_MAP, 1, (byte) 0, "&aPlot Biome", ItemCreator.makeLore("&7Click to adjust biome of your plot"), null,null);
    private static ItemStack headsItem = ItemCreator.create(Material.SKULL_ITEM, 1, (byte) 3, Message.GUI_OPTIONS_HEADS_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.heads_item.lore")), null,null);
    private static ItemStack leaveItem = ItemCreator.create(Material.BED, 1, (byte) 0, Message.ITEMS_LEAVE_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("items.leave_item.lore")), null,null);
    private static ItemStack optionsItem = ItemCreator.create(Material.NETHER_STAR,1,(byte) 0,Message.ITEMS_OPTIONS_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("items.options_item.lore")),null,null);
    private static ItemStack teamsItem = ItemCreator.create(Material.BANNER, 1, (byte) 0, Message.ITEMS_TEAMS_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("items.teams_item.lore")), null,null);
    private static ItemStack timeItem = ItemCreator.create(Material.WATCH, 1, (byte) 0, Message.GUI_OPTIONS_CHANGE_TIME_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.time_item.lore")), null,null);
    private static Inventory particlesInventory = Bukkit.createInventory(null, 6*9, Message.GUI_PARTICLES_TITLE.getMessage());
    private static Inventory timeInventory = Bukkit.createInventory(null, 6*9, Message.GUI_TIME_TITLE.getMessage());
    private static Inventory allArenasInventory = Bukkit.createInventory(null, ArenaManager.getInstance().getArenaListSize(), Message.GUI_ARENA_LIST_TITLE.getMessage());
    private static Inventory biomesInventory = Bukkit.createInventory(null, 9, "BuildBattle - Plot Biome");

    public static ItemStack getOptionsItem() {
        return optionsItem;
    }

    public static Inventory getParticlesInventory() {
        return particlesInventory;
    }

    public static ItemStack getLeaveItem() {
        return leaveItem;
    }

    public static ItemStack getParticlesItem() {
        return particlesItem;
    }

    public static ItemStack getRemoveParticlesItem() {
        return removeParticlesItem;
    }

    public static ItemStack getTimeItem() {
        return timeItem;
    }

    public static ItemStack getClearPlotItem() {
        return clearPlotItem;
    }

    public static Inventory getAllArenasInventory() {
        return allArenasInventory;
    }

    public static ItemStack getHeadsItem() {
        return headsItem;
    }

    public static ItemStack getReportItem() {
        return reportItem;
    }

    public static ItemStack getTeamsItem() {
        return teamsItem;
    }

    public static ItemStack getBackItem() {
        return backItem;
    }

    public static ItemStack getBiomesItem() {
        return biomesItem;
    }

    public static Inventory getBiomesInventory() {
        return biomesInventory;
    }


    public void openOptionsInventory(Player p, BBPlot plot) {
        Inventory optionsInv = Bukkit.createInventory(null, 9, Message.GUI_OPTIONS_TITLE.getMessage());
        optionsInv.setItem(1, getHeadsItem());
        optionsInv.setItem(2, plot.getOptions().getCurrentFloorItem());
        optionsInv.setItem(3, getTimeItem());
        optionsInv.setItem(4, getParticlesItem());
        optionsInv.setItem(5, getWeatherItemStack(plot));
        optionsInv.setItem(6, getBiomesItem());
        optionsInv.setItem(7, getClearPlotItem());
        p.openInventory(optionsInv);
    }

    public void refreshAllArenasInventory() {
        getAllArenasInventory().clear();
        for(BBArena a : ArenaManager.getArenas()) {
            getAllArenasInventory().addItem(getArenaStatusItem(a));
        }
    }

    public void refreshArenaItem(BBArena a) {
        for (ItemStack item : getAllArenasInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName().equals(a.getName())) {
                    ItemStack replacement = getArenaStatusItem(a);
                    item.setItemMeta(replacement.getItemMeta());
                    item.setData(replacement.getData());
                    item.setDurability(replacement.getDurability());
                }
            }
        }
    }

    public void openTimeInventory(Player p, BBPlot plot) {
        Inventory timeInv = Bukkit.createInventory(null, 18, Message.GUI_TIME_TITLE.getMessage());
        for (BBPlotTime time : BBPlotTime.values()) {
            ItemStack item = time.getItem().clone();
            if (time == plot.getOptions().getCurrentTime()) {
                item.setDurability((short) 5);
            }
            timeInv.setItem(time.getSlot(), item);
        }
        timeInv.setItem(13, getBackItem());
        p.openInventory(timeInv);
    }

    static {
        for(BBParticle particle : BBParticle.values()) {
            particlesInventory.setItem(particle.getSlot(),particle.getItemStack());
        }
        particlesInventory.setItem(45, getBackItem());
        particlesInventory.setItem(49, getRemoveParticlesItem());
        for(BBArena a : ArenaManager.getArenas()) {
            getAllArenasInventory().addItem(getArenaStatusItem(a));
        }
        for(PlotBiome biome : PlotBiome.values()) {
            biomesInventory.setItem(biome.getSlot(), biome.getItem());
        }
        biomesInventory.setItem(8, getBackItem());
    }

    private static ItemStack getArenaStatusItem(BBArena a) {
        return ItemCreator.create(Material.STAINED_CLAY, 1, a.getBBArenaState().getDataValue(), a.getName(), ItemCreator.makeLore(
                " ",
                Message.ARENA_LIST_MODE.getMessage().replaceAll("%mode%", a.getGameType().getName()),
                Message.ARENA_LIST_PLAYERS.getMessage().replaceAll("%total_players%", a.getTotalPlayers()),
                Message.ARENA_LIST_STATUS.getMessage().replaceAll("%status%", a.getBBArenaState().getPrefix()),
                " ",
                Message.ARENA_LIST_CLICK_TO_JOIN.getMessage()), null,null);
    }

    public ItemStack getWeatherItemStack(BBPlot plot) {
        ItemStack item = ItemCreator.create(Material.DOUBLE_PLANT, 1,(byte) 0, Message.GUI_OPTIONS_CHANGE_WEATHER_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertWeatherLore(plot, GameManager.getWeatherLore()), null,null);
        switch(plot.getOptions().getCurrentWeather()) {
            case CLEAR:
                break;
            case DOWNFALL:
                item.setType(Material.WATER_BUCKET);
                break;
        }
        return item;
    }

    public void giveAllPlayersItem(BBArena a, ItemStack item) {
        for(Player p : a.getPlayers()) {
            p.getInventory().setItem(8, item);
        }
    }

    public void openActiveParticlesMenu(Player p, BBPlot plot) {
        Inventory inv = Bukkit.createInventory(null, 5*9,Message.GUI_PARTICLE_LIST_TITLE.getMessage());
        for(PlotParticle particle : plot.getParticles()) {
            inv.addItem(getActiveParticleItemStack(particle));
        }
        p.openInventory(inv);
    }

    public ItemStack getActiveParticleItemStack(PlotParticle particle) {
        return ItemCreator.create(particle.getParticle().getItemStack().getType(), 1, (byte) 0, particle.getParticle().getItemStack().getItemMeta().getDisplayName(), ItemCreator.makeLore(
                Message.GUI_PARTICLE_LIST_ITEMS_LOCATION.getMessage(),
                " &7X: " + particle.getLocation().getBlockX(),
                " &7Y: " + particle.getLocation().getBlockY(),
                " &7Z: " + particle.getLocation().getBlockZ(),
                " ",
                Message.GUI_PARTICLE_LIST_ITEMS_CLICK_TO_REMOVE.getMessage()), null,null);
    }

    public PlotParticle getPlotParticleFromLore(BBPlot plot, List<String> lore) {
        for(PlotParticle particle : plot.getParticles()) {
            Location pLoc = particle.getLocation();
            int x = Integer.parseInt(lore.get(1).replaceAll(" §7X: ", ""));
            int y = Integer.parseInt(lore.get(2).replaceAll(" §7Y: ", ""));
            int z = Integer.parseInt(lore.get(3).replaceAll(" §7Z: ", ""));
            if(pLoc.getBlockX() == x && pLoc.getBlockY() == y && pLoc.getBlockZ() == z) {
                return particle;
            }
        }
        return null;
    }
}
