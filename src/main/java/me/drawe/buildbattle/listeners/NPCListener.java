package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    private final BuildBattle plugin;

    public NPCListener(BuildBattle buildBattle) {
        this.plugin = buildBattle;
    }

    @EventHandler
    public void onNPCLClick(NPCLeftClickEvent e) {
        this.manageNPCEvent(e);
    }

    @EventHandler
    public void onNPCRClick(NPCRightClickEvent e) {
        this.manageNPCEvent(e);
    }

    private void manageNPCEvent(NPCClickEvent e) {
        Player p = e.getClicker();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if((a != null) && (a.getBBArenaState() == BBArenaState.INGAME)) {
            BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a,p);
            if(plot != null) {
                if((p.getItemInHand() != null) && (p.getItemInHand().getType() != CompMaterial.AIR.getMaterial())) {
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
