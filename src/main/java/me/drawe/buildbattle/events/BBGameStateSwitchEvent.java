package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBArenaState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameStateSwitchEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private BBArena arena;
    private BBArenaState oldState;
    private BBArenaState newState;

    public BBGameStateSwitchEvent(BBArena arena, BBArenaState oldState, BBArenaState newState) {
        this.arena = arena;
        this.oldState = oldState;
        this.newState = newState;
        arena.resetAllScoreboards();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public BBArena getArena() {
        return arena;
    }

    public BBArenaState getOldState() {
        return oldState;
    }

    public BBArenaState getNewState() {
        return newState;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
