package me.drawe.spectateapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpectateJoinEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Spectatable arena;
    private boolean cancelled;

    public SpectateJoinEvent(Player player, Spectatable arena) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public Spectatable getArena() {
        return arena;
    }
}
