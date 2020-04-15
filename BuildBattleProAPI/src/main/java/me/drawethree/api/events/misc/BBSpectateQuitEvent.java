package me.drawethree.api.events.misc;

import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.spectateapi.SpectateQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class BBSpectateQuitEvent extends SpectateQuitEvent {
    private static final HandlerList handlerList = new HandlerList();

    public BBSpectateQuitEvent(Player player, BBArena arena) {
        super(player, arena);
    }
}
