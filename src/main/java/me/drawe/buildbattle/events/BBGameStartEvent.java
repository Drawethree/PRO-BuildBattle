package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.BBArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameStartEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private BBArena arena;

    public BBGameStartEvent(BBArena arena){
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public BBArena getArena() {
        return arena;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
