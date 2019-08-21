package me.drawe.buildbattle.api.events.misc;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBSpectateQuitEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private Player player;
    private BBArena arena;

    public BBSpectateQuitEvent(Player player, BBArena arena) {
        this.player = player;
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public BBArena getArena() {
        return arena;
    }
}
