package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.objects.bbobjects.rewards.BBCommandRewards;
import me.drawethree.buildbattle.objects.bbobjects.rewards.BBPointsAPIRewards;
import me.drawethree.buildbattle.objects.bbobjects.rewards.BBReward;
import me.drawethree.buildbattle.objects.bbobjects.rewards.BBVaultRewards;

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
        if (votingPlots == null || votingPlots.isEmpty()) {
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
