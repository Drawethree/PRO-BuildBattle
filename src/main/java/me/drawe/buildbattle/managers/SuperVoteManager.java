package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SuperVoteManager {
    private static SuperVoteManager ourInstance = new SuperVoteManager();

    public static SuperVoteManager getInstance() {
        return ourInstance;
    }

    private SuperVoteManager() {

    }



    public boolean giveSuperVote(OfflinePlayer player, int amount) {
        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(player);
        if(pStats != null) {
            pStats.setSuperVotes(pStats.getSuperVotes() + amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean takeSuperVote(OfflinePlayer player, int amount) {
        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(player);
        if(pStats != null) {
            int currentAmount = pStats.getSuperVotes();
            if(currentAmount - amount < 0) {
                amount = currentAmount;
            }
            pStats.setSuperVotes(pStats.getSuperVotes() - amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasSuperVote(Player p) {
        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(p);
        if(pStats != null) {
            return pStats.getSuperVotes() > 0;
        } else {
            return false;
        }
    }
}
