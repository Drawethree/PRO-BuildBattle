package me.drawe.buildbattle.utils;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.utils.compatbridge.VersionResolver;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

    public static void knockbackPlayer(Player p, Location from) {
        final Location loc1 = p.getLocation();

        final double deltaX = from.getX() - loc1.getX();//Get X Delta
        final double deltaZ = from.getZ() - loc1.getZ();//Get Z delta

        final Vector vec = new Vector(deltaX, 0, deltaZ);//Create new vector
        vec.normalize();//Normalize it so we don't shoot the player into oblivion
        p.setVelocity(vec.multiply(5 / (Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0)))));
    }

    public static Location getLocationFromConfig(String configName, String path) {
        try {
            String locString = BuildBattle.getFileManager().getConfig(configName).get().getString(path);
            return getLocationFromString(locString);
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying to get §e" + path + " §cfrom §e" + configName + "§c!");
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromLocation(Location l) {
        if (l != null) {
            return l.getWorld().getName() + "//" + l.getX() + "//" + l.getY() + "//" + l.getZ() + "//" + l.getYaw() + "//" + l.getPitch();
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
        if (s != null) {
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

    public static List<Location> getHollowCube(Location min, Location max) {
        List<Location> result = new ArrayList<>();
        World world = min.getWorld();

        double minX = Math.min(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxX = Math.max(min.getX(), max.getX());
        double maxY = Math.max(min.getY(), max.getY());
        double maxZ = Math.max(min.getZ(), max.getZ());

        for (double x = minX; x <= maxX; x += 1) {
            for (double y = minY; y <= maxY; y += 1) {
                for (double z = minZ; z <= maxZ; z += 1) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    public static void showCreatedPlot(Location l1, Location l2, Player p, int times) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= times) {
                    cancel();
                } else {
                    for (Location l : getHollowCube(l1, l2)) {
                        if (VersionResolver.isAtLeast1_13()) {
                            l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getCenter(l), 1);
                        } else {
                            ParticleEffect.VILLAGER_HAPPY.display(0f, 0f, 0f, 0f, 1, getCenter(l), p);
                        }
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
        for (Entity e : p.getNearbyEntities(5, 5, 5)) {
            NPC n = CitizensAPI.getNPCRegistry().getNPC(e);
            if (n != null) {
                return n;
            }
        }
        return null;
    }
}
