package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.rewards.BBCommandRewards;
import me.drawe.buildbattle.objects.bbobjects.rewards.BBPointsAPIRewards;
import me.drawe.buildbattle.objects.bbobjects.rewards.BBReward;
import me.drawe.buildbattle.objects.bbobjects.rewards.BBVaultRewards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RewardManager {

    private Set<BBReward> rewards;

    public RewardManager(BuildBattle plugin) {
        this.reload(plugin);
    }


    public void giveRewards(List<BBPlot> votingPlots) {

        //Check if there are no plots to give reward for.
        if (votingPlots == null) {
            return;
        }

        for (BBReward reward : this.rewards) {

            if (!reward.isEnabled()) {
                continue;
            }

            for (BBPlot plot : votingPlots) {
                reward.giveReward(plot.getTeam(), votingPlots.indexOf(plot) + 1);
            }
        }
    }

    public void reload(BuildBattle parent) {
        this.rewards = new HashSet<>();
        this.rewards.add(new BBVaultRewards(parent));
        this.rewards.add(new BBCommandRewards(parent));
        this.rewards.add(new BBPointsAPIRewards(parent));
    }
}
