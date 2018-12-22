package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class BuildBattleMostPoints extends OnlineDataCollector {

    public BuildBattleMostPoints() {
        super(
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.most-points.name"),
                "BuildBattlePro",
                BoardType.DEFAULT, BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.most-points.title"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.most-points.command"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getStringList("leaderheads.most-points.sign")
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getMostPoints());
    }
}
