package me.drawe.buildbattle.hooks.papi;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

public class BuildBattleProPlaceholders extends EZPlaceholderHook {

    private BuildBattle ourPlugin;

    public BuildBattleProPlaceholders(BuildBattle ourPlugin) {
        super(ourPlugin, "buildbattlepro");
        this.ourPlugin = ourPlugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
        if (p == null) {
            return "";
        }
        switch (identifier) {
            case "wins":
                if (stats != null) {
                    return String.valueOf(stats.getWins());
                } else {
                    return String.valueOf(0);
                }
            case "played":
                if (stats != null) {
                    return String.valueOf(stats.getPlayed());
                } else {
                    return String.valueOf(0);
                }
            case "most_points":
                if (stats != null) {
                    return String.valueOf(stats.getMostPoints());
                } else {
                    return String.valueOf(0);
                }
            case "blocks_placed":
                if (stats != null) {
                    return String.valueOf(stats.getBlocksPlaced());
                } else {
                    return String.valueOf(0);
                }
            case "particles_placed":
                if (stats != null) {
                    return String.valueOf(stats.getParticlesPlaced());
                } else {
                    return String.valueOf(0);
                }
            case "super_votes":
                if (stats != null) {
                    return String.valueOf(stats.getSuperVotes());
                } else {
                    return String.valueOf(0);
                }
            default:
                if (identifier.contains("status")) {
                    final BBArena arena = ArenaManager.getInstance().getArena(identifier.replaceAll("status_", ""));
                    if (arena != null) {
                        return arena.getBBArenaState().getPrefix();
                    }
                }
        }
        return null;
    }
}