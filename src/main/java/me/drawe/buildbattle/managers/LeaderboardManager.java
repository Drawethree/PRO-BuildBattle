package me.drawe.buildbattle.managers;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.leaderboards.BBLeaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
                ArrayList<BBPlayerStats> allData = new ArrayList<>();
                CountDownLatch latch = new CountDownLatch(1);

                synchronized (this) {
                    switch (plugin.getSettings().getStatsType()) {
                        case FLATFILE:
                            BuildBattle.getInstance().debug("Loading data from stats.yml");
                            plugin.getPlayerManager().loadAllPlayerStats(allData, latch);
                            break;
                        case MYSQL:
                            BuildBattle.getInstance().debug("Loading data from SQL");
                            plugin.getMySQLManager().loadAllPlayerStats(allData, latch);
                            break;
                    }
                }

                try {
                    BuildBattle.getInstance().debug("Waiting for load to complete...");
                    latch.await();
                } catch (InterruptedException e) {
                    BuildBattle.getInstance().debug("§cLoading data was interrupted!");
                    e.printStackTrace();
                }

                BuildBattle.getInstance().debug("Done!");
                BuildBattle.getInstance().debug("Loaded " + allData.size() + " players!");

                for (BBLeaderboard l : activeLeaderboards) {
                    l.update(allData);
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
