package me.drawe.buildbattle.api;

import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.entity.Player;

import java.util.List;

public class BuildBattleProAPI {

    public static List<BBArena> getArenas() {
        return ArenaManager.getArenas();
    }

    public static BBArena getPlayerArena(Player p) {
        return PlayerManager.getInstance().getPlayerArena(p);
    }

    public static BBPlayerStats getPlayerBuildBattleStats(Player p) {
        return PlayerManager.getInstance().getPlayerStats(p);
}

    public static BBTeam getPlayerTeam(Player p) { return PlayerManager.getInstance().getPlayerTeam(getPlayerArena(p), p); }


}
