package me.drawe.buildbattle.objects.bbrewards;

import me.BukkitPVP.PointsAPI.PointsAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBPointsAPIRewards implements BBReward {

    public static int firstPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.first_place");
    public static int secondPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.second_place");
    public static int thirdPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.PointsAPI.third_place");

    @Override
    public void giveReward(BBTeam team, int placement) {
        switch (placement) {
            case 1:
                for(Player p : team.getPlayers()) PointsAPI.addPoints(p, firstPlace);
                break;
            case 2:
                for(Player p : team.getPlayers()) PointsAPI.addPoints(p, secondPlace);
                break;
            case 3:
                for(Player p : team.getPlayers()) PointsAPI.addPoints(p, thirdPlace);
                break;
        }
    }
}
