package me.drawe.buildbattle.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;

public class BBLeaderboard {

    private BuildBattle plugin;
    private LeaderboardType type;
    private Hologram hologram;
    private int amountToDisplay;
    private double refreshTime;
    private BukkitTask updateTask;

    public BBLeaderboard(BuildBattle plugin, Location loc, LeaderboardType type, int amountToDisplay, double refreshTime) {
        this.plugin = plugin;
        this.type = type;
        this.amountToDisplay = amountToDisplay;
        this.refreshTime = refreshTime;
        this.hologram = HologramsAPI.createHologram(plugin, loc);
        this.startUpdateTask();
    }

    private void startUpdateTask() {
        this.updateTask = new BukkitRunnable() {

            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(plugin, 0L, (long) (20 * 60 * getRefreshTime()));
    }

    public void update() {

        if (!BBHook.getHook("HolographicDisplays") || this.hologram == null || this.hologram.isDeleted()) {
            return;
        }

        this.hologram.clearLines();
        this.hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', getType().getTitle()));

        new BukkitRunnable() {

            @Override
            public void run() {
                ArrayList<BBPlayerStats> allData = new ArrayList<>();

                switch (plugin.getSettings().getStatsType()) {
                    case FLATFILE:
                        plugin.getPlayerManager().loadAllPlayerStats(allData);
                        break;
                    case MYSQL:
                        plugin.getMySQLManager().loadAllPlayerStats(allData);
                        break;
                }

                /*
                switch (getType()) {
                    case WINS:
                        Collections.sort(allData, Comparator.comparing(BBPlayerStats::getWins));
                        break;
                    case PLAYED:
                        Collections.sort(allData, Comparator.comparing(BBPlayerStats::getPlayed));
                        break;
                    case BLOCKS_PLACED:
                        Collections.sort(allData, Comparator.comparing(BBPlayerStats::getBlocksPlaced));
                        break;
                    case PARTICLES_PLACED:
                        Collections.sort(allData, Comparator.comparing(BBPlayerStats::getParticlesPlaced));
                        break;
                }
                */

                Collections.reverse(allData);

                for (int i = 0, position = 0; ; i++) {

                    if (position == amountToDisplay) {
                        break;
                    }

                    BBPlayerStats stats;
                    try {
                        stats = allData.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }

                    if (stats.getOfflinePlayer().getName() == null) {
                        plugin.warning("§cYour server cannot recognize player with uuid: " + stats.getUuid() + "! Perhaps he has not played on your server yet ?");
                        continue;
                    }

                    hologram.appendTextLine(getFormattedFormat(stats, ++position, getType()));
                }
                allData = null;
            }
        }.runTaskAsynchronously(plugin);
    }


    public void delete() {
        this.hologram.delete();
        this.updateTask.cancel();
        plugin.getLeaderboardManager().getActiveLeaderboards().remove(this);
        this.removeFromConfig();
    }

    private void removeFromConfig() {
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(hologram.getLocation()), null);
        plugin.getFileManager().getConfig("leaderboards.yml").save();
    }

    public void teleport(Location loc) {
        Location previousLoc = this.hologram.getLocation();
        this.hologram.teleport(loc);
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(previousLoc), null);
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".location", LocationUtil.getStringFromLocation(hologram.getLocation()));
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".type", type.name());
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".player-amount", amountToDisplay);
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".refresh-time", refreshTime);
        plugin.getFileManager().getConfig("leaderboards.yml").save();
    }

    public LeaderboardType getType() {
        return type;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public int getAmountToDisplay() {
        return amountToDisplay;
    }

    public String getFormattedFormat(BBPlayerStats stats, int position, LeaderboardType type) {
        String returnString = type.getLineFormat().
                replaceAll("%position%", position + ".").
                replaceAll("%player%", stats.getOfflinePlayer().getName())
                .replaceAll(type.getPlaceholder(), String.valueOf(stats.getStat(BBStat.valueOf(type.name()))));
        return returnString;
    }

    public double getRefreshTime() {
        return refreshTime;
    }
}
