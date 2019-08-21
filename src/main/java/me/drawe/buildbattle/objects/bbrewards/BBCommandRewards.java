package me.drawe.buildbattle.objects.bbrewards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BBCommandRewards implements BBReward {

    private static List<String> firstPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getStringList("rewards.Command.first_place");
    private static List<String> secondPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getStringList("rewards.Command.second_place");
    private static List<String> thirdPlace = BuildBattle.getInstance().getFileManager().getConfig("config.yml").get().getStringList("rewards.Command.third_place");

    @Override
    public void giveReward(BBTeam team, int placement) {
        List<String> rewardsGiven = null;
        switch (placement) {
            case 1:
                rewardsGiven = firstPlace;
                break;
            case 2:
                rewardsGiven = secondPlace;
                break;
            case 3:
                rewardsGiven = thirdPlace;
                break;
        }
        if(rewardsGiven != null) {
            for (Player p : team.getPlayers())
                for (String cmd : rewardsGiven)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", p.getName()));
        }
    }
}
