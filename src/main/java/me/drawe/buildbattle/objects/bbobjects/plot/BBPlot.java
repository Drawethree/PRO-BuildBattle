package me.drawe.buildbattle.objects.bbobjects.plot;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawe.buildbattle.utils.compatbridge.model.CompatBridge;
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
        this.setBlocksInPlot();
        this.setChunksInPlot();
        this.restoreBBPlot();
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
        this.removeAllBlocks();
        this.removeAllParticles();
        this.votePoints = 0;
        this.reportedBy = null;
        this.votedPlayers = new HashMap<>();
        this.options = new BBPlotOptions(this);
        this.options.setCurrentBiome(PlotBiome.PLAINS, false);
        this.team = null;
        this.changeFloor(BBSettings.getDefaultFloorMaterial());
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
        return (location.getBlockX() >=  Math.min(minPoint.getBlockX(), maxPoint.getBlockX()) && location.getBlockX() <= Math.max(minPoint.getBlockX(), maxPoint.getBlockX()))
                && (location.getBlockY() >= Math.min(minPoint.getBlockY(), maxPoint.getBlockY()) && location.getBlockY() <= Math.max(minPoint.getBlockY(), maxPoint.getBlockY()))
                && (location.getBlockZ() >= Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ()) && location.getBlockZ() <= Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ()));
    }

    /*public void vote(Player p, Votes item) {
        if(votedPlayers.containsKey(p)) {
            if(item.getWeight() != votedPlayers.get(p)) {
                votedPlayers.put(p, item.getWeight());
                p.sendMessage(Message.VOTE_CHANGED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
                if(BBSettings.isShowVoteInSubtitle()) {
                    p.sendTitle("", item.getPrefix());
                }
                p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
            }
        } else {
            votedPlayers.put(p, item.getWeight());
            p.sendMessage(Message.VOTED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
            if(BBSettings.isShowVoteInSubtitle()) {
                p.sendTitle("", item.getPrefix());
            }
            p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
        }
        if (BBSettings.isScoreboardEnabled()) {
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

    public String getPlayerVoteString(Player p) {
        if (votedPlayers.containsKey(p)) {
            return Votes.getVoteItemByPoints(votedPlayers.get(p)).getPrefix();
        } else {
            return Votes.NONE.getPrefix();
        }
    }

    public Location getCenter() {
        double x, y, z;
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
        getCenter().clone().setY(minPoint.getY() + 1);
        return tpLoc;
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
        changeFloor(CompMaterial.fromItemStack(item));
    }

    public boolean isInPlotRange(Location location, int added) {
        if (location.getWorld().equals(minPoint.getWorld()) && location.getWorld().equals(maxPoint.getWorld())) {
            if (location.getBlockX() >= minPoint.getBlockX() - added && location.getBlockX() <= maxPoint.getBlockX() + added) {
                if (location.getBlockY() >= minPoint.getBlockY() - added && location.getBlockY() <= maxPoint.getBlockY() + added) {
                    if (location.getBlockZ() >= minPoint.getBlockZ() - added && location.getBlockZ() <= maxPoint.getBlockZ() + added) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Location getMaxPoint() {
        return maxPoint;
    }

    public Location getMinPoint() {
        return minPoint;
    }

    public List<BBPlotParticle> getParticles() {
        return particles;
    }

    public void addActiveParticle(Player player, BBPlotParticle particle) {
        if (particles.size() != BBSettings.getMaxParticlesPerPlayer()) {
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(player);
            if (stats != null) {
                stats.setParticlesPlaced(stats.getParticlesPlaced() + 1);
            }
            player.sendMessage(Message.PARTICLE_PLACED.getChatMessage());
            particle.start();
            particles.add(particle);
        } else {
            player.sendMessage(Message.MAX_PARTICLES.getChatMessage().replaceAll("%amount%", String.valueOf(BBSettings.getMaxParticlesPerPlayer())));
        }
    }

    public void removeActiveParticle(BBPlotParticle particle) {
        particles.remove(particle);
        particle.stop();
        team.getCaptain().sendMessage(Message.PARTICLE_REMOVED.getChatMessage());
    }

    private void removeAllParticles() {
        Iterator it = particles.iterator();
        while (it.hasNext()) {
            BBPlotParticle particle = (BBPlotParticle) it.next();
            particle.stop();
            it.remove();
        }
        this.particles = new ArrayList<>();
    }

    public void resetPlotFromGame() {
        this.removeAllBlocks();
        this.removeAllParticles();
        options.setCurrentFloorItem(BBSettings.getDefaultFloorMaterial().toItem());
        options.setCurrentWeather(WeatherType.CLEAR, false);
        options.setCurrentTime(BBPlotTime.NOON, false);
        options.setCurrentBiome(PlotBiome.PLAINS, false);
        team.getCaptain().sendMessage(Message.PLOT_CLEARED.getChatMessage());
    }

    private void setBlocksInPlot() {
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

    public void teleportTeamToPlot() {
        for (Player p : team.getPlayers()) {
            p.teleport(getTeleportExactCenterLocation());
        }
    }

    public List<Chunk> getChunksInPlot() {
        return chunksInPlot;
    }

    private void setChunksInPlot() {
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

    public Location getRandomLocationInPlot() {

        final int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        final int maxX = Math.max(minPoint.getBlockX(), maxPoint.getBlockX());
        final int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int maxZ = Math.max(minPoint.getBlockZ(), maxPoint.getBlockZ());
        final int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
        final int maxY = Math.max(minPoint.getBlockY(), maxPoint.getBlockY());

        int tries = 0;
        int randomX;
        int randomY;
        int randomZ;

        BuildBattle.debug("Generating random location..");
        do {
            tries++;
            BuildBattle.debug("Attempt no." + tries);
            randomX = new Random().nextInt(maxX - minX) + minX;
            randomY = new Random().nextInt(maxY - minY) + minY;
            randomZ = new Random().nextInt(maxZ - minZ) + minZ;
        }
        while ((getWorld().getBlockAt(randomX, randomY, randomZ).getType() != CompMaterial.AIR.getMaterial()) && tries < 10);

        BuildBattle.debug("Returning Location: " + getWorld().getName() + ", " + randomX + ", " + randomY + ", " + randomZ);
        return new Location(getWorld(), randomX, randomY, randomZ);
    }


    public List<Location> getPlotCorners() {
        List<Location> returnList = new ArrayList<>();
        Location min = minPoint;
        Location max = maxPoint;
        World w = min.getWorld();

        returnList.add(new Location(w, min.getBlockX(), min.getBlockY(), min.getBlockZ()));
        returnList.add(new Location(w, max.getBlockX(), min.getBlockY(), max.getBlockZ()));
        returnList.add(new Location(w, min.getBlockX(), min.getBlockY(), max.getBlockZ()));
        returnList.add(new Location(w, max.getBlockX(), min.getBlockY(), min.getBlockZ()));

        return returnList;
    }
}
