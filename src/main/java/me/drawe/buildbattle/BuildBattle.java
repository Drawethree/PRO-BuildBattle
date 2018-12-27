package me.drawe.buildbattle;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.SetThemeCommand;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.hooks.leaderheads.*;
import me.drawe.buildbattle.hooks.papi.BuildBattleProPlaceholders;
import me.drawe.buildbattle.listeners.NPCListener;
import me.drawe.buildbattle.listeners.PlayerListener;
import me.drawe.buildbattle.listeners.ServerListener;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
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

public final class BuildBattle extends JavaPlugin implements PluginMessageListener {

    private static BuildBattle instance;
    private static FileManager fileManager;
    //private static WorldEditPlugin worldEdit;
    private static boolean debug = false;
    private boolean mysqlEnabled = false;
    private boolean useBungeecord = false;
    private boolean autoJoinPlayers = false;
    private boolean useHolographicDisplays = false;
    private boolean useLeaderHeads = false;
    private boolean usePlaceholderAPI = false;
    private boolean useMVdWPlaceholderAPI = false;
    private boolean useCitizens = false;
    private boolean loadPluginLater = false;
    private int loadAfter = 0;
    private static Economy econ = null;
    private static MetricsLite metrics = null;

    public static Chat getChat() {
        return chat;
    }

    private static Chat chat = null;

    public static BuildBattle getInstance() {
        return instance;
    }

    /*public static WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }*/

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

        if (setPluginLoading()) {
            warning("§cPlugin will be loaded after §e" + getLoadAfter() + "s !");
            try {
                Thread.sleep(this.loadAfter * 1000);
            } catch (InterruptedException e) {
                warning("§cSomething has interrupted loading plugin later. Loading it now.");
            }
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§fMerry Christmas!"));
        Bukkit.getConsoleSender().sendMessage("");

        //loadWorldEdit();
        //setupChat();

        getCommand("buildbattle").setExecutor(new BBCommand());
        getCommand("settheme").setExecutor(new SetThemeCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), getInstance());
        getServer().getPluginManager().registerEvents(new ServerListener(), getInstance());

        GameManager.getInstance().loadArenaPreferences();
        setupConfigPreferences();
        GameManager.getInstance().loadThemes();
        GameManager.getInstance().loadDefaultFloorMaterial();
        GameManager.getInstance().loadRestrictedBlocks();
        ArenaManager.getInstance().loadArenas();
        ArenaManager.getInstance().loadArenaEditors();
        HeadInventory.loadHeads();

        hook();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lby §a§l" + getDescription().getAuthors().toString().substring(1, getDescription().getAuthors().toString().length() - 1)));
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void hook() {
        useCitizens = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        useLeaderHeads = Bukkit.getPluginManager().isPluginEnabled("LeaderHeads");
        usePlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        useMVdWPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");

        if (useCitizens) {
            info("§aSuccessfully hooked into §eCitizens §a!");
            Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), getInstance());
        }
        if (useHolographicDisplays) {
            info("§aSuccessfully hooked into §eHolographicDisplays §a!");
            LeaderboardManager.getInstance().loadAllLeaderboards();
        }
        if (usePlaceholderAPI) {
            info("§aSuccessfully hooked into §ePlaceholderAPI §a!");
            new BuildBattleProPlaceholders(getInstance()).hook();
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

        metrics = new MetricsLite(getInstance());
    }

    private boolean setPluginLoading() {
        setLoadPluginLater(fileManager.getConfig("config.yml").get().getBoolean("plugin_loading.load_plugin_later"));
        if (loadPluginLater) {
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
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§fMerry Christmas!"));
        Bukkit.getConsoleSender().sendMessage("");
        reloadAllConfigs();
        GameManager.getInstance().loadArenaPreferences();
        setupConfigPreferences();
        GameManager.getInstance().loadThemes();
        GameManager.getInstance().loadDefaultFloorMaterial();
        GameManager.getInstance().loadRestrictedBlocks();
        ArenaManager.getInstance().loadArenas();
        ArenaManager.getInstance().loadArenaEditors();
        if (isUseHolographicDisplays()) {
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

    public void loadAllConfigs() {
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

    /*private void loadWorldEdit() {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            BuildBattle.warning("§cWorldEdit dependency not found ! Some features may not work !");
        }
    }*/

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

    public boolean isUseHolographicDisplays() {
        return useHolographicDisplays;
    }

    public boolean isUseCitizens() {
        return useCitizens;
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + message);
    }

    public static void debug(String message) {
        if (debug) instance.getLogger().info("[DEBUG] " + message);
    }

    public static void severe(String message) {
        instance.getLogger().severe(message);
    }

    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + "§4[Warning] §r" + message);
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

    public static boolean enableDebugMode() {
        BuildBattle.debug = !BuildBattle.debug;
        return BuildBattle.debug;
    }
}

