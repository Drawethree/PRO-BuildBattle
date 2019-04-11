package me.drawe.buildbattle.objects.bbrewards;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBPointsAPIRewards implements BBReward {

    private static int firstPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.first_place");
    private static int secondPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.second_place");
    private static int thirdPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.third_place");

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
        for(Player p : team.getPlayers()) PointsAPI.addPoints(p, rewardGiven);
    }
}
