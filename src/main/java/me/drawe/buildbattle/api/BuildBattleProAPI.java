package me.drawe.buildbattle.api;

import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface BuildBattleProAPI {

    /**
     * Method to get all arenas
     *
     * @return List of all arenas
     */
    Collection<BBArena> getArenas();

    /**
     * Method to get player's arena
     *
     * @param p Player
     * @return Arena in which player is
     */
    BBArena getPlayerArena(Player p);

    /**
     * Method to get player's buildbattle stats
     *
     * @param p Player
     * @return BBPlayerStats object
     * @deprecated If player is offline, this method returns null. Will be replaced soon.
     */
    @Deprecated
    BBPlayerStats getPlayerBuildBattleStats(Player p);

    /**
     * Method to get player's team
     *
     * @param p Player
     * @return BBTeam object
     */
    BBTeam getPlayerTeam(Player p);


}
