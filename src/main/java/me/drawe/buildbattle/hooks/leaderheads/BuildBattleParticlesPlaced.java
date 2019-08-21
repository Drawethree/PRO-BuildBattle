package me.drawe.buildbattle.hooks.leaderheads;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BuildBattleParticlesPlaced extends OnlineDataCollector {


    public BuildBattleParticlesPlaced() {
        super(
                "bb-particles",
                "BuildBattlePro",
                BoardType.DEFAULT,
                "&eBuildBattle - Particles Placed",
                "bbparticles",
                Arrays.asList(null,null,"&e{amount} particles",null)
        );
    }

    @Override
    public Double getScore(Player player) {
        return BuildBattle.getInstance().getPlayerManager().getPlayerStat(BBStat.PARTICLES_PLACED, player);
    }
}
