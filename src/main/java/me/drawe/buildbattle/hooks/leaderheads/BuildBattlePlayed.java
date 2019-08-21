package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattlePlayed extends OnlineDataCollector {

    public BuildBattlePlayed() {
        super(
                "bb-played",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Most Played",
                "bbplayed",
                Arrays.asList(null,null,"&e{amount} times",null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return BuildBattle.getInstance().getPlayerManager().getPlayerStat(BBStat.PLAYED, player);
    }
}
