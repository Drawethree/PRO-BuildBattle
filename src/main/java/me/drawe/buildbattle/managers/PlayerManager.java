package me.drawe.buildbattle.managers;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlayerData;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.scoreboards.BBMainLobbyScoreboard;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.compatbridge.VersionResolver;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private static PlayerManager ourInstance = new PlayerManager();
    private static HashMap<UUID, BBPlayerStats> playerStats = new HashMap<>();
    private static HashMap<Player, PlayerData> playerData = new HashMap<>();
    private static HashMap<Player, BBArena> playersInArenas = new HashMap<>();

    public static PlayerManager getInstance() {
        return ourInstance;
    }

    private PlayerManager() {
    }

    public static HashMap<UUID, BBPlayerStats> getPlayerStats() {
        return playerStats;
    }

    public static HashMap<Player, PlayerData> getPlayerData() {
        return playerData;
    }

    public static HashMap<Player, BBArena> getPlayersInArenas() {
        return playersInArenas;
    }

    public BBPlayerStats getPlayerStats(Player p) {
        return playerStats.get(p.getUniqueId());
    }

    public void setMainLobbyScoreboard(Player... players) {
        BBMainLobbyScoreboard sb;
        for (Player p : players) {
            sb = new BBMainLobbyScoreboard(p, getPlayerStats(p));
            sb.send(p);
        }
    }

    public void teleportToMainLobby(Player... players) {
        for (Player p : players) {
            p.teleport(BBSettings.getMainLobbyLocation());
        }
        if (BBSettings.isMainLobbyScoreboardEnabled()) {
            setMainLobbyScoreboard(players);
        }
    }

    public void loadAllPlayerStats() {
        this.playerStats = new HashMap<>();
        if(BBSettings.getStatsType() == StatsType.MYSQL) {
            MySQL.getInstance().connect();
        } else {
            for (String s : BuildBattle.getFileManager().getConfig("stats.yml").get().getKeys(false)) {
                final int played = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".played");
                final int wins = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".wins");
                final int mostPoints = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".most_points");
                final int blocksPlaced = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".blocks_placed");
                final int particlesPlaced = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".particles_placed");
                final int superVotes = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".super_votes");
                playerStats.put(UUID.fromString(s), new BBPlayerStats(s, wins, played, mostPoints, blocksPlaced, particlesPlaced, superVotes));
            }
        }
    }

    public static PlayerData getPlayerData(Player p) {
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

    public static void giveVoteItems(Player p) {
        p.getInventory().clear();
        for (Votes items : Votes.values()) {
            if (items.getItem() != null) {
                p.getInventory().addItem(items.getItem());
            }
        }
        if (BBSettings.isReportsEnabled()) {
            p.getInventory().setItem(8, OptionsManager.getReportItem());
        }
    }

    public static void giveVoteItemsAllPlayers(BBArena a) {
        for (Player p : a.getPlayers()) {
            giveVoteItems(p);
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
        p.setScoreboard(BuildBattle.getInstance().getServer().getScoreboardManager().getNewScoreboard());
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
        if (!VersionResolver.isAtLeast1_9()) {
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
            p.sendTitle(Message.THEME.getMessage().replaceAll("%theme%", a.getTheme()), Message.BUILD_SOMETHING_RELEVANT.getMessage());
            FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
            for (String s : BBSettings.getStartMessage()) {
                FancyMessage.sendCenteredMessage(p, s.replaceAll("%theme%", a.getTheme()));
            }
            FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
        }
    }

    public void sendMostPointsAnnounce(Player p, int oldPoints, int newPoints) {
        FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
        p.sendMessage("");
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_ANNOUNCE.getMessage());
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_OLD.getMessage().replaceAll("%old_value%", String.valueOf(oldPoints)));
        FancyMessage.sendCenteredMessage(p, Message.MOST_POINTS_NEW.getMessage().replaceAll("%new_value%", String.valueOf(newPoints)));
        p.sendMessage("");
        FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
    }

    public void sendResultsToAllPlayers(BBArena a) {
        for (Player player : a.getPlayers()) {
            BBPlot playerPlot = ArenaManager.getInstance().getPlayerPlot(a, player);
            for (String s : BBSettings.getEndMessage()) {
                if (s.contains("%position%")) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            BBPlot plot = a.getVotingPlots().get(i);
                            if (plot != null && plot.getTeam() != null) {
                                FancyMessage.sendCenteredMessage(player, s.replaceAll("%position%", String.valueOf(a.getPosition(plot))).replaceAll("%points%", String.valueOf(plot.getVotePoints())));
                                FancyMessage.sendCenteredMessage(player, "&7" + plot.getTeam().getPlayersInCommaSeparatedString());
                            }
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                } else {
                    if (Bukkit.getServer().getPluginManager().getPlugin("PointsAPI") != null) {
                        FancyMessage.sendCenteredMessage(player, s
                                .replaceAll("%player_position%", a.getPosition(playerPlot))
                                .replaceAll("%player_points%", String.valueOf(playerPlot.getVotePoints()).
                                        replaceAll("%pointsapi_points%", String.valueOf(PointsAPI.getPoints(player)))));
                    } else {
                        FancyMessage.sendCenteredMessage(player, s
                                .replaceAll("%player_position%", a.getPosition(playerPlot))
                                .replaceAll("%player_points%", String.valueOf(playerPlot.getVotePoints())));
                    }
                }
            }
        }
    }

    public void createPlayerStatsIfNotExists(Player p) {
        if (getPlayerStats(p) == null) {
            BBPlayerStats stats = new BBPlayerStats(p.getUniqueId().toString(), 0, 0, 0, 0, 0, 0);
            playerStats.put(p.getUniqueId(),stats);
            if (BBSettings.isAsyncSavePlayerData()) {
                switch (BBSettings.getStatsType()) {
                    case FLATFILE:
                        addPlayerToStatsYML(stats);
                        break;
                    case MYSQL:
                        MySQLManager.getInstance().addPlayerToTable(stats);
                        break;
                }
            }
            BuildBattle.info("§aPlayer stats for player §e" + p.getName() + " §acreated !");
        }
    }

    private void addPlayerToStatsYML(BBPlayerStats stats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".played", stats.getPlayed());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".wins", stats.getWins());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".most_points", stats.getMostPoints());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".blocks_placed", stats.getBlocksPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".particles_placed", stats.getParticlesPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".super_votes", stats.getSuperVotes());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void saveAllPlayerStatsToStatsYML() {
        for (BBPlayerStats stats : playerStats.values()) {
            addPlayerToStatsYML(stats);
        }
    }

    public void addPlayedToAllPlayers(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
            if (stats != null) {
                stats.setPlayed(stats.getPlayed() + 1);
            }
        }
    }

    public void addWinsToWinner(BBArena arena) {
        if (arena.getWinner() != null) {
            for (Player p : arena.getWinner().getTeam().getPlayers()) {
                BBPlayerStats stats = getPlayerStats(p);
                if (stats != null) {
                    stats.setWins(stats.getWins() + 1);
                }
            }
        }
    }

    public void setAllPlayersMostPoints(BBArena a) {
        for (BBPlot plot : a.getVotingPlots()) {
            for (Player p : plot.getTeam().getPlayers()) {
                BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
                if (stats != null) {
                    int oldPoints = stats.getMostPoints();
                    if (plot.getVotePoints() > stats.getMostPoints()) {
                        stats.setMostPoints(plot.getVotePoints());
                        if (BBSettings.isAnnounceNewMostPoints()) {
                            sendMostPointsAnnounce(p, oldPoints, stats.getMostPoints());
                        }
                    }
                }
            }
        }
    }

    public void giveAllPlayersLeaveItem(BBArena a) {
        for (Player p : a.getPlayers()) {
            p.getInventory().setItem(8, OptionsManager.getLeaveItem());
        }
    }

    public void removeAllPotionEffects(BBArena a) {
        for (Player p : a.getPlayers()) {
            for (PotionEffect potionEffect : p.getActivePotionEffects()) {
                p.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public void savePlayerPlayed(BBPlayerStats playerStats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(playerStats.getUuid().toString() + ".played", playerStats.getPlayed());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void savePlayerMostPoints(BBPlayerStats playerStats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(playerStats.getUuid().toString() + ".most_points", playerStats.getMostPoints());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void savePlayerBlocksPlaced(BBPlayerStats playerStats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(playerStats.getUuid().toString() + ".blocks_placed", playerStats.getBlocksPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void savePlayerParticlesPlaced(BBPlayerStats playerStats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(playerStats.getUuid().toString() + ".particles_placed", playerStats.getParticlesPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void savePlayerSuperVotes(BBPlayerStats playerStats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(playerStats.getUuid().toString() + ".super_votes", playerStats.getSuperVotes());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void giveAllPlayersTeamsItem(BBArena arenaInstance) {
        for (Player p : arenaInstance.getPlayers()) {
            p.getInventory().setItem(0, OptionsManager.getTeamsItem());
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
}
