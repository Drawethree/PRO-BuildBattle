package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.Message;
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
                    switch (mainArena.getBBArenaState()) {
                        case LOBBY:
                            e.setMotd(Message.MOTD_LOBBY.getMessage());
                            break;
                        case INGAME:
                            e.setMotd(Message.MOTD_INGAME.getMessage());
                            break;
                        case VOTING:
                            e.setMotd(Message.MOTD_VOTING.getMessage());
                            break;
                        case ENDING:
                            e.setMotd(Message.MOTD_ENDING.getMessage());
                            break;
                    }
                }
            }
        }
    }
}
