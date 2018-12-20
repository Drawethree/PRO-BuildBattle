package me.drawe.buildbattle.api;

import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

import java.util.List;

public final class BuildBattleProAPI {

    /**
     * Method to get all arenas
     *
     * @return List of all arenas
     */
    public static List<BBArena> getArenas() {
        return ArenaManager.getArenas();
    }

    /**
     * Method to get player's arena
     *
     * @param p Player
     * @return Arena in which player is
     */
    public static BBArena getPlayerArena(Player p) {
        return PlayerManager.getInstance().getPlayerArena(p);
    }

    /**
     * Method to get player's buildbattle stats
     *
     * @param p Player
     * @return BBPlayerStats object
     */
    public static BBPlayerStats getPlayerBuildBattleStats(Player p) {
        return PlayerManager.getInstance().getPlayerStats(p);
    }

    /**
     * Method to get player's team
     * @param p Player
     * @return BBTeam object
     */
    public static BBTeam getPlayerTeam(Player p) {
        return PlayerManager.getInstance().getPlayerTeam(getPlayerArena(p), p);
    }


}
