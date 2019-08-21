package me.drawe.buildbattle.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.OfflinePlayer;

public class BuildBattleProPlaceholders extends PlaceholderExpansion {

    private BuildBattle plugin;

    public BuildBattleProPlaceholders(BuildBattle plugin) {

        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {

        BBPlayerStats stats = plugin.getPlayerManager().getPlayerStats(p);

        if (p == null) {
            return "";
        }

        if (params.contains("status")) {
            final BBArena arena = plugin.getArenaManager().getArena(params.replaceAll("status_", ""));
            if (arena != null) {
                return arena.getBBArenaState().getPrefix();
            }
        }

        return String.valueOf(stats.getStat(BBStat.getStat(params)));
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
        return plugin.getServer().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}