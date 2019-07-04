package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.leaderboards.BBLeaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardManager {

    private static LeaderboardManager ourInstance = new LeaderboardManager();
    private static List<BBLeaderboard> activeLeaderboards;
    private static HashMap<Player, BBLeaderboard> selectedLeaderboards = new HashMap<>();

    private LeaderboardManager() {
    }


    public static LeaderboardManager getInstance() {
        return ourInstance;
    }

    public static List<BBLeaderboard> getActiveLeaderboards() {
        return activeLeaderboards;
    }

    public static HashMap<Player, BBLeaderboard> getSelectedLeaderboards() {
        return selectedLeaderboards;
    }

    public void loadAllLeaderboards() {
        selectedLeaderboards = new HashMap<>();
        activeLeaderboards = new ArrayList<>();
        if (BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards") != null) {
            for (String location : BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards").getKeys(false)) {

                final Location loc = LocationUtil.getLocationFromConfig("leaderboards.yml", "leaderboards." + location + ".location") == null ? LocationUtil.getLocationFromString(location) : LocationUtil.getLocationFromConfig("leaderboards.yml", "leaderboards." + location + ".location");
                final LeaderboardType type = LeaderboardType.valueOf(BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getString("leaderboards." + location + ".type"));
                final int playersToDisplay = BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getInt("leaderboards." + location + ".player-amount");
                double refreshTime = BuildBattle.getFileManager().getConfig("leaderboards.yml").get().getDouble("leaderboards." + location + ".refresh-time");
                activeLeaderboards.add(new BBLeaderboard(loc, type, playersToDisplay, refreshTime));
                BuildBattle.info("§aLeaderboard at location §e" + LocationUtil.getStringFromLocationXYZ(loc) + " §aloaded!");
            }
        }
    }

    public void selectLeaderboard(Player p) {
        BBLeaderboard selected = getClosestLeaderboard(p);
        if (selected != null) {
            p.sendMessage(BBSettings.getPrefix() + " §aYou have selected BBLeaderboard with type §e" + selected.getType().name() + "§a at location §e" + LocationUtil.getStringFromLocationXYZ(selected.getHologram().getLocation()));
            selectedLeaderboards.put(p, selected);
        } else {
            p.sendMessage(BBSettings.getPrefix() + " §cThere is not leaderboard close to you !");
        }
    }

    public void refreshAllLeaderBoards() {
        for (BBLeaderboard l : activeLeaderboards) {
            l.update();
        }
    }

    private BBLeaderboard getClosestLeaderboard(Player p) {
        BBLeaderboard selected = null;
        for (BBLeaderboard l : activeLeaderboards) {
            if (p.getWorld().equals(l.getHologram().getLocation().getWorld())) {
                if (l.getHologram().getLocation().distance(p.getLocation()) <= 5) {
                    if (selected == null) {
                        selected = l;
                    } else {
                        if (selected.getHologram().getLocation().distance(p.getLocation()) > l.getHologram().getLocation().distance(p.getLocation())) {
                            selected = l;
                        }
                    }
                }
            }
        }
        return selected;
    }

    public void createLeaderboard(Player creator, Location loc, LeaderboardType type) {
        BBLeaderboard leaderboard = new BBLeaderboard(loc, type, 10, 30);
        creator.sendMessage(BBSettings.getPrefix() + " §aLeaderboard with type §e" + type.name() + " §acreated!");
        activeLeaderboards.add(leaderboard);
        saveLeaderboardIntoConfig(leaderboard);
    }

    private void saveLeaderboardIntoConfig(BBLeaderboard l) {
        final String configPath = LocationUtil.getStringFromLocationXYZ(l.getHologram().getLocation());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".location", LocationUtil.getStringFromLocation(l.getHologram().getLocation()));
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".type", l.getType().name());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".player-amount", l.getAmountToDisplay());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".refresh-time", l.getRefreshTime());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
    }
}
