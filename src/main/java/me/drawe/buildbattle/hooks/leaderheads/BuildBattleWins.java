package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class BuildBattleWins extends OnlineDataCollector {

    public BuildBattleWins() {
        super (
        BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.wins.name"),
                "BuildBattlePro",
                BoardType.DEFAULT, BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.wins.title"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.wins.command"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getStringList("leaderheads.wins.sign")
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getWins());
    }
}
