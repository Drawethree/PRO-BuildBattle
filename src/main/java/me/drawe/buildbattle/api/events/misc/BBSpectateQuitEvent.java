package me.drawe.buildbattle.api.events.misc;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.spectateapi.SpectateQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class BBSpectateQuitEvent extends SpectateQuitEvent {
    private static final HandlerList handlerList = new HandlerList();

    public BBSpectateQuitEvent(Player player, BBArena arena) {
        super(player,arena);
    }
}
