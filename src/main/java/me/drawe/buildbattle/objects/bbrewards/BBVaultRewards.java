package me.drawe.buildbattle.objects.bbrewards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBVaultRewards implements BBReward {

    private static int firstPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.first_place");
    private static int secondPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.second_place");
    private static int thirdPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.third_place");

    @Override
    public void giveReward(BBTeam team, int placement) {
        int rewardGiven = 0;
        switch (placement) {
            case 1:
                rewardGiven = firstPlace;
                break;
            case 2:
                rewardGiven = secondPlace;
                break;
            case 3:
                rewardGiven = thirdPlace;
                break;
        }
        for(Player p : team.getPlayers()) BuildBattle.getInstance().getEcon().depositPlayer(p, rewardGiven);
    }
}
