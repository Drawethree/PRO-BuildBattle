package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattleSuperVotes extends OnlineDataCollector {


    public BuildBattleSuperVotes() {
        super(
                "bb-supervotes",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Super Votes",
                "bbsupervotes",
                Arrays.asList(null, null, "&e{amount} votes", null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return BuildBattle.getInstance().getPlayerManager().getPlayerStat(BBStat.SUPER_VOTES, player);
    }
}
