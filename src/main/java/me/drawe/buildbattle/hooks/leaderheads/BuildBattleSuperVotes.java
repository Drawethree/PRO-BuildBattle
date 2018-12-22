package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.PlayerManager;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

public class BuildBattleSuperVotes extends OnlineDataCollector {


    public BuildBattleSuperVotes() {
        super(
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.super-votes.name"),
                "BuildBattlePro",
                BoardType.DEFAULT, BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.super-votes.title"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getString("leaderheads.super-votes.command"),
                BuildBattle.getFileManager().getConfig("leaderheads.yml").get().getStringList("leaderheads.super-votes.sign")
        );
    }

    @Override
    public Double getScore(Player player) {
        return Double.valueOf(PlayerManager.getInstance().getPlayerStats(player).getSuperVotes());
    }
}
