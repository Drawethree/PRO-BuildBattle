package me.drawe.buildbattle.objects.bbrewards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BBCommandRewards extends BBReward {

    public static String firstPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getString("rewards.Command.first_place");
    public static String secondPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getString("rewards.Command.second_place");
    public static String thirdPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getString("rewards.Command.third_place");

    @Override
    public void giveReward(BBTeam team, int placement) {
        switch (placement) {
            case 1:
                for(Player p : team.getPlayers()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), firstPlace.replaceAll("%player%", p.getName()));
                break;
            case 2:
                for(Player p : team.getPlayers()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), secondPlace.replaceAll("%player%", p.getName()));
                break;
            case 3:
                for(Player p : team.getPlayers()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), thirdPlace.replaceAll("%player%", p.getName()));
                break;
        }
    }
}
