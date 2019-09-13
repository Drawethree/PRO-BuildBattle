package me.drawe.buildbattle;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import me.drawe.buildbattle.api.BuildBattleProAPI;
import me.drawe.buildbattle.api.BuildBattleProAPIImpl;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.SetThemeCommand;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.listeners.PlayerListener;
import me.drawe.buildbattle.listeners.ServerListener;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStatsLoader;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.MetricsLite;
import me.drawe.buildbattle.utils.compatbridge.MinecraftVersion;
import me.drawe.spectateapi.SpectatorManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.Field;

@Getter
public final class BuildBattle extends JavaPlugin implements PluginMessageListener {

    private static BuildBattle instance;
    private static BuildBattleProAPI API;
    private boolean debug = false;

    private FileManager fileManager;
    private ArenaManager arenaManager;
    private BannerCreatorManager bannerCreatorManager;
    private LeaderboardManager leaderboardManager;
    private MySQLManager mySQLManager;
    private OptionsManager optionsManager;
    private PartyManager partyManager;
    private PlayerManager playerManager;
    private ReportManager reportManager;
    private RewardManager rewardManager;
    private SuperVoteManager superVoteManager;
    private VotingManager votingManager;
    private SpectatorManager spectatorManager;
    private SignManager signManager;
    private BBSettings settings;
    private WorldEditPlugin worldEdit;
    private Economy econ;
    private MetricsLite metrics;
    private HeadInventory headInventory;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("");

        instance = this;
        API = new BuildBattleProAPIImpl(instance);

        this.fileManager = new FileManager(this);
        this.loadAllConfigs();

        this.settings = new BBSettings(this);
        this.settings.loadSettings();

        if (this.settings.getStatsType() == StatsType.MYSQL) {
            MySQL.getInstance().connect();
        }

        this.checkForLoadingLater();

        this.arenaManager = new ArenaManager(this);
        this.bannerCreatorManager = new BannerCreatorManager(this);
        this.leaderboardManager = new LeaderboardManager(this);
        this.mySQLManager = new MySQLManager(this);
        this.optionsManager = new OptionsManager(this);
        this.partyManager = new PartyManager(this);
        this.playerManager = new PlayerManager(this);
        this.rewardManager = new RewardManager(this);
        this.superVoteManager = new SuperVoteManager(this);
        this.votingManager = new VotingManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.signManager = new SignManager(this);

        this.registerBBCommands();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ServerListener(this), this);

        this.arenaManager.loadArenas();
        this.signManager.loadSigns();

        this.headInventory = new HeadInventory(this);
        this.hook();

        for(Player p : Bukkit.getOnlinePlayers()) {
            BBPlayerStatsLoader.load(p);
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lby §a§l" + getDescription().getAuthors().toString().substring(1, getDescription().getAuthors().toString().length() - 1)));
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void registerBBCommands() {
        try {

            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register("bb", new BBCommand(this));
            commandMap.register("settheme", new SetThemeCommand(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForLoadingLater() {
        if (this.settings.isLoadPluginLater()) {
            warning("§cPlugin will be loaded after §e" + this.settings.getLoadAfter() + "s !");
            try {
                Thread.sleep(this.settings.getLoadAfter() * 1000);
            } catch (InterruptedException e) {
                warning("§cSomething has interrupted loading plugin later. Loading it now.");
            }
        }
    }

    private void hook() {
        BBHook.attemptHooks();
        this.loadWorldEdit();
        this.metrics = new MetricsLite(this);
    }

    public static BuildBattle getInstance() {
        if(instance == null) {
            instance = new BuildBattle();
        }
        return instance;
    }


    public void reloadPlugin() {

        for (Player p : this.spectatorManager.getSpectators().keySet()) {
            this.spectatorManager.unspectate(p);
        }

        for (BBArena a : this.arenaManager.getArenas().values()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }


        //ENABLING
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lBuildBattlePro §7v." + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("");

        this.reloadAllConfigs();
        this.settings.loadSettings();
        Message.reloadMessages();
        this.optionsManager.reloadItemsAndInventories();
        this.headInventory.reload();
        this.arenaManager.loadArenas();
        this.signManager.loadSigns();

        if (BBHook.getHook("HolographicDisplays")) {
            for (Hologram h : HologramsAPI.getHolograms(this)) {
                h.delete();
            }
            this.leaderboardManager.loadAllLeaderboards();
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(FancyMessage.getCenteredMessage("§e§lby §a§l" + getDescription().getAuthors().toString().substring(1, getDescription().getAuthors().toString().length() - 1)));
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void loadAllConfigs() {
        this.fileManager.getConfig("config.yml").copyDefaults(true).save();
        this.fileManager.getConfig("arenas.yml").copyDefaults(true).save();
        this.fileManager.getConfig("heads.yml").copyDefaults(true).save();
        this.fileManager.getConfig("leaderboards.yml").copyDefaults(true).save();
        this.fileManager.getConfig("translates.yml").copyDefaults(true).save();
        this.fileManager.getConfig("signs.yml").copyDefaults(true).save();
        this.fileManager.getConfig("stats.yml").copyDefaults(true).save();
        this.fileManager.getConfig("themes.yml").copyDefaults(true).save();
        this.fileManager.getConfig("reports.yml").copyDefaults(true).save();

        this.removeUnusedPathsFromConfigs();
    }

    public void saveAllConfigs() {
        this.fileManager.getConfigs().values().forEach(c -> c.save());
    }

    public void reloadAllConfigs() {
        this.fileManager.getConfigs().values().forEach(c -> c.reload());
    }

    private void removeUnusedPathsFromConfigs() {
        this.fileManager.getConfig("config.yml").set("arena.restriced_blocks", null).set("arena.themes", null).set("arena.blacklisted_themes", null).set("arena.enable_clear_plot_option", null).set("arena.end_command", null).save();
    }

    @Override
    public void onDisable() {
        for (Player p : this.spectatorManager.getSpectators().keySet()) {
            this.spectatorManager.unspectate(p);
        }
        for (BBArena a : this.arenaManager.getArenas().values()) {
            a.stopArena(Message.RELOAD.getChatMessage(), true);
        }
    }

    private void loadWorldEdit() {
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_13)) {
            this.warning("WorldEdit is not supported for versions 1.13 and above. Report features will be disabled !");
            return;
        }
        this.worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if (this.worldEdit == null) {
            this.warning("WorldEdit dependency not found ! Report features will be disabled !");
        } else {
            this.info("Successfully hooked into WorldEdit !");
            this.reportManager = new ReportManager(this);
            this.reportManager.loadAllReports();
        }
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

    }

    public boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        this.econ = rsp.getProvider();
        return true;
    }

    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage(this.settings.getPrefix() + ChatColor.GREEN + message);
    }

    public void debug(String message) {
        if (this.debug) this.info("[DEBUG] " + message);
    }

    public void severe(String message) {
        Bukkit.getLogger().severe(message);
    }

    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(this.settings.getPrefix() + ChatColor.RED + "[Warning] " + ChatColor.RESET + message);
    }

    public boolean enableDebugMode() {
        this.debug = !this.debug;
        return this.debug;
    }

    public static BuildBattleProAPI getAPI() {
        return API;
    }
}

