package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerData {

    private Location l;
    private UUID uuid;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private GameMode gm;
    private int level;
    private float exp;
    private boolean allowFlight;

    public PlayerData(UUID uuid, ItemStack[] contents, ItemStack[] armor, Location loc, GameMode gm, int level, float exp, boolean allowFlight) {
        this.setContents(contents);
        this.setArmor(armor);
        this.l = loc;
        this.uuid = uuid;
        this.gm = gm;
        this.level = level;
        this.exp = exp;
        this.allowFlight = allowFlight;
    }

    public Location getLocation() {
        return l;
    }

    public GameMode getGamemode() {
        return gm;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    public void restorePlayerData() {
        getPlayer().getInventory().clear();
        getPlayer().resetPlayerWeather();
        getPlayer().resetPlayerTime();
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().setFoodLevel(20);
        getPlayer().getInventory().setArmorContents(getArmor());
        getPlayer().getInventory().setContents(getContents());
        getPlayer().setGameMode(getGamemode());
        getPlayer().teleport(getLocation());
        getPlayer().setLevel(getLevel());
        getPlayer().setExp(getExp());
        getPlayer().setAllowFlight(isAllowFlight());
        PlayerManager.getPlayerData().remove(this);
    }

    public int getLevel() {
        return level;
    }

    public float getExp() {
        return exp;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }
}
