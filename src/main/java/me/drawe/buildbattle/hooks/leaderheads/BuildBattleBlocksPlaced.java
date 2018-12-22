package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class BuildBattleBlocksPlaced extends OnlineDataCollector {

    public BuildBattleBlocksPlaced() {
        super(
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.blocks-placed.name"),
                "BuildBattlePro",
                BoardType.DEFAULT, BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.blocks-placed.title"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.blocks-placed.command"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getStringList("leaderheads.blocks-placed.sign")
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getBlocksPlaced());
    }
}
