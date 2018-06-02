package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BBGameEndEvent extends Event {

    public static HandlerList handlerList = new HandlerList();

    private BBArena arena;
    private BBTeam winner;

    public BBGameEndEvent(BBArena arena, BBTeam winner) {
        this.arena = arena;
        this.winner = winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public BBArena getArena() {
        return arena;
    }

    public BBTeam getWinner() {
        return winner;
    }
}
