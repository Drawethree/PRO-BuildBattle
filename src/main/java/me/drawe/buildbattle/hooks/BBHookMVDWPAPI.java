package me.drawe.buildbattle.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.OfflinePlayer;

public class BBHookMVDWPAPI extends BBHook {

    public BBHookMVDWPAPI() {
        super("MVdWPlaceholderAPI");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_wins",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.WINS));
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_played",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.PLAYED));
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_most_points",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.MOST_POINTS));
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_blocks_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.BLOCKS_PLACED));
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_particles_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.PARTICLES_PLACED));
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_super_votes",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = plugin.getPlayerManager().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getStat(BBStat.SUPER_VOTES));
                    }
                    return "0";
                });
        for (BBArena arena : plugin.getArenaManager().getArenas().values()) {
            PlaceholderAPI.registerPlaceholder(plugin, "buildbattlepro_status_" + arena.getName(),
                    placeholderReplaceEvent -> arena.getBBArenaState().getPrefix());
        }
    }
}
