package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbrewards.BBCommandRewards;
import me.drawe.buildbattle.objects.bbrewards.BBPointsAPIRewards;
import me.drawe.buildbattle.objects.bbrewards.BBVaultRewards;

public class RewardManager {
    private static RewardManager ourInstance = new RewardManager();
    private static BBVaultRewards vaultRewards = new BBVaultRewards();
    private static BBCommandRewards commandRewards = new BBCommandRewards();
    private static BBPointsAPIRewards pointsAPIRewards = new BBPointsAPIRewards();

    public static RewardManager getInstance() {
        return ourInstance;
    }

    private RewardManager() {
    }

    public void giveRewards(BBArena a) {
        if(BBSettings.isPointsApiRewards()) {
            try {
                pointsAPIRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                pointsAPIRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                pointsAPIRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        if(BBSettings.isVaultRewards()) {
            try {
                vaultRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                vaultRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                vaultRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        if(BBSettings.isCommandRewards()) {
            try {
                commandRewards.giveReward(a.getVotingPlots().get(0).getTeam(), 1);
                commandRewards.giveReward(a.getVotingPlots().get(1).getTeam(), 2);
                commandRewards.giveReward(a.getVotingPlots().get(2).getTeam(), 3);
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }
}
