package me.drawe.buildbattle.api.events.misc;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.Spectetable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBSpectateJoinEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final BBArena arena;
    private boolean cancelled;

    public BBSpectateJoinEvent(Player player, BBArena arena) {
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

    public Spectetable getArena() {
        return arena;
    }
}
