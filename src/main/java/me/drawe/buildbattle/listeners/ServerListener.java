package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    @EventHandler
    public void onMOTDChange(ServerListPingEvent e) {
        if(GameManager.isChangeMOTD()) {
            if (ArenaManager.getArenas().size() > 0) {
                BBArena mainArena = ArenaManager.getArenas().get(0);
                if (mainArena != null) {
                    e.setMotd(mainArena.getBBArenaState().getPrefix());
                }
            }
        }
    }
}
