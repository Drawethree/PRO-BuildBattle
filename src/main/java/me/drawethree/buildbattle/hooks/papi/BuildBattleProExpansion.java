package me.drawethree.buildbattle.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

public class BuildBattleProExpansion extends PlaceholderExpansion {

    private BuildBattle plugin;

    public BuildBattleProExpansion(BuildBattle plugin) {

        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return "";
        }

        if (identifier.contains("status")) {
            final BBArena arena = plugin.getArenaManager().getArena(identifier.replace("status_", ""));
            if (arena != null) {
                return arena.getBBArenaState().getPrefix();
            }
        }

        BBPlayerStats stats = plugin.getPlayerManager().getPlayerStats(p);

        if (stats == null) {
            return "Loading...";
        } else {
            return String.valueOf(stats.getStat(BBStat.getStat(identifier)));
        }
    }

    @Override
    public String getIdentifier() {
        return "buildbattlepro";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getServer().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }
}