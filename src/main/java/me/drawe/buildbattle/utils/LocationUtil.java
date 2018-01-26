package me.drawe.buildbattle.utils;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
            Bukkit.getConsoleSender().sendMessage("§cAn exception occurred while trying to get §e" + path + " §cfrom §e" + configName + "§c!");
            e.printStackTrace();
        }
        return null;
    }
    public static String getStringFromLocation(Location l) {
        if(l != null) {
            return l.getWorld().getName() + "//" + l.getBlockX() + "//" + l.getBlockY() + "//" + l.getBlockZ();
        } else {
            return null;
        }
    }

    public static Location getLocationFromString(String s) {
        String[] s1 = s.split("//");
        World w = Bukkit.getWorld(s1[0]);
        int x = Integer.parseInt(s1[1]);
        int y = Integer.parseInt(s1[2]);
        int z = Integer.parseInt(s1[3]);
        return new Location(w,x,y,z);
    }

    public static boolean isLocationSafe(Location l, int radius) {
        for(int x = l.getBlockX() - radius; x <= l.getBlockX()+radius; x++) {
            for(int y = l.getBlockY() - radius; y<= l.getBlockY()+radius; y++) {
                for(int z = l.getBlockZ() - radius; z <= l.getBlockZ()+radius; z++) {
                    Block b = l.getWorld().getBlockAt(x, y, z);
                    if(b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA) {
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
            Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + "§cCould not get all blocks between locations because they are not in same world !");
        }
        return result;
    }

    public static List<Location> getHollowCube(Location min, Location max) {
        List<Location> result = new ArrayList<>();
        World world = min.getWorld();
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

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
        returnList.add(new Location(w,min.getBlockX(),min.getBlockY(),min.getBlockZ()));
        returnList.add(new Location(w,max.getBlockX(),min.getBlockY(),max.getBlockZ()));
        returnList.add(new Location(w,min.getBlockX(),min.getBlockY(),max.getBlockZ()));
        returnList.add(new Location(w,max.getBlockX(),min.getBlockY(),min.getBlockZ()));
        return returnList;
    }

    public static void showCreatedPlot(Selection sel, Player p, int times) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= times) {
                    cancel();
                } else {
                    for(Location l : getHollowCube(sel.getMinimumPoint(), sel.getMaximumPoint())) {
                        if(Bukkit.getVersion().contains("1.12")) {
                            p.spawnParticle(Particle.VILLAGER_HAPPY, getCenter(l), 1);
                        } else {
                            p.playEffect(getCenter(l),Effect.HAPPY_VILLAGER,null);
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
}
