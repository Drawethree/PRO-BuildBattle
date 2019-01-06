package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
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
        if (BBSettings.isChangeMOTD()) {
            if (ArenaManager.getArenas().values().size() > 0) {
                BBArena mainArena = new ArrayList<>(ArenaManager.getArenas().values()).get(0);
                if (mainArena != null) {
                    e.setMotd(mainArena.getBBArenaState().getPrefix());
                }
            }
        }
    }
}
