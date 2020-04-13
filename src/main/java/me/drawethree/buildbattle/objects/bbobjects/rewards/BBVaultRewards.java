package me.drawethree.buildbattle.objects.bbobjects.rewards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.BBHook;
import me.drawethree.buildbattle.hooks.BBVaultHook;
import me.drawethree.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBVaultRewards extends BBReward<Integer> {

    public BBVaultRewards(BuildBattle parent) {
        super(parent, "Vault", Integer.class);
    }

    @Override
    public void giveReward(BBTeam team, int placement) {
        super.giveReward(team, placement);


        if(!BBHook.getHook(this.rewardType) || team == null || this.rewardsForPlacements.get(placement) == null || team.getPlayers() == null) {
            return;
        }

        for (Player p : team.getPlayers()) {
            ((BBVaultHook) BBHook.getHookInstance(this.rewardType)).getEcon().depositPlayer(p, this.rewardsForPlacements.get(placement));
        }
    }
}
