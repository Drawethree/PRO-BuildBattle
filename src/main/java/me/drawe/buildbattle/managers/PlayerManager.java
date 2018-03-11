package me.drawe.buildbattle.managers;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.*;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.utils.FancyMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private static PlayerManager ourInstance = new PlayerManager();
    private static List<BBPlayerStats> playerStats = new ArrayList<>();
    private static List<PlayerData> playerData = new ArrayList<>();

    public static PlayerManager getInstance() {
        return ourInstance;
    }

    private PlayerManager() {
    }

    public static List<BBPlayerStats> getPlayerStats() {
        return playerStats;
    }

    public static List<PlayerData> getPlayerData() {
        return playerData;
    }

    public BBPlayerStats getPlayerStats(Player p) {
        for(BBPlayerStats ps : getPlayerStats()) {
            if(ps.getUuid().equals(p.getUniqueId().toString())) {
                return ps;
            }
        }
        return null;
    }

    public void setMainLobbyScoreboard(Player... players) {
        BBMainLobbyBoard sb;
        for(Player p : players) {
            sb = new BBMainLobbyBoard(p, getPlayerStats(p));
            sb.send(p);
        }
    }

    public void teleportToMainLobby(Player... players) {
        for(Player p : players) {
            p.teleport(GameManager.getMainLobbyLocation());
        }
        if(GameManager.isMainLobbyScoreboardEnabled()) {
            setMainLobbyScoreboard(players);
        }
    }

    public void loadAllPlayerStats() {
        for(String s : BuildBattle.getFileManager().getConfig("stats.yml").get().getKeys(false)) {
            String uuid = s;
            int played = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".played");
            int wins = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".wins");
            int mostPoints = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".most_points");
            int blocksPlaced = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".blocks_placed");
            int particlesPlaced = BuildBattle.getFileManager().getConfig("stats.yml").get().getInt(s + ".particles_placed");
            BBPlayerStats stats = new BBPlayerStats(uuid, wins, played, mostPoints, blocksPlaced, particlesPlaced);
            PlayerManager.getPlayerStats().add(stats);
        }
    }

    public static PlayerData getPlayerData(Player p) {
        for (PlayerData pd : getPlayerData()) {
            if (pd.getUuid().equals(p.getUniqueId())) {
                return pd;
            }
        }
        return null;
    }

    public void broadcastToAllPlayersInArena(BBArena a, String message) {
        for(Player p : a.getPlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void removeScoreboardFromAllPlayers(BBArena a) {
        for(Player p : a.getPlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public void sendTitleToAllPlayersInArena(BBArena a, String title, String subTitle) {
        for(Player p : a.getPlayers()) {
            p.sendTitle(title,subTitle);
        }
    }

    public void sendTitleToAllPlayersInArena(BBArena a, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        for(Player p : a.getPlayers()) {
            p.sendTitle(title,subTitle, fadeIn, stay, fadeOut);
        }
    }

    public BBArena getPlayerArena(Player p) {
        for(BBArena arena : ArenaManager.getArenas()) {
            if(arena.getPlayers().contains(p)) {
                return arena;
            }
        }
        return null;
    }
    public boolean isPlayerInGame(Player p) {
        for(BBArena a : ArenaManager.getArenas()) {
            if(a.getPlayers().contains(p)) {
                return true;
            }
        }
        return false;
    }

    public void createNewPlayerData(Player p) {
        PlayerData playerData = new PlayerData(p.getUniqueId(),p.getInventory().getContents(), p.getInventory().getArmorContents(),p.getLocation(),p.getGameMode(), p.getLevel(), p.getExp(), p.getAllowFlight());
        getPlayerData().add(playerData);
    }

    public void setAllPlayersFlying(BBArena a) {
        for(Player p : a.getPlayers()) {
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }

    public static void giveVoteItems(Player p) {
        p.getInventory().clear();
        for(Votes items: Votes.values()) {
            if(items.getItem() != null) {
                p.getInventory().addItem(items.getItem());
            }
        }
        if(GameManager.isReportsEnabled()) {
            p.getInventory().setItem(8, OptionsManager.getReportItem());
        }
    }

    public static void giveVoteItemsAllPlayers(BBArena a) {
        for(Player p : a.getPlayers()) {
            giveVoteItems(p);
        }
    }
    public boolean isCaptain(BBTeam t, Player p) {
        return t.getCaptain().equals(p);
    }

    public BBTeam getPlayerTeam(BBArena a, Player p) {
        for(BBTeam t : a.getTeams()) {
            if(t.getPlayers().contains(p)) {
                return t;
            }
        }
        return null;
    }

    public void teleportAllPlayersToLobby(BBArena arenaInstance) {
        for(Player p : arenaInstance.getPlayers()) {
            p.teleport(arenaInstance.getLobbyLocation());
        }
    }

    public void playSoundToAllPlayers(BBArena arenaInstance, Sound sound) {
        for(Player p : arenaInstance.getPlayers()) {
            p.playSound(p.getLocation(),sound,1.0F,1.0F);
        }
    }

    public void removeScoreboard(Player p) {
        p.setScoreboard(BuildBattle.getInstance().getServer().getScoreboardManager().getNewScoreboard());
    }

    public void restorePlayerData(Player p) {
        PlayerData pd = getPlayerData(p);
        if(pd != null) {
            pd.restorePlayerData();
        }
    }

    public void setLevelsToAllPlayers(BBArena a, int timeLeft) {
        for(Player p : a.getPlayers()) {
            p.setExp(0F);
            p.setLevel(timeLeft);
        }
    }
    public void sendActionBarToAllPlayers(BBArena arenaInstance, String message) {
        for(Player p : arenaInstance.getPlayers()) {
            if(Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            }
        }
    }

    public void clearInventoryAllPlayersInArena(BBArena arenaInstance) {
        for(Player p : arenaInstance.getPlayers()) {
            p.getInventory().clear();
        }
    }

    public void sendStartMessageToAllPlayers(BBArena a) {
        for(Player p : a.getPlayers()) {
            FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
            for(String s : GameManager.getStartMessage()) {
                FancyMessage.sendCenteredMessage(p,s.replaceAll("%theme%", a.getTheme()));
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
            for (String s : GameManager.getEndMessage()) {
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
        if(getPlayerStats(p) == null) {
            BBPlayerStats stats = new BBPlayerStats(p.getUniqueId().toString(),0,0,0,0,0);
            PlayerManager.getPlayerStats().add(stats);
            if(GameManager.isAsyncSavePlayerData()) {
                switch (GameManager.getStatsType()) {
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

    public void addPlayerToStatsYML(BBPlayerStats stats) {
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".played", stats.getPlayed());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".wins", stats.getWins());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".most_points", stats.getMostPoints());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".blocks_placed", stats.getBlocksPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").set(stats.getUuid().toString() + ".particles_placed", stats.getParticlesPlaced());
        BuildBattle.getFileManager().getConfig("stats.yml").save();
    }

    public void saveAllPlayerStatsToStatsYML() {
        for(BBPlayerStats stats : getPlayerStats()) {
            addPlayerToStatsYML(stats);
        }
    }

    public void addPlayedToAllPlayers(BBArena arenaInstance) {
        for(Player p : arenaInstance.getPlayers()) {
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
            if(stats != null) {
                stats.setPlayed(stats.getPlayed() + 1);
            }
        }
    }

    public void addWinsToWinner(BBArena arena) {
        if(arena.getWinner() != null) {
            for (Player p : arena.getWinner().getTeam().getPlayers()) {
                BBPlayerStats stats = getPlayerStats(p);
                if (stats != null) {
                    stats.setWins(stats.getWins() + 1);
                }
            }
        }
    }

    public void setAllPlayersMostPoints(BBArena a) {
        for(BBPlot plot : a.getVotingPlots()) {
            for(Player p : plot.getTeam().getPlayers()) {
                BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
                if (stats != null) {
                    int oldPoints = stats.getMostPoints();
                    if (plot.getVotePoints() > stats.getMostPoints()) {
                        stats.setMostPoints(plot.getVotePoints());
                        if(GameManager.isAnnounceNewMostPoints()) {
                            sendMostPointsAnnounce(p, oldPoints, stats.getMostPoints());
                        }
                    }
                }
            }
        }
    }

    public void giveAllPlayersLeaveItem(BBArena a) {
        for(Player p : a.getPlayers()) {
            p.getInventory().setItem(8, OptionsManager.getLeaveItem());
        }
    }

    public void removeAllPotionEffects(BBArena a) {
        for(Player p : a.getPlayers()) {
            for(PotionEffect potionEffect : p.getActivePotionEffects()) {
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

    public void giveAllPlayersTeamsItem(BBArena arenaInstance) {
        for(Player p : arenaInstance.getPlayers()) {
            p.getInventory().setItem(0, OptionsManager.getTeamsItem());
        }
    }

    public void closeInventoryAllPlayersInArena(BBArena arenaInstance) {
        for(Player p : arenaInstance.getPlayers()) {
            p.closeInventory();
        }
    }
}
