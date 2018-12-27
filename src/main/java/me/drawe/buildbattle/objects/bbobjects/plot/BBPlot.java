package me.drawe.buildbattle.objects.bbobjects.plot;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.kangarko.compatbridge.model.CompMaterial;
import me.kangarko.compatbridge.model.CompatBridge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BBPlot implements Comparable<BBPlot> {

    private BBTeam team;
    private Location minPoint;
    private Location maxPoint;
    private BBArena arena;
    private BBPlotOptions options;
    private int votePoints;
    private HashMap<Player, Integer> votedPlayers;
    private List<BBPlotParticle> particles;
    private List<Location> blocksInPlot;
    private List<Chunk> chunksInPlot;
    private UUID reportedBy;

    public BBPlot(BBArena arena, Location minPoint, Location maxPoint) {
        this.arena = arena;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.votePoints = 0;
        this.options = new BBPlotOptions(this);
        this.votedPlayers = new HashMap<>();
        this.particles = new ArrayList<>();
        this.reportedBy = null;
        this.team = null;
        setBlocksInPlot();
        setChunksInPlot();
    }

    @Override
    public int compareTo(BBPlot bbPlot) {
        if (votePoints == bbPlot.getVotePoints()) {
            return 0;
        } else if (votePoints > bbPlot.getVotePoints()) {
            return 1;
        } else if (votePoints < bbPlot.getVotePoints()) {
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
        for (Integer i : votedPlayers.values()) {
            this.votePoints += i;
        }
    }

    public void restoreBBPlot() {
        this.team = null;
        this.votePoints = 0;
        this.particles = new ArrayList<>();
        this.reportedBy = null;
        this.votedPlayers = new HashMap<>();
        this.options = new BBPlotOptions(this);
        options.setCurrentBiome(PlotBiome.PLAINS, false);
        changeFloor(GameManager.getDefaultFloorMaterial());
    }

    private void removeAllBlocks() {
        final int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        final int maxX = Math.max(minPoint.getBlockX(), maxPoint.getBlockX());
        final int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int maxZ = Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
        final int maxY = Math.max(minPoint.getBlockY(), maxPoint.getBlockY());

        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY + 1; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    final Location tmpblock = new Location(getWorld(), x, y, z);
                    if (tmpblock.getBlock().getType() != CompMaterial.AIR.getMaterial()) {
                        tmpblock.getBlock().setType(CompMaterial.AIR.getMaterial());
                    }
                    for (Entity e : tmpblock.getWorld().getNearbyEntities(tmpblock, 3, 3, 3)) {
                        if (e.getType() != EntityType.PLAYER) {
                            if (!e.hasMetadata("NPC")) {
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

        final int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        final int maxX = Math.max(minPoint.getBlockX(), maxPoint.getBlockX());
        final int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int maxZ = Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
        final int maxY = Math.max(minPoint.getBlockY(), maxPoint.getBlockY());

        if (location.getWorld().equals(minPoint.getWorld())) {
            if ((location.getBlockX() >= minX) && (location.getBlockX() <= maxX)) {
                if ((location.getBlockY() >= minY) && (location.getBlockY() <= maxY)) {
                    if ((location.getBlockZ() >= minZ) && (location.getBlockZ() <= maxZ)) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    /*public void vote(Player p, Votes item) {
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
    */

    public HashMap<Player, Integer> getVotedPlayers() {
        return votedPlayers;
    }

    public World getWorld() {
        return minPoint.getWorld();
    }

    public BBArena getArena() {
        return arena;
    }

    public void addIntoArenaPlots() {
        arena.getBuildPlots().add(this);
    }

    public String getPlayerVoteString(Player p) {
        if (votedPlayers.containsKey(p)) {
            return Votes.getVoteItemByPoints(votedPlayers.get(p)).getPrefix();
        } else {
            return Votes.NONE.getPrefix();
        }
    }

    public Location getCenter() {
        double x, y, z = 0;
        if (minPoint.getX() > maxPoint.getX()) {
            x = maxPoint.getX() + ((minPoint.getX() - maxPoint.getX()) / 2);
        } else {
            x = minPoint.getX() + ((maxPoint.getX() - minPoint.getX()) / 2);
        }
        if (minPoint.getY() > maxPoint.getY()) {
            y = maxPoint.getY() + ((minPoint.getY() - maxPoint.getY()) / 2);
        } else {
            y = minPoint.getY() + ((maxPoint.getY() - minPoint.getY()) / 2);
        }
        if (minPoint.getZ() > maxPoint.getZ()) {
            z = maxPoint.getZ() + ((minPoint.getZ() - maxPoint.getZ()) / 2);
        } else {
            z = minPoint.getZ() + ((maxPoint.getZ() - minPoint.getZ()) / 2);
        }
        return new Location(minPoint.getWorld(), x, y, z);

    }

    public BBPlotOptions getOptions() {
        return options;
    }

    public Location getTeleportExactCenterLocation() {
        Location tpLoc = getCenter();
        tpLoc.clone().setY(minPoint.getY() + 1);
        return tpLoc;
    }

    public Location getTeleportLocation() {
        Location tploc = getCenter();
        while (tploc.getBlock().getType() != CompMaterial.AIR.getMaterial() || tploc.clone().add(0, 1, 0).getBlock().getType() != CompMaterial.AIR.getMaterial())
            tploc = tploc.add(0, 1, 0);
        boolean enclosed = false;
        int counter = 0;
        Location location = tploc.clone();
        while (counter != 10) {
            if (!(location.getBlock().getType() == CompMaterial.BARRIER.getMaterial() || location.getBlock().getType() == CompMaterial.AIR.getMaterial())) {
                enclosed = true;
                tploc = location;
                counter = 9;
            }
            location.add(0, 1, 0);
            counter++;
        }
        if (enclosed) {
            while (tploc.getBlock().getType() != CompMaterial.AIR.getMaterial() || tploc.add(0, 1, 0).getBlock().getType() != CompMaterial.AIR.getMaterial()) {
                tploc = tploc.add(0, 1, 0);
            }
        }
        return tploc;
    }

    public void changeFloor(CompMaterial material) {
        if (material == CompMaterial.WATER_BUCKET) material = CompMaterial.WATER;
        if (material == CompMaterial.LAVA_BUCKET) material = CompMaterial.LAVA;

        final int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        final int maxX = Math.max(minPoint.getBlockX(), maxPoint.getBlockX());
        final int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int maxZ = Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());

        for (int x = minX; x <= maxX; x += 1) {
            for (int z = minZ; z <= maxZ; z += 1) {
                CompatBridge.setTypeAndData(new Location(getWorld(), x, minY, z).getBlock(), material, (byte) material.getData());
            }
        }
    }

    public void changeFloor(ItemStack item) {
        CompMaterial m = CompMaterial.fromItemStack(item);
        changeFloor(m);
    }

    public boolean isInPlotRange(Location location, int added) {
        boolean trueOrNot = false;
        if (location.getWorld().equals(minPoint.getWorld()) && location.getWorld().equals(maxPoint.getWorld())) {
            if (location.getBlockX() >= minPoint.getBlockX() - added && location.getBlockX() <= maxPoint.getBlockX() + added) {
                if (location.getBlockY() >= minPoint.getBlockY() - added && location.getBlockY() <= maxPoint.getBlockY() + added) {
                    if (location.getBlockZ() >= minPoint.getBlockZ() - added && location.getBlockZ() <= maxPoint.getBlockZ() + added) {
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

    public CompMaterial getFloorMaterial() {
        return CompMaterial.fromMaterial(minPoint.getBlock().getType());
    }

    public List<BBPlotParticle> getParticles() {
        return particles;
    }

    public void addActiveParticle(Player player, BBPlotParticle particle) {
        if (particles.size() != GameManager.getMaxParticlesPerPlayer()) {
            particles.add(particle);
            particle.start();
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(player);
            if (stats != null) {
                stats.setParticlesPlaced(stats.getParticlesPlaced() + 1);
            }
            player.sendMessage(Message.PARTICLE_PLACED.getChatMessage());
        } else {
            player.sendMessage(Message.MAX_PARTICLES.getChatMessage().replaceAll("%amount%", String.valueOf(GameManager.getMaxParticlesPerPlayer())));
        }
    }

    public void removeActiveParticle(BBPlotParticle particle) {
        particles.remove(particle);
        particle.stop();
        team.getCaptain().sendMessage(Message.PARTICLE_REMOVED.getChatMessage());
    }

    public void removeAllParticles() {
        Iterator it = particles.iterator();
        while (it.hasNext()) {
            BBPlotParticle particle = (BBPlotParticle) it.next();
            particle.stop();
            it.remove();
        }
    }

    public void resetPlotFromGame() {
        removeAllBlocks();
        removeAllParticles();
        options.setCurrentFloorItem(GameManager.getDefaultFloorMaterial().toItem());
        options.setCurrentWeather(WeatherType.CLEAR, false);
        options.setCurrentTime(BBPlotTime.NOON, false);
        options.setCurrentBiome(PlotBiome.PLAINS, false);
        team.getCaptain().sendMessage(Message.PLOT_CLEARED.getChatMessage());
    }

    public void setBlocksInPlot() {
        List<Location> locations = new ArrayList<>();
        final int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        final int maxX = Math.max(minPoint.getBlockX(), maxPoint.getBlockX());
        final int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int maxZ = Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
        final int maxY = Math.max(minPoint.getBlockY(), maxPoint.getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    locations.add(new Location(getWorld(), x, y, z));
                }
            }
        }
        this.blocksInPlot = locations;
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
        for (Player p : team.getPlayers()) {
            p.teleport(getTeleportExactCenterLocation());
        }
    }

    public List<Chunk> getChunksInPlot() {
        return chunksInPlot;
    }

    public void setChunksInPlot() {
        List<Chunk> chunks = new ArrayList<>();
        for (Location l : blocksInPlot) {
            final Chunk c = l.getChunk();
            if (!chunks.contains(c)) {
                chunks.add(c);
            }
        }
        this.chunksInPlot = chunks;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UUID reportedBy) {
        this.reportedBy = reportedBy;
    }
}
