package me.drawethree.buildbattle.objects.bbobjects.rewards;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.BBHook;
import me.drawethree.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBPointsAPIRewards extends BBReward<Integer> {


    public BBPointsAPIRewards(BuildBattle parent) {
        super(parent, "PointsAPI", Integer.class);
    }

    @Override
    public void giveReward(BBTeam team, int placement) {
        super.giveReward(team, placement);

        if(!BBHook.getHook(this.rewardType) || team == null || this.rewardsForPlacements.get(placement) == null || team.getPlayers() == null) {
            return;
        }

        for(Player p : team.getPlayers())
            PointsAPI.addPoints(p, this.rewardsForPlacements.get(placement));
    }
}
