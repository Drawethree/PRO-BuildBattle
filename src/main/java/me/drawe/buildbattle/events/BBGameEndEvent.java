package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private BBArena arena;
    private BBTeam winner;

    /**
     * Called when game ends
     * @param arena Arena that ended
     * @param winner Team that won
     */
    public BBGameEndEvent(BBArena arena, BBTeam winner) {
        this.arena = arena;
        this.winner = winner;
    }

    public BBArena getArena() {
        return arena;
    }

    public BBTeam getWinner() {
        return winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
