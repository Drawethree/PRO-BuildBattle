package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBArenaState;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCLClick(NPCLeftClickEvent e) {
        Player p = e.getClicker();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if((a != null) && (a.getBBArenaState() == BBArenaState.INGAME)) {
            BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a,p);
            if(plot != null) {
                if((p.getItemInHand() != null) && (p.getItemInHand().getType() != Material.AIR)) {
                    if (p.hasPermission("buildbattlepro.changefloor")) {
                        plot.getOptions().setCurrentFloorItem(p.getItemInHand());
                    } else {
                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onNPCRClick(NPCRightClickEvent e) {
        Player p = e.getClicker();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if((a != null) && (a.getBBArenaState() == BBArenaState.INGAME)) {
            BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a,p);
            if(plot != null) {
                if((p.getItemInHand() != null) && (p.getItemInHand().getType() != Material.AIR)) {
                    if (p.hasPermission("buildbattlepro.changefloor")) {
                        plot.getOptions().setCurrentFloorItem(p.getItemInHand());
                    } else {
                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                    }
                }
            }
        }
    }

}
