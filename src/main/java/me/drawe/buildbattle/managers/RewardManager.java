package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbrewards.BBCommandRewards;
import me.drawe.buildbattle.objects.bbrewards.BBPointsAPIRewards;
import me.drawe.buildbattle.objects.bbrewards.BBVaultRewards;

public class RewardManager {

    private BuildBattle plugin;

    private BBVaultRewards vaultRewards;
    private BBCommandRewards commandRewards;
    private BBPointsAPIRewards pointsAPIRewards;

    public RewardManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.vaultRewards = new BBVaultRewards();
        this.commandRewards = new BBCommandRewards();
        this.pointsAPIRewards = new BBPointsAPIRewards();
    }


    public void giveRewards(BBArena a) {
        if(this.plugin.getSettings().isPointsApiRewards()) {
            try {
                pointsAPIRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                pointsAPIRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                pointsAPIRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        if(this.plugin.getSettings().isVaultRewards()) {
            try {
                vaultRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                vaultRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                vaultRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        if(this.plugin.getSettings().isCommandRewards()) {
            try {
                commandRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                commandRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                commandRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }
}
