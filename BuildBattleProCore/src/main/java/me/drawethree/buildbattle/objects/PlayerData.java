package me.drawethree.buildbattle.objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerData {

    private Location location;
    private UUID uuid;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private GameMode gameMode;
    private int level;
    private float exp;
    private boolean allowFlight;

    public PlayerData(Player p) {
        this.contents = p.getInventory().getContents();
        this.armor = p.getInventory().getArmorContents();
        this.location = p.getLocation();
        this.uuid = p.getUniqueId();
        this.gameMode = p.getGameMode();
        this.level = p.getLevel();
        this.exp = p.getExp();
        this.allowFlight = p.getAllowFlight();
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void restorePlayerData() {
        getPlayer().getInventory().clear();
        getPlayer().resetPlayerWeather();
        getPlayer().resetPlayerTime();
        getPlayer().getActivePotionEffects().forEach(e -> getPlayer().removePotionEffect(e.getType()));
        getPlayer().setMaxHealth(20);
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);
        getPlayer().getInventory().setArmorContents(armor);
        getPlayer().getInventory().setContents(contents);
        getPlayer().setGameMode(gameMode);
        getPlayer().setLevel(level);
        getPlayer().setExp(exp);
        getPlayer().setAllowFlight(allowFlight);
        getPlayer().teleport(location);
    }
}
