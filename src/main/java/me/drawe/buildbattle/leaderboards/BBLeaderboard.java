package me.drawe.buildbattle.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.LeaderboardManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BBLeaderboard {

    private LeaderboardType type;
    private Hologram hologram;
    private int amountToDisplay;
    private double refreshTime;
    private BukkitTask updateTask;

    public BBLeaderboard(Location loc, LeaderboardType type, int amountToDisplay, double refreshTime) {
        this.type = type;
        this.amountToDisplay = amountToDisplay;
        this.refreshTime = refreshTime;
        this.hologram = HologramsAPI.createHologram(BuildBattle.getInstance(), loc);
        this.startUpdateTask();
    }

    private void startUpdateTask() {
        this.updateTask = new BukkitRunnable() {

            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, (long) (20 * 60 * getRefreshTime()));
    }

    public void update() {

        if (!BuildBattle.getInstance().isUseHolographicDisplays() || this.hologram == null || this.hologram.isDeleted()) {
            return;
        }

        this.hologram.clearLines();
        this.hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', getType().getTitle()));
        ArrayList<BBPlayerStats> sortedStats = new ArrayList<>(PlayerManager.getPlayerStats().values());
        switch (getType()) {
            case WINS:
                Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats::getWins));
                break;
            case PLAYED:
                Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats::getPlayed));
                break;
            case BLOCKS_PLACED:
                Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats::getBlocksPlaced));
                break;
            case PARTICLES_PLACED:
                Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats::getParticlesPlaced));
                break;
        }
        Collections.reverse(sortedStats);

        for (int i = 0, position = 0; ; i++) {

            if (position == this.amountToDisplay) {
                break;
            }

            BBPlayerStats stats;
            try {
                stats = sortedStats.get(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            if (stats.getOfflinePlayer().getName() == null) {
                BuildBattle.warning("§cYour server cannot recognize player with uuid: " + stats.getUuid() + "! Perhaps he has not played on your server yet ?");
                continue;
            }

            this.hologram.appendTextLine(getFormattedFormat(stats, ++position, getType()));
        }
    }


    public void delete() {
        this.hologram.delete();
        this.updateTask.cancel();
        LeaderboardManager.getActiveLeaderboards().remove(this);
        this.removeFromConfig();
    }

    private void removeFromConfig() {
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(hologram.getLocation()), null);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
    }

    public void teleport(Location loc) {
        Location previousLoc = this.hologram.getLocation();
        this.hologram.teleport(loc);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(previousLoc), null);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".location", LocationUtil.getStringFromLocation(hologram.getLocation()));
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".type", type.name());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".player-amount", amountToDisplay);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".refresh-time", refreshTime);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
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
                replaceAll("%player%", stats.getOfflinePlayer().getName());
        switch (type) {
            case WINS:
                returnString = returnString.replaceAll(type.getPlaceholder(), String.valueOf(stats.getWins()));
                break;
            case PLAYED:
                returnString = returnString.replaceAll(type.getPlaceholder(), String.valueOf(stats.getPlayed()));
                break;
            case BLOCKS_PLACED:
                returnString = returnString.replaceAll(type.getPlaceholder(), String.valueOf(stats.getBlocksPlaced()));
                break;
            case PARTICLES_PLACED:
                returnString = returnString.replaceAll(type.getPlaceholder(), String.valueOf(stats.getParticlesPlaced()));
                break;
        }
        return returnString;
    }

    public double getRefreshTime() {
        return refreshTime;
    }
}
