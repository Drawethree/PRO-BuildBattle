package me.drawe.buildbattle.objects.bbrewards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBVaultRewards extends BBReward {

    public static int firstPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.first_place");
    public static int secondPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.second_place");
    public static int thirdPlace = BuildBattle.getFileManager().getConfig("config.yml").get().getInt("rewards.Vault.third_place");

    @Override
    public void giveReward(BBTeam team, int placement) {
        switch (placement) {
            case 1:
                for(Player p : team.getPlayers()) BuildBattle.getEconomy().depositPlayer(p, firstPlace);
                break;
            case 2:
                for(Player p : team.getPlayers()) BuildBattle.getEconomy().depositPlayer(p, secondPlace);
                break;
            case 3:
                for(Player p : team.getPlayers()) BuildBattle.getEconomy().depositPlayer(p, thirdPlace);
                break;
        }

    }
}
