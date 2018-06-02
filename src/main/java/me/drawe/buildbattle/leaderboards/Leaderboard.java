package me.drawe.buildbattle.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.LeaderboardManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard {

    private Location location;
    private LeaderboardType type;
    private Hologram hologram;
    private int amountToDisplay;
    private double refreshTime;
    private BukkitTask updateTask;

    public Leaderboard(Location loc, LeaderboardType type, int amountToDisplay, double refreshTime) {
        this.location = loc;
        this.type = type;
        this.amountToDisplay = amountToDisplay;
        this.refreshTime = refreshTime;
        this.hologram = HologramsAPI.createHologram(BuildBattle.getInstance(), location);
        update();
    }

    public void update() {
        if(BuildBattle.getInstance().isUseHolographicDisplays()) {
            updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    hologram.clearLines();
                    hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', getType().getTitle()));
                    ArrayList<BBPlayerStats> sortedStats = new ArrayList<>(PlayerManager.getPlayerStats());
                    switch(getType()) {
                        case WINS:
                            Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats :: getWins));
                            Collections.reverse(sortedStats);
                            for(int i = 0, position=1;i < getAmountToDisplay();i++,position++) {
                                try {
                                    BBPlayerStats stats = sortedStats.get(i);
                                    hologram.insertTextLine(position, getFormattedFormat(stats,position,getType()));
                                } catch (NullPointerException e1) {
                                    BuildBattle.warning("§cLooks like there are empty stats for leaderboard §e" + LocationUtil.getStringFromLocationXYZ(location) + "§c ! Please change player-amount value");
                                    break;
                                } catch (IndexOutOfBoundsException e2) {
                                    break;
                                }
                            }
                            break;
                        case PLAYED:
                            Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats :: getPlayed));
                            Collections.reverse(sortedStats);
                            for(int i = 0, position=1;i < getAmountToDisplay();i++,position++) {
                                try {
                                    BBPlayerStats stats = sortedStats.get(i);
                                    hologram.insertTextLine(position, getFormattedFormat(stats,position,getType()));
                                } catch (NullPointerException e1) {
                                    BuildBattle.warning("§cLooks like there are empty stats for leaderboard §e" + LocationUtil.getStringFromLocationXYZ(location) + "§c ! Please change player-amount value");
                                    break;
                                } catch (IndexOutOfBoundsException e2) {
                                    break;
                                }
                            }
                            break;
                        case BLOCKS_PLACED:
                            Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats :: getBlocksPlaced));
                            Collections.reverse(sortedStats);
                            for(int i = 0, position=1;i < getAmountToDisplay();i++,position++) {
                                try {
                                    BBPlayerStats stats = sortedStats.get(i);
                                    hologram.insertTextLine(position, getFormattedFormat(stats,position,getType()));
                                } catch (NullPointerException e1) {
                                    BuildBattle.warning("§cLooks like there are empty stats for leaderboard §e" + LocationUtil.getStringFromLocationXYZ(location) + "§c ! Please change player-amount value");
                                    break;
                                } catch (IndexOutOfBoundsException e2) {
                                    break;
                                }
                            }
                            break;
                        case PARTICLES_PLACED:
                            Collections.sort(sortedStats, Comparator.comparing(BBPlayerStats :: getParticlesPlaced));
                            Collections.reverse(sortedStats);
                            for(int i = 0, position=1;i < getAmountToDisplay();i++,position++) {
                                try {
                                    BBPlayerStats stats = sortedStats.get(i);
                                    hologram.insertTextLine(position, getFormattedFormat(stats,position,getType()));
                                } catch (NullPointerException e1) {
                                    BuildBattle.warning("§cLooks like there are empty stats for leaderboard §e" + LocationUtil.getStringFromLocationXYZ(location) + "§c ! Please change player-amount value");
                                    break;
                                } catch (IndexOutOfBoundsException e2) {
                                    break;
                                }
                            }
                            break;
                    }
                }
            }.runTaskTimer(BuildBattle.getInstance(), 0L, (long) (20*60*getRefreshTime()));
        }
    }


    public void delete() {
        getHologram().delete();
        getUpdateTask().cancel();
        LeaderboardManager.getActiveLeaderboards().remove(this);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(getLocation()), null);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
    }
    public void teleport(Location loc) {
        getHologram().teleport(loc);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(getLocation()), null);
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".type", getType().name());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".player-amount", getAmountToDisplay());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").get().set("leaderboards." + LocationUtil.getStringFromLocationXYZ(loc) + ".refresh-time", getRefreshTime());
        BuildBattle.getFileManager().getConfig("leaderboards.yml").save();
        setLocation(loc);
    }

    public Location getLocation() {
        return location;
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
                replaceAll("%position%", String.valueOf(position + ".")).
                replaceAll("%player%", stats.getOfflinePlayer().getName());
        switch(type) {
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
    public void setLocation(Location location) {
        this.location = location;
    }
    public BukkitTask getUpdateTask() {
        return updateTask;
    }
}
