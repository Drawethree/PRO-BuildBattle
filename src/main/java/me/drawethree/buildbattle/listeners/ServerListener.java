package me.drawethree.buildbattle.listeners;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;

public class ServerListener implements Listener {

    private final BuildBattle plugin;

    public ServerListener(BuildBattle buildBattle) {
        this.plugin = buildBattle;
    }

    @EventHandler
    public void onMOTDChange(ServerListPingEvent e) {
        if (plugin.getSettings().isChangeMOTD()) {
            if (plugin.getArenaManager().getArenas().values().size() > 0) {
                BBArena mainArena = new ArrayList<>(plugin.getArenaManager().getArenas().values()).get(0);
                if (mainArena != null) {
                    e.setMotd(mainArena.getBBArenaState().getPrefix());
                }
            }
        }
    }
}
