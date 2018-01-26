package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.Reward;

public class RewardManager {
    private static RewardManager ourInstance = new RewardManager();

    public static RewardManager getInstance() {
        return ourInstance;
    }

    private RewardManager() {
    }

    public void giveRewards(BBArena a) {
        if(GameManager.isPointsApiRewards()) {
            try {
                Reward.POINTS_API_FIRST.giveReward(a.getVotingPlots().get(0).getTeam());
                Reward.POINTS_API_SEC.giveReward(a.getVotingPlots().get(1).getTeam());
                Reward.POINTS_API_THIRD.giveReward(a.getVotingPlots().get(2).getTeam());
            } catch (IndexOutOfBoundsException e) {
            }
        }
        if(GameManager.isVaultRewards()) {
            try {
                Reward.VAULT_FIRST.giveReward(a.getVotingPlots().get(0).getTeam());
                Reward.VAULT_SEC.giveReward(a.getVotingPlots().get(1).getTeam());
                Reward.VAULT_THIRD.giveReward(a.getVotingPlots().get(2).getTeam());
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }
}
