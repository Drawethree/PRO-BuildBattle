package me.drawe.buildbattle.objects.bbobjects.arenanew;


import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBSign;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaEdit;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public abstract class BBArena {

    private String name;
    private String theme;
    protected BBGameMode gamemode;
    private Location lobbyLocation;
    private ArrayList<BBSign> arenaSigns;
    private ArrayList<BBPlot> arenaPlots;
    private ArrayList<Player> players;
    private BBArenaEdit arenaEdit;

    protected BukkitTask lobbyTask;
    protected BukkitTask gameTask;
    protected BukkitTask endTask;

    public BBArena(String name) {
        this.name = name;
        this.theme = null;
        this.players = new ArrayList<>();
        this.arenaEdit = new BBArenaEdit(this);
        this.loadArena();
    }

    public void loadArena() {
        this.lobbyLocation = LocationUtil.getLocationFromConfig("arenas.yml", name + ".lobbyLocation");
        this.arenaPlots = this.loadArenaPlots();
        this.arenaSigns = this.loadArenaSigns();
    }

    private List<BBSign> loadArenaSigns() {
        List<BBSign> list = new ArrayList<>();
        try {
            if (BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name) != null) {
                for (String sign : BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name).getKeys(false)) {
                    final Location signLoc = LocationUtil.getLocationFromString(sign);
                    final BBSign bbSign = new BBSign(this, signLoc);
                    if (bbSign.getLocation() != null) {
                        list.add(bbSign);
                    }
                }
            }
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading signs for arena §e" + name + "§c!");
            e.printStackTrace();
        }
        return list;
    }

    private List<BBPlot> loadArenaPlots() {
        List<BBPlot> list = new ArrayList<>();
        try {
            for (String plot : BuildBattle.getFileManager().getConfig("arenas.yml").get().getConfigurationSection(name + ".plots").getKeys(false)) {
                final Location minPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".min"));
                final Location maxPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".max"));
                list.add(new BBPlot(this, minPoint, maxPoint));
                BuildBattle.info("§aPlot §e" + plot + " §afor arena §e" + name + " §aloaded !");
            }
        } catch (Exception e) {
            BuildBattle.warning("§cLooks like arena §e" + name + " §c have no plots ! Please set them.");
        }
        return list;
    }

    public void saveArena() {
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".mode", this.gamemode.name());
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));

        BuildBattle.info("§aArena §e" + name + " §asuccessfully saved into config !");
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
    }

    public abstract void addPlayer(Player p);
    public abstract void removePlayer(Player p);

    public abstract void startLobby();
    public abstract void startGame();
    public abstract void endGame();

}
