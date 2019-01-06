package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattleMostPoints extends OnlineDataCollector {

    public BuildBattleMostPoints() {
        super(
                "bb-mostpoints",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Most Points Gained",
                "bbmostpoints",
                Arrays.asList(null,null,"&e{amount} points",null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return PlayerManager.getInstance().getPlayerStats(player) == null ? 0 :Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getMostPoints());
    }
}
