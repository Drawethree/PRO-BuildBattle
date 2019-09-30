package me.drawe.buildbattle.objects.bbobjects.rewards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.hooks.BBVaultHook;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

public class BBVaultRewards extends BBReward<Integer> {

    public BBVaultRewards(BuildBattle parent) {
        super(parent, "Vault", Integer.class);
    }

    @Override
    public void giveReward(BBTeam team, int placement) {
        super.giveReward(team, placement);


        if(!BBHook.getHook(this.rewardType)) {
            return;
        }

        if (team == null || this.rewardsForPlacements.get(placement) == null) {
            return;
        }

        for (Player p : team.getPlayers()) {
            ((BBVaultHook) BBHook.getHookInstance(this.rewardType)).getEcon().depositPlayer(p, this.rewardsForPlacements.get(placement));
        }
    }
}
