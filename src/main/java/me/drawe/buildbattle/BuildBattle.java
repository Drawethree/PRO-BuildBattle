package me.drawe.buildbattle;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.SetThemeCommand;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.hooks.leaderheads.*;
import me.drawe.buildbattle.hooks.papi.BuildBattleProPlaceholders;
import me.drawe.buildbattle.listeners.NPCListener;
import me.drawe.buildbattle.listeners.PlayerListener;
import me.drawe.buildbattle.listeners.ServerListener;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.MetricsLite;
import me.drawe.buildbattle.utils.compatbridge.VersionResolver;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public final class BuildBattle extends JavaPlugin implements PluginMessageListener {

    private static BuildBattle instance;
    private static FileManager fileManager;
    private static WorldEditPlugin worldEdit;
    private static Economy econ = null;
    private static MetricsLite metrics = null;
    private static boolean debug = false;
    private static boolean useHolographicDisplays = false;
    private static boolean useLeaderHeads = false;
    private static boolean usePlaceholderAPI = false;
    private static boolean useMVdWPlaceholderAPI = false;
    private static boolean useCitizens = false;

    public static BuildBattle getInstance() {
        return instance;
    }

    public static WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        instance = this;
        fileManager = new FileManager(this);

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("");

        loadAllConfigs();
        BBSettings.loadBBSettings();
        checkForLoadingLater();

        getCommand("buildbattle").setExecutor(new BBCommand(this));
        getCommand("settheme").setExecutor(new SetThemeCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);

        ArenaManager.getInstance().loadArenas();
        PlayerManager.getInstance().loadAllPlayerStats();
        HeadInventory.loadHeads();
        hook();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lby §a§l" + getDescription().getAuthors().toString().substring(1, getDescription().getAuthors().toString().length() - 1)));
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void checkForLoadingLater() {
        if (BBSettings.isLoadPluginLater()) {
            warning("§cPlugin will be loaded after §e" + BBSettings.getLoadAfter() + "s !");
            try {
                Thread.sleep(BBSettings.getLoadAfter() * 1000);
            } catch (InterruptedException e) {
                warning("§cSomething has interrupted loading plugin later. Loading it now.");
            }
        }
    }

    private void hook() {
        useCitizens = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        useLeaderHeads = Bukkit.getPluginManager().isPluginEnabled("LeaderHeads");
        usePlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        useMVdWPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");

        if (useCitizens) {
            info("§aSuccessfully hooked into §eCitizens §a!");
            Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(this), this);
        }
        if (useHolographicDisplays) {
            info("§aSuccessfully hooked into §eHolographicDisplays §a!");
            LeaderboardManager.getInstance().loadAllLeaderboards();
        }
        if (usePlaceholderAPI) {
            info("§aSuccessfully hooked into §ePlaceholderAPI §a!");
            new BuildBattleProPlaceholders(this).hook();
        }
        if (useMVdWPlaceholderAPI) {
            info("§aSuccessfully hooked into §eMVdWPlaceholderAPI §a!");
            registerMvdWPlaceholders();
        }
        if (useLeaderHeads) {
            info("§aSuccessfully hooked into §eLeaderHeads §a!");
            new BuildBattleWins();
            new BuildBattlePlayed();
            new BuildBattleBlocksPlaced();
            new BuildBattleMostPoints();
            new BuildBattleSuperVotes();
            new BuildBattleParticlesPlaced();
        }

        loadWorldEdit();
        metrics = new MetricsLite(this);
    }


    public void reloadPlugin() {
        //DISABLING
        if (BBSettings.getStatsType() == StatsType.MYSQL) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
            } catch (Exception e) {
                severe("§cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }

        for (BBArena a : ArenaManager.getArenas().values()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }

        //ENABLING
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("");

        reloadAllConfigs();
        BBSettings.loadBBSettings();
        Message.reloadMessages();
        ArenaManager.getInstance().loadArenas();
        PlayerManager.getInstance().loadAllPlayerStats();

        if (useHolographicDisplays) {
            for (Hologram h : HologramsAPI.getHolograms(this)) {
                h.delete();
            }
            LeaderboardManager.getInstance().loadAllLeaderboards();
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lby §a§l" + getDescription().getAuthors().toString().substring(1, getDescription().getAuthors().toString().length() - 1)));
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void registerMvdWPlaceholders() {
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_wins",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getWins());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_played",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getPlayed());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_most_points",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getMostPoints());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_blocks_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getBlocksPlaced());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_particles_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getParticlesPlaced());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_super_votes",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getSuperVotes());
                    }
                    return "0";
                });
    }

    private void loadAllConfigs() {
        fileManager.getConfig("config.yml").copyDefaults(true).save();
        fileManager.getConfig("arenas.yml").copyDefaults(true).save();
        fileManager.getConfig("heads.yml").copyDefaults(true).save();
        fileManager.getConfig("leaderboards.yml").copyDefaults(true).save();
        fileManager.getConfig("translates.yml").copyDefaults(true).save();
        fileManager.getConfig("signs.yml").copyDefaults(true).save();
        fileManager.getConfig("stats.yml").copyDefaults(true).save();
        fileManager.getConfig("themes.yml").copyDefaults(true).save();
        fileManager.getConfig("reports.yml").copyDefaults(true).save();
        removeUnusedPathsFromConfigs();
    }

    public void saveAllConfigs() {
        fileManager.getConfigs().values().forEach(c -> c.save());
    }

    public void reloadAllConfigs() {
        fileManager.getConfigs().values().forEach(c -> c.reload());
    }

    private void removeUnusedPathsFromConfigs() {
        fileManager.getConfig("config.yml").set("arena.themes", null).set("arena.blacklisted_themes", null).set("arena.enable_clear_plot_option", null).set("arena.end_command", null).save();
    }

    @Override
    public void onDisable() {
        if (BBSettings.getStatsType() == StatsType.MYSQL) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
            } catch (Exception e) {
                severe("§cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }
        for (BBArena a : ArenaManager.getArenas().values()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }
    }

    private void loadWorldEdit() {
        if (VersionResolver.isAtLeast1_13()) {
            warning("§cWorldEdit is not supported for versions 1.13 and above. Report features will be disabled !");
            return;
        }

        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if (worldEdit == null) {
            warning("§cWorldEdit dependency not found ! Report features will be disabled !");
        } else {
            info("§aSuccessfully hooked into §eWorldEdit §a!");
            ReportManager.getInstance().loadAllReports();
        }
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        return true;
    }

    public boolean isUseHolographicDisplays() {
        return useHolographicDisplays;
    }

    public boolean isUseCitizens() {
        return useCitizens;
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(BBSettings.getPrefix() + message);
    }

    public static void debug(String message) {
        if (debug) instance.getLogger().info("[DEBUG] " + message);
    }

    public static void severe(String message) {
        instance.getLogger().severe(message);
    }

    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(BBSettings.getPrefix() + "§4[Warning] §r" + message);
    }

    public static boolean enableDebugMode() {
        BuildBattle.debug = !BuildBattle.debug;
        return BuildBattle.debug;
    }
}

