package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.*;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {
    private static ArenaManager ourInstance = new ArenaManager();
    private static List<BBArena> arenas = new ArrayList<>();

    public static ArenaManager getInstance() {
        return ourInstance;
    }

    private ArenaManager() {

    }

    public int getArenaListSize() {
        int arenasSize = getArenas().size();
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
        for(BBArena a : getArenas()) {
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
        for(BBArena arena : getArenas()) {
            if(arena.getName().equalsIgnoreCase(answer)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSignArenaSign(Sign s) {
        for(BBArena a : getArenas()) {
            for(BBSign sign : a.getArenaSigns()) {
                if(sign.getSign().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public BBSign getArenaSign(Sign s) {
        for(BBArena a : getArenas()) {
            for(BBSign sign : a.getArenaSigns()) {
                if(sign.getSign().equals(s)) {
                    return sign;
                }
            }
        }
        return null;
    }

    public BBArena getArena(String name) {
        for(BBArena arena : getArenas()) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void saveAllArenasIntoConfig() {
        for(BBArena arena : getArenas()) {
            arena.saveIntoConfig();
        }
    }

    public BBArena getArenaToAutoJoin() {
        for(BBArena a : getArenas()) {
            if((a.getBBArenaState() == BBArenaState.LOBBY) && (!a.isFull())) {
                return a;
            }
        }
        return null;
    }

    public String getArenaStatus(BBArena a) {
        return "§e" + a.getName() + "§8: " + a.getTotalPlayers() + " §8| " + a.getBBArenaState().getPrefix();
    }

    public static List<BBArena> getArenas() {
        return arenas;
    }
    public void loadArenas() {
        try {
            arenas = new ArrayList<>();
            for (String arena : BuildBattle.getFileManager().getConfig("arenas.yml").get().getKeys(false)) {
                String name = arena;
                int minPlayers = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(arena + ".min_players");
                if(!BuildBattle.getFileManager().getConfig("arenas.yml").get().isSet(arena + ".mode")) {
                    BuildBattle.getFileManager().getConfig("arenas.yml").get().set(arena + ".mode", BBGameMode.SOLO.name());
                    BuildBattle.getFileManager().getConfig("arenas.yml").save();
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set mode ! Automatically set to SOLO");
                }
                BBGameMode gameMode = BBGameMode.valueOf(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(arena + ".mode"));
                if(!BuildBattle.getFileManager().getConfig("arenas.yml").get().isSet(arena + ".teamSize")) {
                    BuildBattle.getFileManager().getConfig("arenas.yml").get().set(arena + ".teamSize", gameMode.getDefaultTeamSize());
                    BuildBattle.getFileManager().getConfig("arenas.yml").save();
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set teamSize ! Automatically set to " + gameMode.getDefaultTeamSize());
                }
                int teamSize = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(arena + ".teamSize");
                Location lobbyLoc = LocationUtil.getLocationFromConfig("arenas.yml", arena + ".lobbyLocation");
                if(lobbyLoc == null) {
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set lobby location !");
                }
                BBArena bbArena = new BBArena(name, minPlayers, gameMode, teamSize, lobbyLoc, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                loadBBPlots(bbArena);
                loadBBSigns(bbArena);
                bbArena.setupTeams();
                bbArena.setupTeamInventory();
                Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aArena §e" + arena + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cAn exception occurred while trying loading arenas !");
            e.printStackTrace();
        }
    }

    public void loadBBPlots(BBArena a) {
        try {
            for (String plot : BuildBattle.getFileManager().getConfig("arenas.yml").get().getConfigurationSection(a.getName() + ".plots").getKeys(false)) {
                Location minPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(a.getName() + ".plots." + plot + ".min"));
                Location maxPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(a.getName() + ".plots." + plot + ".max"));
                BBPlot bbPlot = new BBPlot(a,minPoint,maxPoint);
                bbPlot.addIntoArenaPlots();
                bbPlot.restoreBBPlot();
                Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aPlot §e" + plot + " §afor arena §e" + a.getName() + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cLooks like arena §e" + a.getName() + " §c have no plots ! Please set them.");
        }
    }

    private void loadBBSigns(BBArena a) {
        try {
            if (BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(a.getName()) != null) {
                for (String sign : BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(a.getName()).getKeys(false)) {
                    Location signLoc = LocationUtil.getLocationFromString(sign);
                    //BBSign constructor already adds this sign into BBArena signs and update it.
                    BBSign bbSign = new BBSign(a, signLoc);
                }
            }
        } catch(Exception e){
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cAn exception occurred while trying loading signs for arena §e" + a.getName() + "§c!");
            e.printStackTrace();
        }
    }
}
