package me.drawe.buildbattle.managers;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.BBSign;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaEdit;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.InventoryUtil;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaManager {

    private BuildBattle plugin;

    @Getter
    private final ItemStack posSelectorItem = ItemUtil.create(CompMaterial.STICK, 1, "&ePlot Selector", ItemUtil.makeLore("&aLeft-Click &7block to select &aPosition 1", "&aRight-Click &7block to select &aPostion 2"), true);

    private HashMap<String, BBArena> arenas;
    private HashMap<Player, Location[]> playerBBPos;

    private int totalPlayedGames = 0;

    private Inventory editArenasInventory;
    private Inventory allArenasInventory;
    private Inventory soloArenasInventory;
    private Inventory teamArenasInventory;


    public ArenaManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.playerBBPos = new HashMap<>();
    }

    private int getArenasAmount(BBGameMode gm) {
        int returnVal = 0;
        for (BBArena a : this.arenas.values()) {
            if (a.getGameType() == gm) {
                returnVal += 1;
            }
        }
        return returnVal;
    }

    public HashMap<String, BBArena> getArenas() {
        return this.arenas;
    }

    public int getTotalPlayedGames() {
        return this.totalPlayedGames;
    }

    public void setTotalPlayedGames(int totalPlayedGames) {
        this.totalPlayedGames = totalPlayedGames;
        if (this.plugin.getSettings().isAutoRestarting()) {
            if (totalPlayedGames == this.plugin.getSettings().getAutoRestartGamesRequired()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.plugin.getSettings().getAutoRestartCommand()), this.plugin.getSettings().getEndTime() * 20L);
            }
        }
    }

    public HashMap<Player, Location[]> getPlayerBBPos() {
        return this.playerBBPos;
    }

    public Inventory getEditArenasInventory() {
        return this.editArenasInventory;
    }

    public void createArena(CommandSender sender, String name, String gamemode) {
        if (!existsArena(name)) {
            try {
                BBGameMode bbGameMode = BBGameMode.valueOf(gamemode.toUpperCase());
                this.arenas.put(name, new BBArena(this.plugin,name, bbGameMode));
                sender.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully created arena §e" + name + " §8[§e" + bbGameMode.name() + "§8]§a!");
                this.reloadAllArenasInventory();
                this.reloadArenaEditors();
            } catch (Exception e) {
                sender.sendMessage("§e§lBuildBattle Setup §8| §cInvalid arena type ! Valid types: §esolo, team");
            }
        } else {
            sender.sendMessage(Message.ARENA_EXISTS.getChatMessage().replaceAll("%arena%", name));
        }
    }

    public void removeArena(CommandSender sender, BBArena arena) {
        arena.delete(sender);
        this.arenas.remove(arena.getName());

        this.reloadAllArenasInventory();
        this.reloadArenaEditors();
    }


    public BBPlot getBBPlotFromLocation(Location l) {
        for (BBArena a : this.arenas.values()) {
            for (BBPlot plot : a.getBuildPlots()) {
                if (plot.isLocationInPlot(l)) {
                    return plot;
                }
            }
        }
        return null;
    }

    public BBPlot getBBPlotFromNearbyLocation(Location l) {
        for (BBArena a : this.arenas.values()) {
            for (BBPlot plot : a.getBuildPlots()) {
                if (plot.isInPlotRange(l, 5)) {
                    return plot;
                }
            }
        }
        return null;
    }

    public BBPlot getPlayerPlot(BBArena arena, Player p) {
        for (BBPlot plot : arena.getBuildPlots()) {
            if ((plot.getTeam() != null) && (plot.getTeam().getPlayers().contains(p))) {
                return plot;
            }
        }
        return null;
    }


    public boolean existsArena(String answer) {
        for (BBArena arena : this.arenas.values()) {
            if (arena.getName().equalsIgnoreCase(answer)) {
                return true;
            }
        }
        return false;
    }

    public BBSign getArenaSign(Sign s) {
        for (BBArena a : this.arenas.values()) {
            for (BBSign sign : a.getArenaSigns()) {
                if (sign.getSign().equals(s)) {
                    return sign;
                }
            }
        }
        return null;
    }

    public BBArena getArena(String name) {
        return arenas.get(name);
    }

    public BBArenaEdit getArenaEdit(Inventory inv) {
        for (BBArena a : this.arenas.values()) {
            if (a.getArenaEdit().getEditInventory().equals(inv)) {
                return a.getArenaEdit();
            }
        }
        return null;
    }

    public BBArena getArenaToAutoJoin(BBGameMode gamemode) {
        if (gamemode == null) {
            for (BBArena a : this.arenas.values()) {
                if ((a.getBBArenaState() == BBArenaState.LOBBY) && (!a.isFull())) {
                    return a;
                }
            }
        } else {
            for (BBArena a : this.arenas.values()) {
                if (a.getGameType() == gamemode) {
                    if ((a.getBBArenaState() == BBArenaState.LOBBY) && (!a.isFull())) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    public void loadArenas() {
        try {
            this.arenas = new HashMap<>();
            for (String name : this.plugin.getFileManager().getConfig("arenas.yml").get().getKeys(false)) {
                this.arenas.put(name, new BBArena(this.plugin,name));
                plugin.info("§aArena §e" + name + " §aloaded !");
            }
        } catch (Exception e) {
            plugin.severe("§cAn exception occurred while trying loading arenas !");
            e.printStackTrace();
        }
        this.loadAllArenasInventory();
        this.loadArenaEditors();
    }


    public void reloadArenaEditors() {
        List<HumanEntity> viewers = this.editArenasInventory.getViewers();

        this.loadArenaEditors();

        for (HumanEntity e : new ArrayList<>(viewers)) {
            e.openInventory(this.editArenasInventory);
        }
    }

    public void loadArenaEditors() {
        this.editArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(this.arenas.values().size()), "Arena Editor");
        for (BBArena a : this.arenas.values()) {
            this.editArenasInventory.addItem(a.getArenaEdit().getArenaEditItemStack());
        }
    }

    private void reloadAllArenasInventory() {
        List<HumanEntity> viewers = this.allArenasInventory.getViewers();

        loadAllArenasInventory();

        for (HumanEntity e : new ArrayList<>(viewers)) {
            e.openInventory(this.allArenasInventory);
        }
    }

    public void refreshArenaItem(BBArena a) {
        for (ItemStack item : this.allArenasInventory.getContents()) {
            if (item != null && item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName().equals(a.getName())) {
                    ItemStack replacement = a.getArenaStatusItem();
                    item.setItemMeta(replacement.getItemMeta());
                    item.setData(replacement.getData());
                    item.setDurability(replacement.getDurability());
                }
            }
        }
        if (a.getGameType() == BBGameMode.SOLO) {
            for (ItemStack item : this.soloArenasInventory.getContents()) {
                if (item != null && item.hasItemMeta()) {
                    if (item.getItemMeta().getDisplayName().equals(a.getName())) {
                        ItemStack replacement = a.getArenaStatusItem();
                        item.setItemMeta(replacement.getItemMeta());
                        item.setData(replacement.getData());
                        item.setDurability(replacement.getDurability());
                    }
                }
            }
        } else {
            for (ItemStack item : this.teamArenasInventory.getContents()) {
                if (item != null && item.hasItemMeta()) {
                    if (item.getItemMeta().getDisplayName().equals(a.getName())) {
                        ItemStack replacement = a.getArenaStatusItem();
                        item.setItemMeta(replacement.getItemMeta());
                        item.setData(replacement.getData());
                        item.setDurability(replacement.getDurability());
                    }
                }
            }
        }
    }

    private void loadAllArenasInventory() {
        this.allArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(arenas.values().size()), Message.GUI_ARENA_LIST_TITLE.getMessage());
        this.teamArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(getArenasAmount(BBGameMode.TEAM)), Message.GUI_ARENA_LIST_TEAM_TITLE.getMessage());
        this.soloArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(getArenasAmount(BBGameMode.SOLO)), Message.GUI_ARENA_LIST_SOLO_TITLE.getMessage());

        for (BBArena a : this.arenas.values()) {
            if (a.getGameType() == BBGameMode.SOLO) {
                this.soloArenasInventory.addItem(a.getArenaStatusItem());
            } else {
                this.teamArenasInventory.addItem(a.getArenaStatusItem());
            }
            this.allArenasInventory.addItem(a.getArenaStatusItem());
        }

    }

    public BBArenaEdit getArenaEdit(ItemStack currentItem) {
        for (BBArena a : this.arenas.values()) {
            if (a.getArenaEdit().getArenaEditItemStack().equals(currentItem)) {
                return a.getArenaEdit();
            }
        }
        return null;
    }

    public void setPos(Player p, Block clickedBlock, int pos) {
        Location[] poses;
        if (this.playerBBPos.containsKey(p)) {
            poses = this.playerBBPos.get(p);
        } else {
            poses = new Location[2];
        }
        poses[pos - 1] = clickedBlock.getLocation();
        this.playerBBPos.put(p, poses);
        p.sendMessage(this.plugin.getSettings().getPrefix() + "§aPosition §e" + pos + "§a set to §eWorld:" + clickedBlock.getLocation().getWorld().getName() + ", X:" + clickedBlock.getLocation().getBlockX() + ", Y:" + clickedBlock.getLocation().getBlockY() + ", Z:" + clickedBlock.getLocation().getBlockZ());
    }

    public boolean hasSelectionReady(Player p) {
        return (this.playerBBPos.get(p) != null) && (this.playerBBPos.get(p)[0] != null) && (this.playerBBPos.get(p)[1] != null);
    }

    public int getMissingSelection(Player p) {
        if (this.playerBBPos.get(p) == null) {
            return -1;
        } else if (this.playerBBPos.get(p)[0] == null) {
            return 1;
        } else if (this.playerBBPos.get(p)[1] == null) {
            return 2;
        }
        return -1;
    }

    public Inventory getAllArenasInventory() {
        return allArenasInventory;
    }

    public  Inventory getSoloArenasInventory() {
        return soloArenasInventory;
    }

    public Inventory getTeamArenasInventory() {
        return teamArenasInventory;
    }
}
