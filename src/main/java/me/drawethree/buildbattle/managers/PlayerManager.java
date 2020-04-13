package me.drawethree.buildbattle.managers;

import lombok.Getter;
import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.PlayerData;
import me.drawethree.buildbattle.objects.Votes;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStatsLoader;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import me.drawethree.buildbattle.objects.bbobjects.BBTeam;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.objects.bbobjects.scoreboards.BBMainLobbyScoreboard;
import me.drawethree.buildbattle.utils.FancyMessage;
import me.drawethree.buildbattle.utils.compatbridge.MinecraftVersion;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Getter
public class PlayerManager {

    private BuildBattle plugin;

    private HashMap<UUID, BBPlayerStats> playerStats;
    private HashMap<UUID, BBPlayerStats> cachedStats;
    private HashMap<Player, PlayerData> playerData;
    private HashMap<Player, BBArena> playersInArenas;


    public PlayerManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
        this.cachedStats = new HashMap<>();
        this.playerData = new HashMap<>();
        this.playersInArenas = new HashMap<>();
    }

    public BBPlayerStats getPlayerStats(Player p) {
        if(playerStats.get(p.getUniqueId()) == null) {
            //Try to look in cache
            return cachedStats.get(p.getUniqueId());
        } else {
            return playerStats.get(p.getUniqueId());
        }
    }

    public void showMainLobbyScoreboard(Player... players) {
        BBMainLobbyScoreboard sb;
        for (Player p : players) {
            sb = new BBMainLobbyScoreboard(p, this.getPlayerStats(p));
            sb.send(p);
        }
    }

    public void teleportToMainLobby(Player... players) {
        for (Player p : players) {
            p.teleport(this.plugin.getSettings().getMainLobbyLocation());
        }
    }

    public synchronized void loadAllPlayerStats(HashMap<UUID, BBPlayerStats> map, CountDownLatch latch) {
        for (String s : this.plugin.getFileManager().getConfig("stats.yml").get().getKeys(false)) {
            BBPlayerStats stats = new BBPlayerStats(UUID.fromString(s));
            for (BBStat stat : BBStat.values()) {
                stats.setStat(stat, this.plugin.getFileManager().getConfig("stats.yml").get().get(s + "." + stat.getConfigKey()));
            }
            map.put(stats.getUuid(), stats);
        }
        latch.countDown();
    }

    public void loadPlayerData(Player p) {

        BuildBattle.getInstance().debug("Loading data for player " + p.getName());

        //Check if data is in cache
        if (this.cachedStats.containsKey(p.getUniqueId())) {
            BuildBattle.getInstance().debug("Data is cached, loading it ");
            this.playerStats.put(p.getUniqueId(), this.cachedStats.remove(p.getUniqueId()));
            return;
        }

        BuildBattle.getInstance().debug("Data is not cached, trying to load it");
        BBPlayerStatsLoader.load(p);
    }

    public PlayerData getPlayerData(Player p) {
        return playerData.get(p);
    }

    public void broadcastToAllPlayersInArena(BBArena a, String message) {
        for (Player p : a.getPlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void removeScoreboardFromAllPlayers(BBArena a) {
        for (Player p : a.getPlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public void sendTitleToAllPlayersInArena(BBArena a, String title, String subTitle) {
        for (Player p : a.getPlayers()) {
            p.sendTitle(title, subTitle);
        }
    }

    public BBArena getPlayerArena(Player p) {
        return playersInArenas.get(p);
    }

    public boolean isPlayerInGame(Player p) {
        return getPlayerArena(p) != null;
    }

    public void createNewPlayerData(Player p) {
        playerData.put(p, new PlayerData(p));
    }

    public void setAllPlayersFlying(BBArena a) {
        for (Player p : a.getPlayers()) {
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }

    public void giveVoteItems(Player p) {
        p.getInventory().clear();
        for (Votes items : Votes.values()) {
            if (items.getItem() != null) {
                p.getInventory().addItem(items.getItem());
            }
        }
        if (this.plugin.getSettings().isReportsEnabled()) {
            p.getInventory().setItem(8, this.plugin.getOptionsManager().getReportItem());
        }
    }

    public void giveVoteItemsAllPlayers(BBArena a) {
        for (Player p : a.getPlayers()) {
            this.giveVoteItems(p);
        }
    }

    public BBTeam getPlayerTeam(BBArena a, Player p) {
        for (BBTeam t : a.getTeams()) {
            if (t.getPlayers().contains(p)) {
                return t;
            }
        }
        return null;
    }

    public void teleportAllPlayersToLobby(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            p.teleport(arenaInstance.getLobbyLocation());
        }
    }

    public void playSoundToAllPlayers(BBArena arenaInstance, CompSound sound) {
        for (Player p : arenaInstance.getPlayers()) {
            p.playSound(p.getLocation(), sound.getSound(), 1.0F, 1.0F);
        }
    }

    public void removeScoreboard(Player p) {
        p.setScoreboard(this.plugin.getServer().getScoreboardManager().getNewScoreboard());
    }

    public void restorePlayerData(Player p) {
        PlayerData pd = getPlayerData(p);
        if (pd != null) {
            pd.restorePlayerData();
            playerData.remove(p);
        }
    }

    public void setLevelsToAllPlayers(BBArena a, int timeLeft) {
        for (Player p : a.getPlayers()) {
            p.setExp(0F);
            p.setLevel(timeLeft);
        }
    }

    public void sendActionBarToAllPlayers(BBArena arenaInstance, String message) {
        if (!MinecraftVersion.atLeast(MinecraftVersion.V.v1_9)) {
            return;
        }
        for (Player p : arenaInstance.getPlayers()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    public void clearInventoryAllPlayersInArena(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            p.getInventory().clear();
        }
    }

    public void sendStartMessageToAllPlayers(BBArena a) {
        for (Player p : a.getPlayers()) {
            p.sendTitle(Message.THEME.getMessage().replace("%theme%", a.getTheme()), Message.BUILD_SOMETHING_RELEVANT.getMessage(new String[]{"%theme%"}, a.getTheme()));
            FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
            for (String s : this.plugin.getSettings().getStartMessage()) {
                FancyMessage.sendCenteredMessage(p, s.replace("%theme%", a.getTheme()));
            }
            FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
        }
    }

    public void sendMostPointsAnnounce(Player p, int oldPoints, int newPoints) {
        FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
        p.sendMessage("");
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_ANNOUNCE.getMessage());
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_OLD.getMessage().replace("%old_value%", String.valueOf(oldPoints)));
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_NEW.getMessage().replace("%new_value%", String.valueOf(newPoints)));
        p.sendMessage("");
        FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
    }

    public void sendResultsToAllPlayers(BBArena a) {
        for (Player player : a.getPlayers()) {
            BBPlot playerPlot = this.plugin.getArenaManager().getPlayerPlot(a, player);
            for (String s : this.plugin.getSettings().getEndMessage()) {
                if (s.contains("%position%")) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            BBPlot plot = a.getVotingPlots().get(i);
                            if (plot != null && plot.getTeam() != null) {
                                FancyMessage.sendCenteredMessage(player, s.replace("%position%", String.valueOf(a.getPositionOrdinal(plot))).replace("%points%", String.valueOf(plot.getVotePoints())));
                                FancyMessage.sendCenteredMessage(player, "&7" + plot.getTeam().getPlayersInCommaSeparatedString());
                            }
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                } else {
                    if (Bukkit.getServer().getPluginManager().getPlugin("PointsAPI") != null) {
                        FancyMessage.sendCenteredMessage(player, s
                                .replace("%player_position%", a.getPositionOrdinal(playerPlot))
                                .replace("%player_points%", String.valueOf(playerPlot.getVotePoints()).
                                        replace("%pointsapi_points%", String.valueOf(PointsAPI.getPoints(player)))));
                    } else {
                        FancyMessage.sendCenteredMessage(player, s
                                .replace("%player_position%", a.getPositionOrdinal(playerPlot))
                                .replace("%player_points%", String.valueOf(playerPlot.getVotePoints())));
                    }
                }
            }
        }
    }

    public boolean createPlayerStatsIfNotExists(Player p) {
        if (getPlayerStats(p) == null) {
            BBPlayerStats stats = new BBPlayerStats(p.getUniqueId(), 0, 0, 0, 0, 0, 0);
            playerStats.put(p.getUniqueId(), stats);

            if (this.plugin.getSettings().isAsyncSavePlayerData()) {
                switch (this.plugin.getSettings().getStatsType()) {
                    case FLATFILE:
                        this.addPlayerToStatsYML(stats);
                        break;
                    case MYSQL:
                        this.plugin.getMySQLManager().addPlayerToTable(stats);
                        break;
                }
            }
            this.plugin.info("§aPlayer stats for player §e" + p.getName() + " §acreated !");
            return true;
        }
        return false;
    }

    private synchronized void addPlayerToStatsYML(BBPlayerStats stats) {
        for (BBStat stat : BBStat.values()) {
            this.plugin.getFileManager().getConfig("stats.yml").set(stats.getUuid() + "." + stat.getConfigKey(), stats.getStat(stat));
        }
        this.plugin.getFileManager().getConfig("stats.yml").save();
    }

    public void addPlayedToAllPlayers(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            BBPlayerStats stats = this.plugin.getPlayerManager().getPlayerStats(p);
            if (stats != null) {
                stats.setStat(BBStat.PLAYED, (Integer) (stats.getStat(BBStat.PLAYED)) + 1);
            }
        }
    }

    public void addWinsToWinner(BBArena arena) {
        if (arena.getWinner() != null) {
            for (Player p : arena.getWinner().getTeam().getPlayers()) {
                BBPlayerStats stats = getPlayerStats(p);
                if (stats != null) {
                    stats.setStat(BBStat.WINS, (Integer) (stats.getStat(BBStat.WINS)) + 1);
                }
            }
        }
    }

    public void setAllPlayersMostPoints(BBArena a) {
        for (BBPlot plot : a.getVotingPlots()) {
            for (Player p : plot.getTeam().getPlayers()) {
                BBPlayerStats stats = this.plugin.getPlayerManager().getPlayerStats(p);
                if (stats != null) {
                    int oldPoints = (int) stats.getStat(BBStat.MOST_POINTS);
                    if (plot.getVotePoints() > oldPoints) {
                        stats.setStat(BBStat.MOST_POINTS, plot.getVotePoints());
                        if (this.plugin.getSettings().isAnnounceNewMostPoints()) {
                            sendMostPointsAnnounce(p, oldPoints, (Integer) stats.getStat(BBStat.MOST_POINTS));
                        }
                    }
                }
            }
        }
    }

    public void giveAllPlayersLeaveItem(BBArena a) {
        for (Player p : a.getPlayers()) {
            p.getInventory().setItem(8, this.plugin.getOptionsManager().getLeaveItem());
        }
    }

    public void removeAllPotionEffects(BBArena a) {
        for (Player p : a.getPlayers()) {
            for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                p.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public void giveAllPlayersTeamsItem(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            p.getInventory().setItem(0, this.plugin.getOptionsManager().getTeamsItem());
        }
    }

    public void closeInventoryAllPlayersInArena(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            p.closeInventory();
        }
    }

    public BBPlayerStats getPlayerStats(OfflinePlayer p) {
        return playerStats.get(p.getUniqueId());
    }

    public void unloadPlayerData(Player p) {

        //Data can be null in rare cases ! (Not plugin issue)
        if (getPlayerStats(p) == null) {
            return;
        }

        switch (this.plugin.getSettings().getStatsType()) {
            case FLATFILE:
                this.addPlayerToStatsYML(this.getPlayerStats(p));
                break;
            case MYSQL:
                this.plugin.getMySQLManager().savePlayerStats(this.getPlayerStats(p));
                break;
        }

        BuildBattle.getInstance().debug("Putting data of player " + p.getName() + " into cache.");
        this.cachedStats.put(p.getUniqueId(), playerStats.remove(p.getUniqueId()));
        this.runRemoveFromCacheTask(p);
    }

    private void runRemoveFromCacheTask(Player player) {
        BuildBattle.getInstance().debug("Data of player " + player.getName() + " will be remove from cache in 60 minutes.");
        new BukkitRunnable() {

            @Override
            public void run() {
                cachedStats.remove(player.getUniqueId());
                BuildBattle.getInstance().debug("Removing data of player " + player.getName() + " from cache.");
            }
        }.runTaskLater(plugin, 20 * 60 * 60);
    }

    public Double getPlayerStat(BBStat stat, Player player) {

        if (this.getPlayerStats(player) != null) {
            return ((Integer) this.getPlayerStats(player).getStat(stat)) + 0.0;
        }

        switch (this.plugin.getSettings().getStatsType()) {
            case MYSQL:
                return this.plugin.getMySQLManager().getPlayerStat(stat, player);
            case FLATFILE:
                if (this.plugin.getFileManager().getConfig("stats.yml").get().contains(player.getUniqueId().toString())) {
                    return (Double) this.plugin.getFileManager().getConfig("stats.yml").get().get(player.getUniqueId().toString() + "." + stat.getConfigKey());
                }
                break;
        }
        return 0.0;
    }

    public void savePlayerStat(BBPlayerStats bbPlayerStats, BBStat stat) {
        switch (this.plugin.getSettings().getStatsType()) {
            case MYSQL:
                this.plugin.getMySQLManager().savePlayerStat(stat, bbPlayerStats);
            case FLATFILE:
                this.plugin.getFileManager().getConfig("stats.yml").get().set(bbPlayerStats.getUuid().toString() + "." + stat.getConfigKey(), bbPlayerStats.getStat(stat));
                this.plugin.getFileManager().getConfig("stats.yml").save();
        }
    }

    public synchronized void loadPlayer(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getFileManager().getConfig("stats.yml").get().contains(p.getUniqueId().toString())) {
                    BBPlayerStats stats = new BBPlayerStats(p.getUniqueId());
                    for (BBStat stat : BBStat.values()) {
                        stats.setStat(stat, plugin.getFileManager().getConfig("stats.yml").get().get(p.getUniqueId().toString() + "." + stat.getConfigKey()));
                    }
                    playerStats.put(p.getUniqueId(), stats);
                    BuildBattle.getInstance().debug("Data for player " + p.getName() + " loaded from stats.yml!");
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}