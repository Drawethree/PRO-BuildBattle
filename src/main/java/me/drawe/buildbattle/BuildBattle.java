package me.drawe.buildbattle;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.drawe.buildbattle.api.BuildBattleProPlaceholders;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.SetThemeCommand;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.listeners.NPCListener;
import me.drawe.buildbattle.listeners.PlayerListener;
import me.drawe.buildbattle.listeners.ServerListener;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.MetricsLite;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public final class BuildBattle extends JavaPlugin implements PluginMessageListener {

    private static BuildBattle instance;
    private static FileManager fileManager;
    private static WorldEditPlugin worldEdit;
    private static boolean debug = false;
    private boolean mysqlEnabled = false;
    private boolean useBungeecord = false;
    private boolean autoJoinPlayers = false;
    private boolean useHolographicDisplays = false;
    private boolean useCitizens = false;
    protected boolean loadPluginLater = false;
    protected int loadAfter = 0;
    private static Economy econ = null;

    public static Chat getChat() {
        return chat;
    }

    private static Chat chat = null;

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
        loadAllConfigs();
        if(setPluginLoading()) {
            warning("§cPlugin will be loaded after §e" + getLoadAfter() + "s !");
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
                Bukkit.getConsoleSender().sendMessage("");
                GameManager.getInstance().loadArenaPreferences();
                setupConfigPreferences();
                loadWorldEdit();
                //setupChat();
                useCitizens = Bukkit.getPluginManager().isPluginEnabled("Citizens");
                useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

                getCommand("buildbattle").setExecutor(new BBCommand());
                getCommand("settheme").setExecutor(new SetThemeCommand());
                getServer().getPluginManager().registerEvents(new PlayerListener(), getInstance());
                getServer().getPluginManager().registerEvents(new ServerListener(), getInstance());

                GameManager.getInstance().loadThemes();
                GameManager.getInstance().loadDefaultFloorMaterial();
                GameManager.getInstance().loadRestrictedBlocks();
                ArenaManager.getInstance().loadArenas();
                ArenaManager.getInstance().loadArenaEditors();
                ReportManager.getInstance().loadAllReports();
                if(useCitizens) Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), getInstance());
                if(isUseHolographicDisplays()) LeaderboardManager.getInstance().loadAllLeaderboards();

                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    new BuildBattleProPlaceholders(getInstance()).hook();
                }
                registerMvdWPlaceholders();
                MetricsLite metrics = new MetricsLite(getInstance());
                HeadInventory.loadHeads();
            }
        }.runTaskLater(this, 20*getLoadAfter());
    }

    private boolean setPluginLoading() {
        setLoadPluginLater(fileManager.getConfig("config.yml").get().getBoolean("plugin_loading.load_plugin_later"));
        if(loadPluginLater) {
            setLoadAfter(fileManager.getConfig("config.yml").get().getInt("plugin_loading.load_after"));
        }
        return loadPluginLater;
    }


    public void reloadPlugin() {
        //DISABLING
        if (isMysqlEnabled()) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
                MySQL.getConnection().close();
            } catch (Exception e) {
                BuildBattle.severe("§cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }
        for (BBArena a : ArenaManager.getArenas()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }
        //ENABLING
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("");
        reloadAllConfigs();
        GameManager.getInstance().loadArenaPreferences();
        setupConfigPreferences();
        GameManager.getInstance().loadThemes();
        GameManager.getInstance().loadDefaultFloorMaterial();
        GameManager.getInstance().loadRestrictedBlocks();
        ArenaManager.getInstance().loadArenas();
        ArenaManager.getInstance().loadArenaEditors();
        if(isUseHolographicDisplays()) {
            for (Hologram h : HologramsAPI.getHolograms(this)) {
                h.delete();
            }
            LeaderboardManager.getInstance().loadAllLeaderboards();
        }
        ReportManager.getInstance().loadAllReports();
    }

    private void registerMvdWPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_wins",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getWins());
                        }
                        return "0";
                    });
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_played",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getPlayed());
                        }
                        return "0";
                    });
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_most_points",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getMostPoints());
                        }
                        return "0";
                    });
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_blocks_placed",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getBlocksPlaced());
                        }
                        return "0";
                    });
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_particles_placed",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getParticlesPlaced());
                        }
                        return "0";
                    });
            PlaceholderAPI.registerPlaceholder(this, "buildbattlepro_super_votes",
                    event -> {
                        OfflinePlayer offlinePlayer = event.getPlayer();
                        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                        if(pStats != null) {
                            return String.valueOf(pStats.getSuperVotes());
                        }
                        return "0";
                    });
        }
    }

    public void loadAllConfigs() {
        fileManager.getConfig("config.yml").copyDefaults(true).save();
        fileManager.getConfig("arenas.yml").copyDefaults(true).save();
        fileManager.getConfig("heads.yml").copyDefaults(true).save();
        fileManager.getConfig("leaderboards.yml").copyDefaults(true).save();
        fileManager.getConfig("messages.yml").copyDefaults(true).save();
        fileManager.getConfig("signs.yml").copyDefaults(true).save();
        fileManager.getConfig("stats.yml").copyDefaults(true).save();
        fileManager.getConfig("themes.yml").copyDefaults(true).save();
        fileManager.getConfig("reports.yml").copyDefaults(true).save();
        removeUnusedPathsFromConfigs();
    }

    public void saveAllConfigs() {
        fileManager.getConfigs().values().forEach(c-> c.save());
    }

    public void reloadAllConfigs() {
        fileManager.getConfigs().values().forEach(c-> c.reload());
    }

    protected void removeUnusedPathsFromConfigs() {
        fileManager.getConfig("config.yml").set("arena.themes", null).set("arena.blacklisted_themes", null).set("arena.enable_clear_plot_option", null).set("arena.end_command", null).save();
    }

    @Override
    public void onDisable() {
        if (isMysqlEnabled()) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
                MySQL.getConnection().close();
            } catch (Exception e) {
                BuildBattle.severe("§cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }
        for (BBArena a : ArenaManager.getArenas()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }
    }

    private void setupConfigPreferences() {
        BuildBattle.info("§aPlayer Data >> §e" + GameManager.getStatsType() + " §a>> §e" + GameManager.getStatsType().getInfo());
        if (GameManager.getStatsType() == StatsType.MYSQL) {
            MySQL.getInstance().connect();
            MySQLManager.getInstance().loadAllPlayerStats();
        } else {
            PlayerManager.getInstance().loadAllPlayerStats();
            setMysqlEnabled(false);
        }
        if (getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.use_bungee")) {
            BuildBattle.info("§aBungeeCord system for BuildBattlePro loaded !");
            setUseBungeecord(true);
            getServer().getMessenger().unregisterIncomingPluginChannel(this);
            getServer().getMessenger().unregisterOutgoingPluginChannel(this);
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
            GameManager.getInstance().loadFallbackServers();
            setAutoJoinPlayers(getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.auto_join_players"));
        }
    }

    public boolean isMysqlEnabled() {
        return mysqlEnabled;
    }

    public void setMysqlEnabled(boolean mysqlEnabled) {
        this.mysqlEnabled = mysqlEnabled;
    }

    private void loadWorldEdit() {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            BuildBattle.warning("§cWorldEdit dependency not found ! Many features may not work !");
        }
    }

    public boolean isUseBungeecord() {
        return useBungeecord;
    }

    public void setUseBungeecord(boolean useBungeecord) {
        this.useBungeecord = useBungeecord;
    }

    public boolean isAutoJoinPlayers() {
        return autoJoinPlayers;
    }

    public void setAutoJoinPlayers(boolean autoJoinPlayers) {
        this.autoJoinPlayers = autoJoinPlayers;
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

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public boolean isUseHolographicDisplays() {
        return useHolographicDisplays;
    }

    public boolean isUseCitizens() {
        return useCitizens;
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " " + message);
    }
    public static void debug(String message) {
        if(debug) instance.getLogger().info("[DEBUG] " + message);
    }
    public static void severe(String message) {
        instance.getLogger().severe(message);
    }
    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §4[Warning] §r" + message);
    }

    public boolean isLoadPluginLater() {
        return loadPluginLater;
    }

    public void setLoadPluginLater(boolean loadPluginLater) {
        this.loadPluginLater = loadPluginLater;
    }

    public int getLoadAfter() {
        return loadAfter;
    }

    public void setLoadAfter(int loadAfter) {
        this.loadAfter = loadAfter;
    }
}

