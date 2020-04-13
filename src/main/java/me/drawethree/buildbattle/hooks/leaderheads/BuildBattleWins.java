package me.drawethree.buildbattle.hooks.leaderheads;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
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
        return BuildBattle.getInstance().getPlayerManager().getPlayerStat(BBStat.WINS, player);
    }
}
