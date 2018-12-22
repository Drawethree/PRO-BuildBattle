package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class BuildBattlePlayed extends OnlineDataCollector {

    public BuildBattlePlayed() {
        super(
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.played.name"),
                "BuildBattlePro",
                BoardType.DEFAULT, BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.played.title"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.played.command"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getStringList("leaderheads.played.sign")
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getPlayed());
    }
}
