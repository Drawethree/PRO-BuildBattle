package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SuperVoteManager {

    private BuildBattle plugin;

    public SuperVoteManager(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public boolean giveSuperVote(OfflinePlayer player, int amount) {
        BBPlayerStats pStats = this.plugin.getPlayerManager().getPlayerStats(player);
        if (pStats != null) {
            pStats.setStat(BBStat.SUPER_VOTES, (int) pStats.getStat(BBStat.SUPER_VOTES) + amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean takeSuperVote(OfflinePlayer player, int amount) {
        BBPlayerStats pStats = this.plugin.getPlayerManager().getPlayerStats(player);
        if (pStats != null) {
            int currentAmount = (int) pStats.getStat(BBStat.SUPER_VOTES);
            if (currentAmount - amount < 0) {
                amount = currentAmount;
            }
            pStats.setStat(BBStat.SUPER_VOTES, (int) pStats.getStat(BBStat.SUPER_VOTES) - amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasSuperVote(Player p) {
        BBPlayerStats pStats = this.plugin.getPlayerManager().getPlayerStats(p);
        if (pStats != null) {
            return (int) pStats.getStat(BBStat.SUPER_VOTES) > 0;
        } else {
            return false;
        }
    }
}
