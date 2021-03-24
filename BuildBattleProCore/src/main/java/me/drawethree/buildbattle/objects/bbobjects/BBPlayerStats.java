package me.drawethree.buildbattle.objects.bbobjects;

import me.drawethree.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class BBPlayerStats {

    private UUID uuid;
    private String username;
    private HashMap<BBStat, Object> stats;

    public BBPlayerStats(UUID uuid, String username, int wins, int played, int mostPoints, int blocksPlaced, int particlesPlaced, int superVotes) {
        this.uuid = uuid;
        this.username = username;
        this.stats = new HashMap<>();
        this.stats.put(BBStat.WINS, wins);
        this.stats.put(BBStat.PLAYED, played);
        this.stats.put(BBStat.MOST_POINTS, mostPoints);
        this.stats.put(BBStat.BLOCKS_PLACED, blocksPlaced);
        this.stats.put(BBStat.PARTICLES_PLACED, particlesPlaced);
        this.stats.put(BBStat.SUPER_VOTES, superVotes);
    }

    public BBPlayerStats(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username == null ? "" : username;
        this.stats = new HashMap<>();
    }

    public void setStat(BBStat stat, Object value, boolean save) {
        this.stats.put(stat, value);
        if (save && BuildBattle.getInstance().getSettings().isAsyncSavePlayerData()) {
            BuildBattle.getInstance().getPlayerManager().savePlayerStat(this, stat);
        }
    }

    public Object getStat(BBStat stat) {
        return this.stats.get(stat);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }

    @Override
    public String toString() {
        return "BBPlayerStats{" +
                "uuid=" + this.uuid +
                ", nick=" + this.getOfflinePlayer().getName() +
                '}';
    }
}
