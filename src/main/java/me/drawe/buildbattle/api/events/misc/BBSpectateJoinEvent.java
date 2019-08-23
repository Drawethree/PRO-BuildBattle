package me.drawe.buildbattle.api.events.misc;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.spectateapi.SpectateJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class BBSpectateJoinEvent extends SpectateJoinEvent {

    private static final HandlerList handlerList = new HandlerList();


    public BBSpectateJoinEvent(Player player, BBArena arena) {
        super(player,arena);
    }


}
