package me.drawethree.buildbattle.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.BBHook;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import me.drawethree.buildbattle.objects.bbobjects.BBStatIntegerComparator;
import me.drawethree.buildbattle.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class BBLeaderboard {

    private BuildBattle plugin;
    private LeaderboardType type;
    private Hologram hologram;
    private int amountToDisplay;

    public BBLeaderboard(BuildBattle plugin, Location loc, LeaderboardType type, int amountToDisplay) {
        this.plugin = plugin;
        this.type = type;
        this.amountToDisplay = amountToDisplay;
        this.hologram = HologramsAPI.createHologram(plugin, loc);
        this.hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', this.type.getTitle()));
    }

    public void update(List<BBPlayerStats> loadedStats) {

        if (!BBHook.getHook("HolographicDisplays") || this.hologram == null || this.hologram.isDeleted()) {
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                hologram.clearLines();
                hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', getType().getTitle()));

//                switch (type) {
//                    case WINS:
//                    case PLAYED:
//                    case BLOCKS_PLACED:
//                    case PARTICLES_PLACED:
//                        loadedStats.sort(new BBStatIntegerComparator(BBStat.valueOf(type.name())).reversed());
//                        break;
//                    default:
//                        return;
//                }
                loadedStats.sort(new BBStatIntegerComparator(BBStat.valueOf(type.name())).reversed());

                for (int i = 0, position = 0; ; i++) {

                    if (position == amountToDisplay) {
                        break;
                    }

                    BBPlayerStats stats;
                    try {
                        stats = loadedStats.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }

                    if (stats.getOfflinePlayer().getName() == null) {
                        plugin.warning("§cYour server cannot recognize player with uuid: " + stats.getUuid() + "! Perhaps he has not played on your server yet ?");
                        continue;
                    }

                    hologram.appendTextLine(getFormattedFormat(stats, ++position, getType()));
                }
            }
        }.runTask(plugin);
    }


    public void delete() {
        this.hologram.delete();
        plugin.getLeaderboardManager().getActiveLeaderboards().remove(this);
        this.removeFromConfig();
    }

    private void removeFromConfig() {
        plugin.getFileManager().getConfig("/leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(hologram.getLocation()), null);
        plugin.getFileManager().getConfig("/leaderboards.yml").save();
    }

    public void teleport(Location loc) {
        Location previousLoc = this.hologram.getLocation();
        this.hologram.teleport(loc);
        plugin.getFileManager().getConfig("/leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(previousLoc), null);
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".location", LocationUtil.getStringFromLocation(hologram.getLocation()));
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".type", type.name());
        plugin.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".player-amount", amountToDisplay);
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
                replace("%position%", position + ".").
                replace("%player%", stats.getOfflinePlayer().getName()).
                replace(type.getPlaceholder(), String.valueOf(stats.getStat(BBStat.valueOf(type.name()))));
        return returnString;
    }
}
