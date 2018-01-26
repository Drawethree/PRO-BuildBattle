package me.drawe.buildbattle.objects;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public enum Reward {

    POINTS_API_FIRST(RewardType.POINTS_API, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.first_place")),
    POINTS_API_SEC(RewardType.POINTS_API, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.second_place")),
    POINTS_API_THIRD(RewardType.POINTS_API, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.third_place")),
    VAULT_FIRST(RewardType.VAULT, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.first_place")),
    VAULT_SEC(RewardType.VAULT, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.second_place")),
    VAULT_THIRD(RewardType.VAULT, BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.third_place"));

    private int reward;
    private RewardType type;

    Reward(RewardType type, int reward) {
        this.reward = reward;
        this.type = type;
    }

    public int getReward() {
        return reward;
    }

    public void giveReward(BBTeam team) {
        switch (getType()) {
            case POINTS_API:
                for(Player p : team.getPlayers()) PointsAPI.addPoints(p, getReward());
                break;
            case VAULT:
                for(Player p : team.getPlayers()) BuildBattle.getEconomy().depositPlayer(p, getReward());
                break;
        }
    }

    public RewardType getType() {
        return type;
    }
}
