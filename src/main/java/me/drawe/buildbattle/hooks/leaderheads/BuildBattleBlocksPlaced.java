package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattleBlocksPlaced extends OnlineDataCollector {

    public BuildBattleBlocksPlaced() {
        super(
                "bb-blocks",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Blocks Placed",
                "bbblocks",
                Arrays.asList(null, null, "&e{amount} blocks", null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return PlayerManager.getInstance().getPlayerStats(player) == null ? 0 : Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getBlocksPlaced());
    }
}
