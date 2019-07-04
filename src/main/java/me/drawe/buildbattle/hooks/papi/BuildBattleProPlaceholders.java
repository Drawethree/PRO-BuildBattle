package me.drawe.buildbattle.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.OfflinePlayer;

public class BuildBattleProPlaceholders extends PlaceholderExpansion {

    @Override
    public String onRequest(OfflinePlayer p, String params) {

        BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);

        if (p == null) {
            return "";
        }

        switch (params) {
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
                if (params.contains("status")) {
                    final BBArena arena = ArenaManager.getInstance().getArena(params.replaceAll("status_", ""));
                    if (arena != null) {
                        return arena.getBBArenaState().getPrefix();
                    }
                }
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return "buildbattlepro";
    }

    @Override
    public String getAuthor() {
        return "TheRealDrawe";
    }

    @Override
    public String getVersion() {
        return BuildBattle.getInstance().getServer().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}