package me.drawe.buildbattle.utils;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import me.kangarko.compatbridge.model.CompMaterial;
import me.kangarko.compatbridge.model.CompatBridge;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

    public static Location getLocationFromConfig(String configName, String path) {
        try {
            String locString = BuildBattle.getFileManager().getConfig(configName).get().getString(path);
            return getLocationFromString(locString);
        } catch(Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying to get §e" + path + " §cfrom §e" + configName + "§c!");
            e.printStackTrace();
        }
        return null;
    }
    public static String getStringFromLocation(Location l) {
        if(l != null) {
            return l.getWorld().getName() + "//" + l.getBlockX() + "//" + l.getBlockY() + "//" + l.getBlockZ() + "//" + l.getYaw() + "//" + l.getPitch();
        } else {
            return null;
        }
    }

    public static String getStringFromLocationXYZ(Location l) {
        if (l != null) {
            return l.getWorld().getName() + "//" + l.getBlockX() + "//" + l.getBlockY() + "//" + l.getBlockZ();
        } else {
            return null;
        }
    }

    public static Location getLocationFromString(String s) {
        if(s != null) {
            try {
                String[] s1 = s.split("//");
                World w = Bukkit.getWorld(s1[0]);
                double x = Double.parseDouble(s1[1]);
                double y = Double.parseDouble(s1[2]);
                double z = Double.parseDouble(s1[3]);
                float yaw = Float.parseFloat(s1[4]);
                float pitch = Float.parseFloat(s1[5]);
                return new Location(w, x, y, z, yaw, pitch);
            } catch (Exception e1) {
                try {
                    String[] s1 = s.split("//");
                    World w = Bukkit.getWorld(s1[0]);
                    double x = Double.parseDouble(s1[1]);
                    double y = Double.parseDouble(s1[2]);
                    double z = Double.parseDouble(s1[3]);
                    return new Location(w, x, y, z);
                } catch (Exception e2) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static boolean isLocationSafe(Location l, int radius) {
        for(int x = l.getBlockX() - radius; x <= l.getBlockX()+radius; x++) {
            for(int y = l.getBlockY() - radius; y<= l.getBlockY()+radius; y++) {
                for(int z = l.getBlockZ() - radius; z <= l.getBlockZ()+radius; z++) {
                    Block b = l.getWorld().getBlockAt(x, y, z);
                    if(b.getType() == CompMaterial.LAVA.getMaterial()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static List<Location> getBlocksBetweenLocations(Location l1, Location l2) {
        List<Location> result = new ArrayList<>();
        if(l1.getWorld().equals(l2.getWorld())) {
            int minX = Math.min(l1.getBlockX(), l2.getBlockX());
            int minY = Math.min(l1.getBlockY(), l2.getBlockY());
            int minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
            int maxX = Math.max(l1.getBlockX(), l2.getBlockX());
            int maxY = Math.max(l1.getBlockY(), l2.getBlockY());
            int maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());
            for (int x = minX; x <= maxX; x += 1) {
                for (int y = minY; y <= maxY; y += 1) {
                    for (int z = minZ; z <= maxZ; z += 1) {
                        result.add(new Location(l1.getWorld(), x,y,z));
                    }
                }
            }
        } else {
            BuildBattle.warning("§cCould not get all blocks between locations because they are not in same world !");
        }
        return result;
    }

    public static List<Location> getHollowCube(Location min, Location max) {
        List<Location> result = new ArrayList<>();
        World world = min.getWorld();
        double minX = Math.min(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxX = Math.max(min.getX(), max.getX());
        double maxY = Math.max(min.getY(), max.getY());
        double maxZ = Math.max(min.getZ(), max.getZ());

        for (double x = minX; x <= maxX; x+=1) {
            for (double y = minY; y <= maxY; y+=1) {
                for (double z = minZ; z <= maxZ; z+=1) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    public static Location getChunkCorner1(Player p,Chunk c) {
        return new Location(c.getWorld(), c.getX()*16, p.getLocation().getBlockY(), c.getZ()*16);
    }
    public static Location getChunkCorner2(Player p, Chunk c) {
        return new Location(c.getWorld(), (c.getX()*16)+ 15, p.getLocation().getBlockY()+ 15, (c.getZ()*16) + 15);
    }

    public static List<Location> getAllCornersOfPlot(BBPlot plot) {
        List<Location> returnList = new ArrayList<>();
        Location min = plot.getMinPoint();
        Location max = plot.getMaxPoint();
        World w = min.getWorld();
        returnList.add(new Location(w,min.getX(),min.getY(),min.getZ()));
        returnList.add(new Location(w,max.getX(),min.getY(),max.getZ()));
        returnList.add(new Location(w,min.getX(),min.getY(),max.getZ()));
        returnList.add(new Location(w,max.getX(),min.getY(),min.getZ()));
        return returnList;
    }

    public static void showCreatedPlot(Location l1, Location l2, Player p, int times) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= times) {
                    cancel();
                } else {
                    for(Location l : getHollowCube(l1, l2)) {
                        p.spawnParticle(Particle.VILLAGER_HAPPY, getCenter(l), 1);
                    }
                }
                count = count + 1;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 20L, 20L);
    }

    public static Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                        getRelativeCoord(loc.getBlockX()),
                getRelativeCoord(loc.getBlockY()),
                getRelativeCoord(loc.getBlockZ()));
    }

    private static double getRelativeCoord(int i) {
        double d = i;
        d = d < 0 ? d - .5 : d + .5;
        return d;
    }

    public static Block getAttachedBlock(Block b) {
        MaterialData m = b.getState().getData();
        BlockFace face = BlockFace.DOWN;
        if (m instanceof Attachable) {
            face = ((Attachable) m).getAttachedFace();
        }
        return b.getRelative(face);
    }

    public static NPC getClosestNPC(Player p) {
        for(Entity e : p.getNearbyEntities(5,5,5)) {
            NPC n = CitizensAPI.getNPCRegistry().getNPC(e);
            if(n != null) {
                return n;
            }
        }
        return null;
    }
}
