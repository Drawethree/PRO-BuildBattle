package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.sign.*;
import me.drawethree.buildbattle.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class SignManager {

    private BuildBattle plugin;
    private HashMap<Location, BBSign> loadedSigns;

    public SignManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.loadedSigns = new HashMap<>();
    }

    public void loadSigns() {
        this.loadedSigns = new HashMap<>();

        //Check if we want to import from old config.
        if (this.plugin.getFileManager().getConfig("signs.yml").get().getBoolean("import-old-signs")) {
            this.importFromOldConfig();
            return;
        }

        for (BBSignType type : BBSignType.values()) {
            for (String locationString : this.plugin.getFileManager().getConfig("signs.yml").get().getConfigurationSection("signs." + type.getConfigPath()).getKeys(false)) {

                BBSign loadedSign = null;
                Location location = LocationUtil.getLocationFromStringXYZ(locationString);

                switch (type) {
                    case JOIN:
                        loadedSign = new BBArenaJoinSign(this.plugin, location);
                        break;
                    case LEAVE:
                        loadedSign = new BBArenaLeaveSign(this.plugin, location);
                        break;
                    case AUTO_JOIN:
                        loadedSign = new BBAutoJoinSign(this.plugin, location);
                        break;
					case SPECTATE:
						loadedSign = new BBArenaSpectateSign(this.plugin, location);
						break;
                }

                if (loadedSign != null && loadedSign.isValid()) {
                    this.loadedSigns.put(loadedSign.getLocation(), loadedSign);
                } else {
                    this.plugin.warning("Sign at location " + locationString + " is invalid!");
                    loadedSign.printInvalidCause();
                }
            }
        }
    }

    public void importFromOldConfig() {

        this.plugin.info("Starting importing signs from old signs.yml...");

        for (BBArena arena : this.plugin.getArenaManager().getArenas().values()) {
            this.plugin.info("Importing from arena " + arena.getName());

            BBArenaJoinSign loadedSign;
            Location loc;

            if(!this.plugin.getFileManager().getConfig("signs.yml").get().contains(arena.getName())) {
                continue;
            }

            for (String locationString : this.plugin.getFileManager().getConfig("signs.yml").get().getConfigurationSection(arena.getName()).getKeys(false)) {

                loc = LocationUtil.getLocationFromStringXYZ(locationString);
                loadedSign = new BBArenaJoinSign(this.plugin, arena, loc);

                if (loadedSign != null && loadedSign.isValid()) {
                    this.loadedSigns.put(loadedSign.getLocation(), loadedSign);
                    loadedSign.save();
                } else {
                    this.plugin.warning("Sign at location " + locationString + " is invalid!");
                    loadedSign.printInvalidCause();
                }
            }

            this.plugin.getFileManager().getConfig("signs.yml").set(arena.getName(), null);

        }
        this.plugin.info("Done! Setting import-old-signs to false.");
        this.plugin.getFileManager().getConfig("signs.yml").set("import-old-signs", false).save();
    }

    public BBSign getSignAtLocation(Location location) {
        return loadedSigns.get(location);
    }

    public boolean createSign(BBSign sign) {
        if (sign != null && sign.isValid()) {
            sign.save();
            this.loadedSigns.put(sign.getLocation(), sign);
            return true;
        }
        return false;
    }

    public void updateAllSigns(BBArena arena) {
        if(!Bukkit.getPluginManager().isPluginEnabled(this.plugin)) {
            return;
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                for (BBSign sign : loadedSigns.values()) {
                    if (sign instanceof BBArenaSign && ((BBArenaSign) sign).getArena() == arena) {
                        sign.update();
                    }
                }
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public void removeSign(Player p, BBSign bbSign) {
        bbSign.removeSign(p);
        this.loadedSigns.remove(bbSign.getLocation());
    }
}
