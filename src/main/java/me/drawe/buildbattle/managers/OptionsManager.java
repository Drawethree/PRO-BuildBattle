package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.objects.bbobjects.BBBannerCreator;
import me.drawe.buildbattle.objects.bbobjects.BBDyeColor;
import me.drawe.buildbattle.objects.bbobjects.BBParticle;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlotParticle;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlotTime;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.StringUtils;
import me.kangarko.compatbridge.model.CompDye;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class OptionsManager {

    private static OptionsManager ourInstance = new OptionsManager();
    private static ItemStack deleteArenaItem = ItemUtil.create(CompMaterial.BARRIER, 1, "&eDelete Arena", ItemUtil.makeLore("&4&lWARNING!", "&cThis action cannot be undone !"), null, null);
    private static ItemStack saveItem = ItemUtil.create(CompMaterial.EMERALD, 1, "&eSave & Close", ItemUtil.makeLore("&7Click to save and apply changes !"), null, null);
    private static ItemStack backItem = ItemUtil.create(CompMaterial.ARROW, 1, Message.ITEMS_BACK_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("items.back_item.lore")), null, null);
    private static ItemStack reportItem = ItemUtil.create(CompMaterial.ENCHANTED_BOOK, 1, Message.ITEMS_REPORT_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("items.report_item.lore")), null, null);
    private static ItemStack clearPlotItem = ItemUtil.create(CompMaterial.BARRIER, 1, Message.GUI_OPTIONS_CLEAR_PLOT_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.clear_plot_item.lore")), null, null);
    private static ItemStack removeParticlesItem = ItemUtil.create(CompMaterial.CHEST, 1, Message.GUI_OPTIONS_PARTICLE_LIST_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.particle_list_item.lore")), null, null);
    private static ItemStack particlesItem = ItemUtil.create(CompMaterial.BLAZE_POWDER, 1, Message.GUI_OPTIONS_PARTICLES_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.particles_item.lore")), null, null);
    private static ItemStack biomesItem = ItemUtil.create(CompMaterial.FILLED_MAP, 1, Message.GUI_OPTIONS_PLOT_BIOME_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.change_biome_item.lore")), null, null);
    private static ItemStack headsItem = ItemUtil.create(CompMaterial.PLAYER_HEAD, 1, Message.GUI_OPTIONS_HEADS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.heads_item.lore")), null, null);
    private static ItemStack leaveItem = ItemUtil.create(CompMaterial.RED_BED, 1, Message.ITEMS_LEAVE_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("items.leave_item.lore")), null, null);
    private static ItemStack optionsItem = ItemUtil.create(CompMaterial.NETHER_STAR, 1, Message.ITEMS_OPTIONS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("items.options_item.lore")), null, null);
    private static ItemStack teamsItem = ItemUtil.create(CompMaterial.BLACK_BANNER, 1, Message.ITEMS_TEAMS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("items.teams_item.lore")), null, null);
    private static ItemStack timeItem = ItemUtil.create(CompMaterial.CLOCK, 1, Message.GUI_OPTIONS_CHANGE_TIME_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.time_item.lore")), null, null);
    private static ItemStack bannerCreatorItem = ItemUtil.create(CompMaterial.BLACK_BANNER, 1, Message.ITEMS_BANNER_CREATOR_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.banner_creator_item.lore")), null, null);
    private static Inventory particlesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PARTICLES_TITLE.getMessage());
    private static Inventory colorsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_COLORS_TITLE.getMessage());
    private static Inventory patternsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PATTERNS_TITLE.getMessage());
    private static Inventory biomesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_BIOMES_TITLE.getMessage());
    private static String reportsInventoryTitle = "Build Reports Page: ";

    private OptionsManager() {
    }

    static {
        loadColorsInventory();
        loadPatternsInventory();
        loadParticlesInventory();
        loadBiomesInventory();
    }

    private static void loadBiomesInventory() {
        biomesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_BIOMES_TITLE.getMessage());
        for (PlotBiome biome : PlotBiome.values()) {
            if (biome.getBiome().getBiome() != null) {
                biomesInventory.addItem(biome.getItem());
            }
        }
        biomesInventory.setItem(49, backItem);
    }

    private static void loadParticlesInventory() {
        particlesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PARTICLES_TITLE.getMessage());
        for (BBParticle particle : BBParticle.values()) {
            particlesInventory.setItem(particle.getSlot(), particle.getItemStack());
        }
        particlesInventory.setItem(45, backItem);
        particlesInventory.setItem(49, removeParticlesItem);
    }

    public static OptionsManager getInstance() {
        return ourInstance;
    }

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

    public static Inventory getPatternsInventory() {
        return patternsInventory;
    }

    public static Inventory getColorsInventory() {
        return colorsInventory;
    }

    private static void loadPatternsInventory() {
        patternsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PATTERNS_TITLE.getMessage());
        int slot = 0;
        for (PatternType type : PatternType.values()) {
            Pattern p = new Pattern(CompDye.BLACK.getDye(), type);
            ItemStack item = CompMaterial.WHITE_BANNER.toItem();
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            meta.setBaseColor(CompDye.WHITE.getDye());
            meta.addPattern(p);
            meta.setDisplayName(StringUtils.getDisplayNameOfPattern(type));
            item.setItemMeta(meta);
            patternsInventory.setItem(slot, item);
            slot += 1;
        }
        patternsInventory.setItem(50, backItem);
    }


    private static void loadColorsInventory() {
        colorsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_COLORS_TITLE.getMessage());
        int slot = 10;
        for (int i = 0; i < 16; i++) {
            BBDyeColor dyeColor = BBDyeColor.getById(i);
            colorsInventory.setItem(slot, dyeColor.getItem());
            if (slot == 16 || slot == 25 || slot == 34) {
                slot += 3;
            } else {
                slot += 1;
            }
        }
        colorsInventory.setItem(50, backItem);
    }

    public static ItemStack getBannerCreatorItem() {
        return bannerCreatorItem;
    }

    public static ItemStack getSaveItem() {
        return saveItem;
    }

    public static ItemStack getDeleteArenaItem() {
        return deleteArenaItem;
    }

    public static String getReportsInventoryTitle() {
        return reportsInventoryTitle;
    }


    public void openOptionsInventory(Player p, BBPlot plot) {
        Inventory optionsInv = Bukkit.createInventory(null, 9, Message.GUI_OPTIONS_TITLE.getMessage());
        if (BBSettings.isEnableHeadsOption()) optionsInv.setItem(0, headsItem);
        if (BBSettings.isEnableChangeFloorOption()) optionsInv.setItem(1, plot.getOptions().getCurrentFloorItem());
        if (BBSettings.isEnableTimeOption()) optionsInv.setItem(2, timeItem);
        if (BBSettings.isEnableParticleOption()) optionsInv.setItem(3, particlesItem);
        if (BBSettings.isEnabledWeatherOption()) optionsInv.setItem(4, getWeatherItemStack(plot));
        if (BBSettings.isEnableBiomeOption()) optionsInv.setItem(5, biomesItem);
        if (BBSettings.isEnableBannerCreatorOption()) optionsInv.setItem(6, bannerCreatorItem);
        if (BBSettings.isEnableClearPlotOption()) optionsInv.setItem(7, clearPlotItem);
        p.openInventory(optionsInv);
    }

    public void openTimeInventory(Player p, BBPlot plot) {
        Inventory timeInv = Bukkit.createInventory(null, 18, Message.GUI_TIME_TITLE.getMessage());
        for (BBPlotTime time : BBPlotTime.values()) {
            ItemStack item = time.getItem().clone();
            if (time == plot.getOptions().getCurrentTime()) {
                ItemMeta meta = item.getItemMeta();
                item = CompMaterial.LIME_TERRACOTTA.toItem();
                item.setItemMeta(meta);
                //ItemUtil.addGlowEffect(item);
            }
            timeInv.setItem(time.getSlot(), item);
        }
        timeInv.setItem(13, backItem);
        p.openInventory(timeInv);
    }

    public ItemStack getWeatherItemStack(BBPlot plot) {
        ItemStack item = ItemUtil.create(CompMaterial.SUNFLOWER, 1, Message.GUI_OPTIONS_CHANGE_WEATHER_ITEM_DISPLAYNAME.getMessage(), ItemUtil.convertWeatherLore(plot, BBSettings.getWeatherLore()), null, null);
        switch (plot.getOptions().getCurrentWeather()) {
            case CLEAR:
                break;
            case DOWNFALL:
                item.setType(CompMaterial.WATER_BUCKET.getMaterial());
                break;
        }
        return item;
    }

    public void giveAllPlayersItem(BBArena a, ItemStack item, int slot) {
        for (Player p : a.getPlayers()) {
            if (slot == -1) {
                p.getInventory().addItem(item);
            } else {
                p.getInventory().setItem(slot, item);
            }
        }
    }

    public void openActiveParticlesMenu(Player p, BBPlot plot) {
        Inventory inv = Bukkit.createInventory(null, 5 * 9, Message.GUI_PARTICLE_LIST_TITLE.getMessage());
        for (BBPlotParticle particle : plot.getParticles()) {
            inv.addItem(getActiveParticleItemStack(particle));
        }
        p.openInventory(inv);
    }

    public ItemStack getActiveParticleItemStack(BBPlotParticle particle) {
        return ItemUtil.create(CompMaterial.fromMaterial(particle.getParticle().getItemStack().getType()), 1, particle.getParticle().getItemStack().getItemMeta().getDisplayName(), ItemUtil.makeLore(
                Message.GUI_PARTICLE_LIST_ITEMS_LOCATION.getMessage(),
                " &7X: " + particle.getLocation().getBlockX(),
                " &7Y: " + particle.getLocation().getBlockY(),
                " &7Z: " + particle.getLocation().getBlockZ(),
                " ",
                Message.GUI_PARTICLE_LIST_ITEMS_CLICK_TO_REMOVE.getMessage()), null, null);
    }

    public BBPlotParticle getPlotParticleFromLore(BBPlot plot, List<String> lore) {
        for (BBPlotParticle particle : plot.getParticles()) {
            Location pLoc = particle.getLocation();
            int x = Integer.parseInt(lore.get(1).replaceAll(" §7X: ", ""));
            int y = Integer.parseInt(lore.get(2).replaceAll(" §7Y: ", ""));
            int z = Integer.parseInt(lore.get(3).replaceAll(" §7Z: ", ""));
            if (pLoc.getBlockX() == x && pLoc.getBlockY() == y && pLoc.getBlockZ() == z) {
                return particle;
            }
        }
        return null;
    }

    public void openColorsInventory(BBBannerCreator bbBannerCreator) {
        Inventory openInv = colorsInventory;
        openInv.setItem(48, bbBannerCreator.getCreatedBanner());
        bbBannerCreator.getPlayer().openInventory(openInv);
    }

    public void openPatternsInventory(BBBannerCreator bbBannerCreator) {
        Inventory openInv = patternsInventory;
        openInv.setItem(48, bbBannerCreator.getCreatedBanner());
        bbBannerCreator.getPlayer().openInventory(openInv);
    }
}
