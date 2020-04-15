package me.drawethree.api.events.game;

import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameStartEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final BBArena arena;

    /**
     * Called when game starts
     *
     * @param arena Arena that started
     */
    public BBGameStartEvent(BBArena arena) {
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public BBArena getArena() {
        return arena;
    }
}
