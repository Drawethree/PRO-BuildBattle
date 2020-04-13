package me.drawethree.buildbattle.managers;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.PlotBiome;
import me.drawethree.buildbattle.objects.bbobjects.BBBannerCreator;
import me.drawethree.buildbattle.objects.bbobjects.BBDyeColor;
import me.drawethree.buildbattle.objects.bbobjects.BBParticle;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlotParticle;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlotTime;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.StringUtils;
import me.drawethree.buildbattle.utils.compatbridge.model.CompDye;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
@Getter
public class OptionsManager {

    private BuildBattle plugin;

    private ItemStack deleteArenaItem;
    private ItemStack saveItem;
    private ItemStack backItem;
    private ItemStack reportItem;
    private ItemStack clearPlotItem;
    private ItemStack removeParticlesItem;
    private ItemStack particlesItem;
    private ItemStack biomesItem;
    private ItemStack headsItem;
    private ItemStack leaveItem;
    private ItemStack optionsItem;
    private ItemStack teamsItem;
    private ItemStack timeItem;
    private ItemStack bannerCreatorItem;
    private Inventory particlesInventory;
    private Inventory colorsInventory;
    private Inventory patternsInventory;
    private Inventory biomesInventory;
    private String reportsInventoryTitle;

    public OptionsManager(BuildBattle plugin) {
        this.plugin = plugin;

        this.saveItem = ItemUtil.create(CompMaterial.EMERALD, 1, "&eSave & Close", ItemUtil.makeLore("&7Click to save and apply changes !"), null, null);
        this.deleteArenaItem = ItemUtil.create(CompMaterial.BARRIER, 1, "&eDelete Arena", ItemUtil.makeLore("&7Click to open a confirmation GUI"), null, null);

        this.particlesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PARTICLES_TITLE.getMessage());
        this.colorsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_COLORS_TITLE.getMessage());
        this.patternsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PATTERNS_TITLE.getMessage());
        this.biomesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_BIOMES_TITLE.getMessage());
        this.reportsInventoryTitle = "Build Reports Page: ";

        this.reloadItemsAndInventories();
    }

    public void reloadItemsAndInventories() {

        this.backItem = ItemUtil.create(CompMaterial.ARROW, 1, Message.ITEMS_BACK_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("items.back_item.lore")), null, null);
        this.reportItem = ItemUtil.create(CompMaterial.ENCHANTED_BOOK, 1, Message.ITEMS_REPORT_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("items.report_item.lore")), null, null);
        this.clearPlotItem = ItemUtil.create(CompMaterial.BARRIER, 1, Message.GUI_OPTIONS_CLEAR_PLOT_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.clear_plot_item.lore")), null, null);
        this.removeParticlesItem = ItemUtil.create(CompMaterial.CHEST, 1, Message.GUI_OPTIONS_PARTICLE_LIST_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.particle_list_item.lore")), null, null);
        this.particlesItem = ItemUtil.create(CompMaterial.BLAZE_POWDER, 1, Message.GUI_OPTIONS_PARTICLES_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.particles_item.lore")), null, null);
        this.biomesItem = ItemUtil.create(CompMaterial.FILLED_MAP, 1, Message.GUI_OPTIONS_PLOT_BIOME_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.change_biome_item.lore")), null, null);
        this.headsItem = ItemUtil.create(CompMaterial.PLAYER_HEAD, 1, Message.GUI_OPTIONS_HEADS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.heads_item.lore")), null, null);
        this.leaveItem = ItemUtil.create(CompMaterial.RED_BED, 1, Message.ITEMS_LEAVE_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("items.leave_item.lore")), null, null);
        this.optionsItem = ItemUtil.create(CompMaterial.NETHER_STAR, 1, Message.ITEMS_OPTIONS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("items.options_item.lore")), null, null);
        this.teamsItem = ItemUtil.create(CompMaterial.BLACK_BANNER, 1, Message.ITEMS_TEAMS_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("items.teams_item.lore")), null, null);
        this.timeItem = ItemUtil.create(CompMaterial.CLOCK, 1, Message.GUI_OPTIONS_CHANGE_TIME_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.time_item.lore")), null, null);
        this.bannerCreatorItem = ItemUtil.create(CompMaterial.BLACK_BANNER, 1, Message.ITEMS_BANNER_CREATOR_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.banner_creator_item.lore")), null, null);

        this.loadColorsInventory();
        this.loadPatternsInventory();
        this.loadParticlesInventory();
        this.loadBiomesInventory();
    }

    private void loadBiomesInventory() {
        this.biomesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_BIOMES_TITLE.getMessage());
        for (PlotBiome biome : PlotBiome.values()) {
            if (biome.getBiome().getBiome() != null) {
                this.biomesInventory.addItem(biome.getItem());
            }
        }
        this.biomesInventory.setItem(49, backItem);
    }

    private void loadParticlesInventory() {
        this.particlesInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PARTICLES_TITLE.getMessage());
        for (BBParticle particle : BBParticle.values()) {
            this.particlesInventory.setItem(particle.getSlot(), particle.getItemStack());
        }
        this.particlesInventory.setItem(45, backItem);
        this.particlesInventory.setItem(49, removeParticlesItem);
    }

    private void loadPatternsInventory() {
        this.patternsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_PATTERNS_TITLE.getMessage());
        int slot = 0;

        for (PatternType type : PatternType.values()) {

            Pattern p = new Pattern(CompDye.BLACK.getDye(), type);

            ItemStack item = CompMaterial.WHITE_BANNER.toItem();
            BannerMeta meta = (BannerMeta) item.getItemMeta();

            meta.setBaseColor(CompDye.WHITE.getDye());
            meta.addPattern(p);
            meta.setDisplayName(StringUtils.getDisplayNameOfPattern(type));
            item.setItemMeta(meta);

            this.patternsInventory.setItem(slot, item);
            slot += 1;

        }

        this.patternsInventory.setItem(50, backItem);
    }


    private void loadColorsInventory() {
        this.colorsInventory = Bukkit.createInventory(null, 6 * 9, Message.GUI_COLORS_TITLE.getMessage());
        int slot = 10;
        for (DyeColor color : DyeColor.values()) {
            BBDyeColor dyeColor = BBDyeColor.getByColor(color);
            this.colorsInventory.setItem(slot, dyeColor.getItem());
            if (slot == 16 || slot == 25 || slot == 34) {
                slot += 3;
            } else {
                slot += 1;
            }
        }
        this.colorsInventory.setItem(50, backItem);
    }


    public void openOptionsInventory(Player p, BBPlot plot) {
        Inventory optionsInv = Bukkit.createInventory(null, 9, Message.GUI_OPTIONS_TITLE.getMessage());
        if (plugin.getSettings().isEnableHeadsOption()) optionsInv.addItem(headsItem);
        if (plugin.getSettings().isEnableChangeFloorOption()) optionsInv.addItem(plot.getOptions().getCurrentFloorItem());
        if (plugin.getSettings().isEnableTimeOption()) optionsInv.addItem(timeItem);
        if (plugin.getSettings().isEnableParticleOption()) optionsInv.addItem(particlesItem);
        if (plugin.getSettings().isEnabledWeatherOption()) optionsInv.addItem(getWeatherItemStack(plot));
        if (plugin.getSettings().isEnableBiomeOption()) optionsInv.addItem(biomesItem);
        if (plugin.getSettings().isEnableBannerCreatorOption()) optionsInv.addItem(bannerCreatorItem);
        if (plugin.getSettings().isEnableClearPlotOption()) optionsInv.addItem(clearPlotItem);
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
        timeInv.setItem(13, this.backItem);
        p.openInventory(timeInv);
    }

    public ItemStack getWeatherItemStack(BBPlot plot) {
        ItemStack item = ItemUtil.create(CompMaterial.SUNFLOWER, 1, Message.GUI_OPTIONS_CHANGE_WEATHER_ITEM_DISPLAYNAME.getMessage(), ItemUtil.convertWeatherLore(plot, plugin.getSettings().getWeatherLore()), null, null);
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
            int x = Integer.parseInt(lore.get(1).replace(" §7X: ", ""));
            int y = Integer.parseInt(lore.get(2).replace(" §7Y: ", ""));
            int z = Integer.parseInt(lore.get(3).replace(" §7Z: ", ""));
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
