package me.drawe.buildbattle.api;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BuildBattleProAPIImpl implements BuildBattleProAPI {

    private final BuildBattle plugin;

    public BuildBattleProAPIImpl(BuildBattle plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<BBArena> getArenas() {
        return plugin.getArenaManager().getArenas().values();
    }

    @Override
    public BBArena getPlayerArena(Player p) {
        return plugin.getPlayerManager().getPlayerArena(p);
    }

    @Override
    public BBPlayerStats getPlayerBuildBattleStats(Player p) {
        return plugin.getPlayerManager().getPlayerStats(p);
    }

    @Override
    public BBTeam getPlayerTeam(Player p) {
        return plugin.getPlayerManager().getPlayerTeam(plugin.getPlayerManager().getPlayerArena(p), p);
    }
}
