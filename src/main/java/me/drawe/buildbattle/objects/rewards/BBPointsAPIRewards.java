package me.drawe.buildbattle.objects.rewards;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBPointsAPIRewards extends BBReward<Integer> {


    public BBPointsAPIRewards(BuildBattle parent) {
        super(parent, "PointsAPI", Integer.class);
    }

    @Override
    public void giveReward(BBTeam team, int placement) {
        super.giveReward(team, placement);

        if(!BBHook.getHook(this.rewardType)) {
            return;
        }

        for(Player p : team.getPlayers())
            PointsAPI.addPoints(p, this.rewardsForPlacements.get(placement));
    }
}
