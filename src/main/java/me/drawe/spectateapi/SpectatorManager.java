package me.drawe.spectateapi;

import lombok.Getter;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class SpectatorManager implements Listener {

    private final JavaPlugin plugin;
    @Getter
    private final HashMap<Player, Spectatable> spectators;
    private final HashMap<Player, PlayerData> playerData;

    public SpectatorManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.spectators = new HashMap<>();
        this.playerData = new HashMap<>();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage2(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getDamager();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInterract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p) && e.getItem() != null) {
            e.setCancelled(true);
            if (e.getItem().isSimilar(Spectatable.SPECTATE_ITEM)) {
                this.openSpectateInventory(p);
            } else if (e.getItem().isSimilar(Spectatable.QUIT_SPECTATE_ITEM)) {
                this.unspectate(p);
            }
        }
    }

    private void openSpectateInventory(Player p) {
        p.openInventory(this.spectators.get(p).getSpectateInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (this.spectators.containsKey(p) && e.getCurrentItem() != null && e.getCurrentItem().getType() == CompMaterial.PLAYER_HEAD.getMaterial()) {
            e.setCancelled(true);
            p.teleport(Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName()).getLocation());
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (this.spectators.containsKey(p)) {
            e.setCancelled(true);
        }
    }


    public void spectate(Player p, Spectatable target) {

        if (this.spectators.containsKey(p)) {
            return;
        }

        SpectateJoinEvent event = new SpectateJoinEvent(p, target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        this.playerData.put(p, new PlayerData(p));

        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);

        this.hidePlayer(p);

        p.getInventory().setItem(0, Spectatable.SPECTATE_ITEM);
        p.getInventory().setItem(8, Spectatable.QUIT_SPECTATE_ITEM);

        this.spectators.put(p, target);

        target.spectate(p);
    }

    public void unspectate(Player p) {
        if (!spectators.containsKey(p)) {
            return;
        }

        Spectatable spectatable = spectators.remove(p);

        this.playerData.remove(p).restorePlayerData();
        this.showPlayer(p);

        spectatable.unspectate(p);

        SpectateQuitEvent event = new SpectateQuitEvent(p, spectatable);
        Bukkit.getPluginManager().callEvent(event);

    }

    private void showPlayer(Player p) {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            if (p1.equals(p))
                continue;
            p1.showPlayer(p);
        }
    }

    private void hidePlayer(Player p) {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            if (p1.equals(p))
                continue;
            p1.hidePlayer(p);
        }
    }

    private class PlayerData {

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

        public Location getLocation() {
            return location;
        }

        public GameMode getGamemode() {
            return gameMode;
        }

        public ItemStack[] getContents() {
            return contents;
        }

        public ItemStack[] getArmor() {
            return armor;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Player getPlayer() {
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

        public int getLevel() {
            return level;
        }

        public float getExp() {
            return exp;
        }

        public boolean isAllowFlight() {
            return allowFlight;
        }
    }

}
