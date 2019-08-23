package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class BBPlayerStats {

    private UUID uuid;
    private HashMap<BBStat, Object> stats;

    public BBPlayerStats(UUID uuid, int wins, int played, int mostPoints, int blocksPlaced, int particlesPlaced, int superVotes) {
        this.uuid = uuid;
        this.stats = new HashMap<>();
        this.stats.put(BBStat.WINS, wins);
        this.stats.put(BBStat.PLAYED, played);
        this.stats.put(BBStat.MOST_POINTS, mostPoints);
        this.stats.put(BBStat.BLOCKS_PLACED, blocksPlaced);
        this.stats.put(BBStat.PARTICLES_PLACED, particlesPlaced);
        this.stats.put(BBStat.SUPER_VOTES, superVotes);
    }

    public BBPlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.stats = new HashMap<>();
    }

    public void setStat(BBStat stat, Object value) {
        this.stats.put(stat, value);
        if (BuildBattle.getInstance().getSettings().isAsyncSavePlayerData()) {
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

    @Override
    public String toString() {
        return "BBPlayerStats{" +
                "uuid=" + this.uuid +
                ", nick=" + this.getOfflinePlayer().getName() +
                '}';
    }
}
