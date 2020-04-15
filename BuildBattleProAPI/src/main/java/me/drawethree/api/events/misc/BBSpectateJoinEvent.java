package me.drawethree.api.events.misc;

import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.spectateapi.SpectateJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class BBSpectateJoinEvent extends SpectateJoinEvent {

    private static final HandlerList handlerList = new HandlerList();


    public BBSpectateJoinEvent(Player player, BBArena arena) {
        super(player, arena);
    }


}
