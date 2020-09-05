package me.drawethree.buildbattle.managers;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.leaderboards.BBLeaderboard;
import me.drawethree.buildbattle.leaderboards.LeaderboardType;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import me.drawethree.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardManager {

    private BuildBattle plugin;
    @Getter
    private List<BBLeaderboard> activeLeaderboards;
    @Getter
    private HashMap<Player, BBLeaderboard> selectedLeaderboards;
    private BukkitTask refreshTask;


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
                this.activeLeaderboards.add(new BBLeaderboard(plugin,loc, type, playersToDisplay));
                this.plugin.info("§aLeaderboard at location §e" + LocationUtil.getStringFromLocationXYZ(loc) + " §aloaded!");
            }
        }
        this.startRefreshTask();
    }

    private void startRefreshTask() {
        this.refreshTask = new BukkitRunnable() {

            @Override
            public void run() {
                refreshAllLeaderBoards(null);
            }
        }.runTaskTimer(plugin, 0L, 20*60*BuildBattle.getInstance().getSettings().getLeaderBoardsRefreshTime());
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

    public void refreshAllLeaderBoards(CommandSender sender) {
        BuildBattle.getInstance().debug("Starting refreshing all Leaderboards");

        new BukkitRunnable() {

            @Override
            public void run() {
                for (BBLeaderboard l : activeLeaderboards) {
                    List<BBPlayerStats> loadedStats = null;

                    switch (plugin.getSettings().getStatsType()) {
                        case FLATFILE:
                            BuildBattle.getInstance().debug("Loading " + BBStat.map(l.getType()).getConfigKey() + " from stats.yml");
                            loadedStats = plugin.getPlayerManager().loadTopStatistics(BBStat.map(l.getType()), l.getAmountToDisplay());
                            break;
                        case MYSQL:
                            BuildBattle.getInstance().debug("Loading " + BBStat.map(l.getType()).getSQLKey() + " from SQL database");
                            loadedStats = plugin.getMySQLManager().loadTopStatistics(BBStat.map(l.getType()), l.getAmountToDisplay());
                            break;
                    }

                    l.update(loadedStats);
                }

                BuildBattle.getInstance().debug("Leaderboards Refreshed!");
                if(sender != null) {
                    sender.sendMessage(plugin.getSettings().getPrefix() + " §aLeaderboards refreshed !");
                }
            }
        }.runTaskAsynchronously(plugin);

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
        BBLeaderboard leaderboard = new BBLeaderboard(plugin,loc, type, 10);
        creator.sendMessage(this.plugin.getSettings().getPrefix() + " §aLeaderboard with type §e" + type.name() + " §acreated!");
        this.activeLeaderboards.add(leaderboard);
        this.saveLeaderboardIntoConfig(leaderboard);
    }

    private void saveLeaderboardIntoConfig(BBLeaderboard l) {
        final String configPath = LocationUtil.getStringFromLocationXYZ(l.getHologram().getLocation());
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".location", LocationUtil.getStringFromLocation(l.getHologram().getLocation()));
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".type", l.getType().name());
        this.plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + configPath + ".player-amount", l.getAmountToDisplay());
        this.plugin.getFileManager().getConfig("leaderboards.yml").save();
    }
}
