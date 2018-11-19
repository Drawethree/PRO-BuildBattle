package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.leaderboards.Leaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardManager {
    private static LeaderboardManager ourInstance = new LeaderboardManager();
    private static List<Leaderboard> activeLeaderboards;
    private static HashMap<Player, Leaderboard> selectedLeaderboards = new HashMap<>();

    private LeaderboardManager() {
    }


    public static LeaderboardManager getInstance() {
        return ourInstance;
    }
    public static List<Leaderboard> getActiveLeaderboards() {
        return activeLeaderboards;
    }
    public static HashMap<Player, Leaderboard> getSelectedLeaderboards() {
        return selectedLeaderboards;
    }

    public void loadAllLeaderboards() {
        activeLeaderboards = new ArrayList<>();
        if (BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards") != null) {
            for (String location : BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards").getKeys(false)) {
                Location loc = LocationUtil.getLocationFromString(location);
                LeaderboardType type = LeaderboardType.valueOf(BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getString("leaderboards." + location + ".type"));
                int playersToDisplay = BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getInt("leaderboards." + location + ".player-amount");
                double refreshTime = BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getDouble("leaderboards." + location + ".refresh-time");
                if (refreshTime < 10) {
                    BuildBattle.warning("§cRefresh-Time cannot be lower than 10 minutes ! Setting it to 10 minutes");
                    refreshTime = 10;
                }
                Leaderboard leaderboard = new Leaderboard(loc, type, playersToDisplay, refreshTime);
                BuildBattle.info("§aLeaderboard at location §e" + LocationUtil.getStringFromLocationXYZ(leaderboard.getLocation()) + " §aloaded!");
                activeLeaderboards.add(leaderboard);
            }
        }
    }

    public void selectLeaderboard(Player p) {
        Leaderboard selected = getClosestLeaderboard(p);
        if(selected != null) {
            p.sendMessage(GameManager.getPrefix() + " §aYou have selected Leaderboard with type §e" + selected.getType().name() + "§a at location §e" + LocationUtil.getStringFromLocationXYZ(selected.getLocation()));
            selectedLeaderboards.put(p, selected);
        } else {
            p.sendMessage(GameManager.getPrefix() + " §cThere is not leaderboard close to you !");
        }
    }

    public void refreshAllLeaderBoards() {
        for(Leaderboard l : getActiveLeaderboards()) {
            l.update();
        }
    }

    public Leaderboard getClosestLeaderboard(Player p) {
        Leaderboard selected = null;
        for (Leaderboard l : getActiveLeaderboards()) {
            if (p.getWorld().equals(l.getLocation().getWorld())) {
                if (l.getLocation().distance(p.getLocation()) <= 5) {
                    if (selected == null) {
                        selected = l;
                    } else {
                        if (selected.getLocation().distance(p.getLocation()) > l.getLocation().distance(p.getLocation())) {
                            selected = l;
                        }
                    }
                }
            }
        }
        return selected;
    }

    public void createLeaderboard(Player creator, Location loc, LeaderboardType type, int playerAmount, double refreshTime) {
        Leaderboard leaderboard = new Leaderboard(loc,type,playerAmount,refreshTime);
        creator.sendMessage(GameManager.getPrefix() + " §aLeaderboard with type §e" + type.name() + " §acreated at location §e" + LocationUtil.getStringFromLocationXYZ(leaderboard.getLocation()) + "§a!");
        activeLeaderboards.add(leaderboard);
        saveLeaderboardIntoConfig(leaderboard);
    }

    public void createLeaderboard(Player creator,Location loc, LeaderboardType type) {
        Leaderboard leaderboard = new Leaderboard(loc,type,10,30);
        creator.sendMessage(GameManager.getPrefix() + " §aLeaderboard with type §e" + type.name() + " §acreated at location §e" + LocationUtil.getStringFromLocationXYZ(leaderboard.getLocation()) + "§a!");
        activeLeaderboards.add(leaderboard);
        saveLeaderboardIntoConfig(leaderboard);
    }

    private void saveLeaderboardIntoConfig(Leaderboard l) {
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(l.getLocation()) + ".type", l.getType().name());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(l.getLocation()) + ".player-amount", l.getAmountToDisplay());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(l.getLocation()) + ".refresh-time", l.getRefreshTime());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
    }
}
