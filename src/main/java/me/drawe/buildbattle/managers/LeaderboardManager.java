package me.drawe.buildbattle.managers;

import lombok.Getter;
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

    private BuildBattle plugin;
    @Getter
    private List<BBLeaderboard> activeLeaderboards;
    @Getter
    private HashMap<Player, BBLeaderboard> selectedLeaderboards;


    public LeaderboardManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.selectedLeaderboards = new HashMap<>();
        this.activeLeaderboards = new ArrayList<>();
    }


    public void loadAllLeaderboards() {
        this.selectedLeaderboards = new HashMap<>();
        this.activeLeaderboards = new ArrayList<>();
        if (this.plugin.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards") != null) {
            for (String location : this.plugin.getFileManager().getConfig("leaderboards.yml").get().getConfigurationSection("leaderboards").getKeys(false)) {

                final Location loc = LocationUtil.getLocationFromConfig("leaderboards.yml", "leaderboards." + location + ".location") == null ? LocationUtil.getLocationFromString(location) : LocationUtil.getLocationFromConfig("leaderboards.yml", "leaderboards." + location + ".location");
                final LeaderboardType type = LeaderboardType.valueOf(this.plugin.getFileManager().getConfig("leaderboards.yml").get().getString("leaderboards." + location + ".type"));
                final int playersToDisplay = this.plugin.getFileManager().getConfig("leaderboards.yml").get().getInt("leaderboards." + location + ".player-amount");
                double refreshTime = this.plugin.getFileManager().getConfig("leaderboards.yml").get().getDouble("leaderboards." + location + ".refresh-time");
                this.activeLeaderboards.add(new BBLeaderboard(plugin,loc, type, playersToDisplay, refreshTime));
                this.plugin.info("§aLeaderboard at location §e" + LocationUtil.getStringFromLocationXYZ(loc) + " §aloaded!");
            }
        }
    }

    public void selectLeaderboard(Player p) {
        BBLeaderboard selected = getClosestLeaderboard(p);
        if (selected != null) {
            p.sendMessage(this.plugin.getSettings().getPrefix() + " §aYou have selected BBLeaderboard with type §e" + selected.getType().name() + "§a at location §e" + LocationUtil.getStringFromLocationXYZ(selected.getHologram().getLocation()));
            this.selectedLeaderboards.put(p, selected);
        } else {
            p.sendMessage(this.plugin.getSettings().getPrefix() + " §cThere is not leaderboard close to you !");
        }
    }

    public void refreshAllLeaderBoards() {
        for (BBLeaderboard l : this.activeLeaderboards) {
            l.update();
        }
    }

    private BBLeaderboard getClosestLeaderboard(Player p) {
        BBLeaderboard selected = null;
        for (BBLeaderboard l : this.activeLeaderboards) {
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
        BBLeaderboard leaderboard = new BBLeaderboard(plugin,loc, type, 10, 30);
        creator.sendMessage(this.plugin.getSettings().getPrefix() + " §aLeaderboard with type §e" + type.name() + " §acreated!");
        this.activeLeaderboards.add(leaderboard);
        this.saveLeaderboardIntoConfig(leaderboard);
    }

    private void saveLeaderboardIntoConfig(BBLeaderboard l) {
        final String configPath = LocationUtil.getStringFromLocationXYZ(l.getHologram().getLocation());
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".location", LocationUtil.getStringFromLocation(l.getHologram().getLocation()));
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".type", l.getType().name());
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".player-amount", l.getAmountToDisplay());
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".refresh-time", l.getRefreshTime());
        this.plugin.getFileManager().getConfig("leaderboards.yml").save();
    }
}
