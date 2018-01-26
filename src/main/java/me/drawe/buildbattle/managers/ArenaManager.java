package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.*;
import me.drawe.buildbattle.objects.bbobjects.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaManager {
    private static ArenaManager ourInstance = new ArenaManager();

    public static ArenaManager getInstance() {
        return ourInstance;
    }

    private ArenaManager() {

    }

    public int getArenaListSize() {
        int arenasSize = GameManager.getArenas().size();
        int baseSize = 9;
        while(arenasSize > baseSize) {
            baseSize += 9;
        }
        return baseSize;
    }

    public void createArena(CommandSender sender, String name, String gamemode) {
        if (!existsArena(name)) {
            try {
                BBGameMode bbGameMode = BBGameMode.valueOf(gamemode.toUpperCase());
                BBArena newArena = new BBArena(name, bbGameMode);
                sender.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully created arena §e" + newArena.getName() + " §8[§e" + bbGameMode.name() + "§8]§a!");
            } catch (Exception e) {
                sender.sendMessage("§e§lBuildBattle Setup §8| §cInvalid arena type ! Valid types: §esolo, team ");
            }
        } else {
            sender.sendMessage(Message.ARENA_EXISTS.getChatMessage().replaceAll("%arena%", name));
        }

    }
    public void removeArena(CommandSender sender, BBArena arena) {
        arena.delete(sender);
    }


    public BBPlot getBBPlotFromLocation(Location l) {
        for(BBArena a : GameManager.getArenas()) {
            for(BBPlot plot : a.getBuildPlots()) {
                if(plot.isLocationInPlot(l)) {
                    return plot;
                }
            }
        }
        return null;
    }

    public BBPlot getPlotPlayerIsIn(BBArena a,Player p) {
        for (BBPlot plot : a.getBuildPlots()) {
            if (plot.isLocationInPlot(p.getLocation())) {
                return plot;
            }
        }
        return null;
    }

    public BBPlot getPlayerPlot(BBArena arena, Player p) {
        for(BBPlot plot : arena.getBuildPlots()) {
            if((plot.getTeam() != null) && (plot.getTeam().getPlayers().contains(p))) {
                return plot;
            }
        }
        return null;
    }

    public void resetAllPlots(BBArena arena) {
        for(BBPlot plot : arena.getBuildPlots()) {
            plot.restoreBBPlot();
        }
    }

    public boolean existsArena(String answer) {
        for(BBArena arena : GameManager.getArenas()) {
            if(arena.getName().equalsIgnoreCase(answer)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSignArenaSign(Sign s) {
        for(BBArena a : GameManager.getArenas()) {
            for(BBSign sign : a.getArenaSigns()) {
                if(sign.getSign().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public BBSign getArenaSign(Sign s) {
        for(BBArena a : GameManager.getArenas()) {
            for(BBSign sign : a.getArenaSigns()) {
                if(sign.getSign().equals(s)) {
                    return sign;
                }
            }
        }
        return null;
    }

    public BBArena getArena(String name) {
        for(BBArena arena : GameManager.getArenas()) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void saveAllArenasIntoConfig() {
        for(BBArena arena : GameManager.getArenas()) {
            arena.saveIntoConfig();
        }
    }

    public BBArena getArenaToAutoJoin() {
        for(BBArena a : GameManager.getArenas()) {
            if((a.getBBArenaState() == BBArenaState.LOBBY) && (!a.isFull())) {
                return a;
            }
        }
        return null;
    }

    public String getArenaStatus(BBArena a) {
        return "§e" + a.getName() + "§8: " + a.getTotalPlayers() + " §8| " + a.getBBArenaState().getPrefix();
    }
}
