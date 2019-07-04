package me.drawe.buildbattle.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.OfflinePlayer;

public class BBHookMVDWPAPI extends BBHook {

    public BBHookMVDWPAPI() {
        super("MVdWPlaceholderAPI");
    }

    @Override
    protected void runHookAction() {
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_wins",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getWins());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_played",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getPlayed());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_most_points",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getMostPoints());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_blocks_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getBlocksPlaced());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_particles_placed",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getParticlesPlaced());
                    }
                    return "0";
                });
        PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_super_votes",
                event -> {
                    OfflinePlayer offlinePlayer = event.getPlayer();
                    BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(offlinePlayer);
                    if (pStats != null) {
                        return String.valueOf(pStats.getSuperVotes());
                    }
                    return "0";
                });
        for (BBArena arena : ArenaManager.getArenas().values()) {
            PlaceholderAPI.registerPlaceholder(BuildBattle.getInstance(), "buildbattlepro_status_" + arena.getName(),
                    placeholderReplaceEvent -> arena.getBBArenaState().getPrefix());
        }
    }
}
