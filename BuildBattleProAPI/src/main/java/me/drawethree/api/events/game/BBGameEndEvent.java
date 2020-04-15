package me.drawethree.api.events.game;

import me.drawethree.buildbattle.objects.bbobjects.BBTeam;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameEndEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final BBArena arena;
    private final BBTeam winner;

    /**
     * Called when game ends
     *
     * @param arena  Arena that ended
     * @param winner Team that won
     */
    public BBGameEndEvent(BBArena arena, BBTeam winner) {
        this.arena = arena;
        this.winner = winner;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
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
}
