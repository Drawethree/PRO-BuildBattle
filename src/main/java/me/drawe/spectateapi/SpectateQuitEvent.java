package me.drawe.spectateapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpectateQuitEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Spectatable arena;

    public SpectateQuitEvent(Player player, Spectatable arena) {
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

    public Spectatable getArena() {
        return arena;
    }
}
