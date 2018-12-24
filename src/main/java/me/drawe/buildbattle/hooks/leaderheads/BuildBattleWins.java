package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattleWins extends OnlineDataCollector {

    public BuildBattleWins() {
        super(
                "bb-wins",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Wins",
                "bbwins",
                Arrays.asList(null, null, "&e{amount} wins", null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getWins());
    }
}
