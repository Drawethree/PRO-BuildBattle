package me.drawethree.buildbattle.hooks.leaderheads;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
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
        return BuildBattle.getInstance().getPlayerManager().getPlayerStat(BBStat.MOST_POINTS, player);
    }
}
