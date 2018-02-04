package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.particles.PlotParticle;
import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BBPlot implements Comparable<BBPlot> {

    private BBTeam team;
    private Location minPoint;
    private Location maxPoint;
    private BBArena arena;
    private BBPlotOptions options;
    private int votePoints;
    private HashMap<Player, Integer> votedPlayers;
    private List<PlotParticle> particles;
    private List<Location> blocksInPlot;

    public BBPlot(BBArena arena, Location minPoint, Location maxPoint) {
        this.arena = arena;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.votePoints = 0;
        this.options = new BBPlotOptions(this);
        this.votedPlayers = new HashMap<>();
        this.particles = new ArrayList<>();
        this.team = null;
        //setBlocksInPlot();
    }

    @Override
    public int compareTo(BBPlot bbPlot) {
        if(getVotePoints() == bbPlot.getVotePoints()) {
            return 0;
        } else if(getVotePoints() > bbPlot.getVotePoints()) {
            return 1;
        } else if(getVotePoints() < bbPlot.getVotePoints()) {
            return -1;
        }
        return 0;
    }

    public int getVotePoints() {
        return votePoints;
    }

    public void setVotePoints(int votePoints) {
        this.votePoints = votePoints;
    }

    public void setFinalPoints() {
        for(Integer i : getVotedPlayers().values()) {
            setVotePoints(getVotePoints() + i);
        }
    }

    public void restoreBBPlot() {
        setTeam(null);
        setVotePoints(0);
        removeAllBlocks();
        removeAllParticles();
        setParticles(new ArrayList<>());
        setVotedPlayers(new HashMap<>());
        ItemStack item = ItemCreator.getItemStack(GameManager.getDefaultFloorMaterial());
        changeFloor(item.getType(), item.getData().getData());
        setOptions(new BBPlotOptions(this));
    }

    private void removeAllBlocks() {
        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY + 1; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    Location tmpblock = new Location(getWorld(), x, y, z);
                    if(tmpblock.getBlock().getType() != Material.AIR) {
                        tmpblock.getBlock().setType(Material.AIR);
                    }
                    for (Entity e : tmpblock.getWorld().getNearbyEntities(tmpblock, 3, 3, 3)) {
                        if (e.getType() != EntityType.PLAYER) {
                            if(!e.hasMetadata("NPC")) {
                                e.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isLocationInPlot(Location location) {
        boolean trueOrNot = false;

        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());

        if (location.getWorld().equals(getMinPoint().getWorld())) {
            if (location.getBlockX() >= minX && location.getBlockX() <= maxX) {
                if (location.getBlockY() >= minY && location.getBlockY() <= maxY) {
                    if (location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public void setVotedPlayers(HashMap<Player, Integer> votedPlayers) {
        this.votedPlayers = votedPlayers;
    }

    public void vote(Player p, Votes item) {
        if(votedPlayers.containsKey(p)) {
            if(item.getWeight() != votedPlayers.get(p)) {
                votedPlayers.put(p, item.getWeight());
                p.sendMessage(Message.VOTE_CHANGED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
                if(GameManager.isShowVoteInSubtitle()) {
                    p.sendTitle("", item.getPrefix());
                }
                p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
            }
        } else {
            votedPlayers.put(p, item.getWeight());
            p.sendMessage(Message.VOTED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
            if(GameManager.isShowVoteInSubtitle()) {
                p.sendTitle("", item.getPrefix());
            }
            p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
        }
        if (GameManager.isScoreboardEnabled()) {
            getArena().updateAllScoreboards(0);
        }
    }

    public HashMap<Player, Integer> getVotedPlayers() {
        return votedPlayers;
    }

    public World getWorld() {
        return getMinPoint().getWorld();
    }

    public BBArena getArena() {
        return arena;
    }

    public void addIntoArenaPlots(){
        getArena().getBuildPlots().add(this);
    }

    public String getPlayerVoteString(Player p) {
        if(getVotedPlayers().containsKey(p)) {
            return Votes.getVoteItemByPoints(getVotedPlayers().get(p)).getPrefix();
        } else {
            return Votes.NONE.getPrefix();
        }
    }
    
    public Location getCenter(){
        double x,y,z = 0;
        if(getMinPoint().getX() > getMaxPoint().getX()){
            x = getMaxPoint().getX() + ((getMinPoint().getX()-getMaxPoint().getX())/2);
        }else{
            x = getMinPoint().getX() + ((getMaxPoint().getX()-getMinPoint().getX())/2);
        }
        if(getMinPoint().getY() > getMaxPoint().getY()){
            y = getMaxPoint().getY() + ((getMinPoint().getY()-getMaxPoint().getY())/2);
        }else{
            y = getMinPoint().getY() + ((getMaxPoint().getY()-getMinPoint().getY())/2);
        }
        if(getMinPoint().getZ() > getMaxPoint().getZ()){
            z = getMaxPoint().getZ() + ((getMinPoint().getZ()-getMaxPoint().getZ())/2);
        }else{
            z = getMinPoint().getZ() + ((getMaxPoint().getZ()-getMinPoint().getZ())/2);
        }
        return new Location(getMinPoint().getWorld(),x,y,z);

    }
    public BBPlotOptions getOptions() {
        return options;
    }

    public void setOptions(BBPlotOptions options) {
        this.options = options;
    }

    public Location getTeleportExactCenterLocation() {
        Location tpLoc = getCenter();
        tpLoc.clone().setY(getMinPoint().getY() + 1);
        return tpLoc;
    }

    public Location getTeleportLocation() {
        Location tploc = getCenter();
        while(tploc.getBlock().getType() != Material.AIR || tploc.add(0,1,0).getBlock().getType() != Material.AIR)
            tploc = tploc.add(0,1,0);
        boolean enclosed = false;
        int counter = 0;
        Location location = tploc.clone();
        while (counter!=10){
            if(!(location.getBlock().getType() == Material.BARRIER || location.getBlock().getType() == Material.AIR)){
                enclosed = true;
                tploc = location;
                counter = 9;
            }
            location.add(0,1,0);
            counter++;
        }
        if(enclosed) {
            while (tploc.getBlock().getType() != Material.AIR || tploc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
                tploc = tploc.add(0, 1, 0);
            }
        }
        return tploc;
    }

    public void changeFloor(Material material, int data) {
        if (material == Material.WATER_BUCKET)
            material = Material.STATIONARY_WATER;
        if (material == Material.LAVA_BUCKET)
            material = Material.STATIONARY_LAVA;
        double y = 0;
        if (getMinPoint().getBlockY() > getMaxPoint().getBlockY()) {
            y = getMaxPoint().getBlockY() - 1;
        } else {
            y = getMinPoint().getBlockY();
        }
        for (int x = getMinPoint().getBlockX(); x <= getMaxPoint().getBlockX(); x += 1) {
            for (int z = getMinPoint().getBlockZ(); z <= getMaxPoint().getBlockZ(); z += 1) {
                Location tmpblock = new Location(getWorld(), x, y, z);
                tmpblock.getBlock().setType(material);
                if (data != 0) {
                    tmpblock.getBlock().setData((byte)data);
                }
            }
        }
    }

    public void changeFloor(ItemStack item){
        Material m = item.getType();
        if (item.getType() == Material.WATER_BUCKET)
            m = Material.STATIONARY_WATER;
        if (item.getType() == Material.LAVA_BUCKET)
            m = Material.STATIONARY_LAVA;
        double y = 0;
        if (getMinPoint().getBlockY() > getMaxPoint().getBlockY()) {
            y = getMaxPoint().getBlockY() - 1;
        } else {
            y = getMinPoint().getBlockY();
        }
        for (int x = getMinPoint().getBlockX(); x <= getMaxPoint().getBlockX(); x += 1) {
            for (int z = getMinPoint().getBlockZ(); z <= getMaxPoint().getBlockZ(); z += 1) {
                Location tmpblock = new Location(getWorld(), x, y, z);
                tmpblock.getBlock().setType(m);
                tmpblock.getBlock().setData((byte) item.getData().getData());
            }
        }
    }

    public boolean isInPlotRange(Location location, int added){
        boolean trueOrNot = false;
        if (location.getWorld().equals(getMinPoint().getWorld()) && location.getWorld().equals(getMaxPoint().getWorld())) {
            if (location.getBlockX() >= getMinPoint().getBlockX()-added && location.getBlockX() <= getMaxPoint().getBlockX()+added){
                if (location.getBlockY() >= getMinPoint().getBlockY()-added&& location.getBlockY() <= getMaxPoint().getBlockY()+added) {
                    if (location.getBlockZ() >= getMinPoint().getBlockZ()-added && location.getBlockZ() <= getMaxPoint().getBlockZ()+added) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public Location getMaxPoint() {
        return maxPoint;
    }

    public Location getMinPoint() {
        return minPoint;
    }

    public Material getFloorMaterial() {
        return getMinPoint().getBlock().getType();
    }

    public List<PlotParticle> getParticles() {
        return particles;
    }

    public void setParticles(List<PlotParticle> particles) {
        this.particles = particles;
    }

    public void addActiveParticle(Player placer,PlotParticle plotParticle) {
        if(getParticles().size() != GameManager.getMaxParticlesPerPlayer()) {
            getParticles().add(plotParticle);
            plotParticle.start();
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(placer);
            if(stats != null) {
                stats.setParticlesPlaced(stats.getParticlesPlaced() + 1);
            }
        } else {
            placer.sendMessage(Message.MAX_PARTICLES.getChatMessage().replaceAll("%amount%", String.valueOf(GameManager.getMaxParticlesPerPlayer())));
        }
    }

    public void removeActiveParticle(PlotParticle plotParticle) {
        getParticles().remove(plotParticle);
        plotParticle.stop();
        getTeam().getCaptain().sendMessage(Message.PARTICLE_REMOVED.getChatMessage());
    }

    public void removeAllParticles() {
        Iterator it = getParticles().iterator();
        while(it.hasNext()) {
            PlotParticle particle = (PlotParticle) it.next();
            particle.stop();
            it.remove();
        }
    }

    public void resetPlotFromGame() {
        removeAllBlocks();
        removeAllParticles();
        ItemStack item = ItemCreator.getItemStack(GameManager.getDefaultFloorMaterial());
        getOptions().setCurrentFloorItem(item);
        getOptions().setCurrentWeather(WeatherType.CLEAR, false);
        getOptions().setCurrentTime(BBPlotTime.NOON, false);
        getTeam().getCaptain().sendMessage(Message.PLOT_CLEARED.getChatMessage());
    }

    public void setBlocksInPlot() {
        List<Location> locations = new ArrayList<>();
        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    Location loc = new Location(getWorld(), x, y, z);
                    locations.add(loc);
                }
            }
        }
        blocksInPlot = locations;
    }
    public List<Location> getBlocksInPlot() {
        return blocksInPlot;
    }

    public BBTeam getTeam() {
        return team;
    }
    public void setTeam(BBTeam team) {
        this.team = team;
    }

    public void teleportTeamToPlot(BBTeam team) {
        for(Player p : team.getPlayers()) {
            p.teleport(getTeleportExactCenterLocation());
        }
    }
}
