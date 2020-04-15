package me.drawethree.buildbattle.objects.bbobjects;

import me.drawethree.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BBPlayerStatsLoader implements Runnable {

    public static void load(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(BuildBattle.getInstance(), new BBPlayerStatsLoader(p));
    }

    private final Player player;

    private BBPlayerStatsLoader(Player p) {
        this.player = p;
    }

    @Override
    public void run() {
        switch (BuildBattle.getInstance().getSettings().getStatsType()) {
            case MYSQL:
                BuildBattle.getInstance().getMySQLManager().loadPlayer(player);
                break;
            case FLATFILE:
                BuildBattle.getInstance().getPlayerManager().loadPlayer(player);
                break;
        }
    }
}
