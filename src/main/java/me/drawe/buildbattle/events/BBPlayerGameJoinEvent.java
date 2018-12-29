package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBPlayerGameJoinEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final BBArena arena;
    private final Player player;
    private boolean cancelled;

    /**
     * Called when gamestate changes
     *
     * @param arena  Arena in which gamestate has changed
     * @param player Player who joined
     */
    public BBPlayerGameJoinEvent(BBArena arena, Player player) {
        this.arena = arena;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public BBArena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = true;
    }
}
