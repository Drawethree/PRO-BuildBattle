package me.drawe.buildbattle;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.drawe.buildbattle.api.BuildBattleProPlaceholders;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.SetThemeCommand;
import me.drawe.buildbattle.leaderboards.Leaderboard;
import me.drawe.buildbattle.listeners.NPCListener;
import me.drawe.buildbattle.listeners.PlayerListener;
import me.drawe.buildbattle.listeners.ServerListener;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.MetricsLite;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BuildBattle extends JavaPlugin implements PluginMessageListener {

    private static BuildBattle instance;
    private static FileManager fileManager;
    private static WorldEditPlugin worldEdit;

    private boolean mysqlEnabled = false;
    private boolean useBungeecord = false;
    private boolean autoJoinPlayers = false;
    private boolean useHolographicDisplays = false;
    private boolean useCitizens = false;
    private static Economy econ = null;

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
        GameManager.getInstance().loadArenaPreferences();
        setupConfigPreferences();
        loadWorldEdit();
        useCitizens = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

        getCommand("buildbattle").setExecutor(new BBCommand());
        getCommand("settheme").setExecutor(new SetThemeCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ServerListener(), this);

        GameManager.getInstance().loadThemes();
        GameManager.getInstance().loadDefaultFloorMaterial();
        GameManager.getInstance().loadRestrictedBlocks();
        GameManager.getInstance().loadArenas();
        if(useCitizens) Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), this);
        if(isUseHolographicDisplays()) LeaderboardManager.getInstance().loadAllLeaderboards();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BuildBattleProPlaceholders(this).hook();
        }
        MetricsLite metrics = new MetricsLite(this);
    }


    public void reloadPlugin() {
        //DISABLING
        if (isMysqlEnabled()) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
                MySQL.getConnection().close();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }
        for (BBArena a : GameManager.getArenas()) {
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
        GameManager.getInstance().loadArenas();
        if(isUseHolographicDisplays()) {
            for (Hologram h : HologramsAPI.getHolograms(this)) {
                h.delete();
            }
            LeaderboardManager.getInstance().loadAllLeaderboards();
        }
    }

    public void loadAllConfigs() {
        getFileManager().getConfig("config.yml").copyDefaults(true).save();
        getFileManager().getConfig("messages.yml").copyDefaults(true).save();
        getFileManager().getConfig("arenas.yml").copyDefaults(true).save();
        getFileManager().getConfig("stats.yml").copyDefaults(true).save();
        getFileManager().getConfig("heads.yml").copyDefaults(true).save();
        getFileManager().getConfig("signs.yml").copyDefaults(true).save();
        getFileManager().getConfig("leaderboards.yml").copyDefaults(true).save();
    }

    public void saveAllConfigs() {
        getFileManager().getConfig("config.yml").save();
        getFileManager().getConfig("messages.yml").save();
        getFileManager().getConfig("arenas.yml").save();
        getFileManager().getConfig("stats.yml").save();
        getFileManager().getConfig("heads.yml").save();
        getFileManager().getConfig("signs.yml").save();
        getFileManager().getConfig("leaderboards.yml").save();
    }

    public void reloadAllConfigs() {
        getFileManager().getConfig("config.yml").reload();
        getFileManager().getConfig("messages.yml").reload();
        getFileManager().getConfig("arenas.yml").reload();
        getFileManager().getConfig("stats.yml").reload();
        getFileManager().getConfig("heads.yml").reload();
        getFileManager().getConfig("signs.yml").reload();
        getFileManager().getConfig("leaderboards.yml").reload();
    }

    @Override
    public void onDisable() {
        if (isMysqlEnabled()) {
            try {
                MySQLManager.getInstance().saveAllPlayerStats();
                MySQL.getConnection().close();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cAn exception occurred while trying to close MySQL connection !");
                e.printStackTrace();
            }
        } else {
            PlayerManager.getInstance().saveAllPlayerStatsToStatsYML();
        }
        for (BBArena a : GameManager.getArenas()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }
    }

    private void setupConfigPreferences() {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aPlayer Data >> §e" + GameManager.getStatsType() + " §a>> §e" + GameManager.getStatsType().getInfo());
        if (GameManager.getStatsType() == StatsType.MYSQL) {
            MySQL.getInstance().connect();
            MySQLManager.getInstance().loadAllPlayerStats();
        } else {
            PlayerManager.getInstance().loadAllPlayerStats();
            setMysqlEnabled(false);
        }
        if (getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.use_bungee")) {
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aBungeeCord system for BuildBattlePro loaded !");
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
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cWorldEdit dependency not found ! Many features may not work !");
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

    public boolean isUseHolographicDisplays() {
        return useHolographicDisplays;
    }

    public boolean isUseCitizens() {
        return useCitizens;
    }
}

