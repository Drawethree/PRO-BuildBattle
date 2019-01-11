package me.drawe.buildbattle.managers;

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

    public static final ItemStack posSelectorItem = ItemUtil.create(CompMaterial.STICK, 1, "&ePlot Selector", ItemUtil.makeLore("&aLeft-Click &7block to select &aPosition 1", "&aRight-Click &7block to select &aPostion 2"), true);

    private static ArenaManager ourInstance = new ArenaManager();
    private static HashMap<String, BBArena> arenas = new HashMap<>();
    private static HashMap<Player, Location[]> playerBBPos = new HashMap<>();
    private static int totalPlayedGames = 0;
    private static Inventory editArenasInventory;
    private static Inventory allArenasInventory;
    private static Inventory soloArenasInventory;
    private static Inventory teamArenasInventory;

    private ArenaManager() {
    }

    public static ArenaManager getInstance() {
        return ourInstance;
    }

    private int getArenasAmount(BBGameMode gm) {
        int returnVal = 0;
        for (BBArena a : arenas.values()) {
            if (a.getGameType() == gm) {
                returnVal += 1;
            }
        }
        return returnVal;
    }

    public static HashMap<String, BBArena> getArenas() {
        return arenas;
    }

    public static int getTotalPlayedGames() {
        return totalPlayedGames;
    }

    public static void setTotalPlayedGames(int totalPlayedGames) {
        ArenaManager.totalPlayedGames = totalPlayedGames;
        if (BBSettings.isAutoRestarting()) {
            if (totalPlayedGames == BBSettings.getAutoRestartGamesRequired()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BBSettings.getAutoRestartCommand());
                    }
                }, BBSettings.getEndTime() * 20L);
            }
        }
    }

    public static HashMap<Player, Location[]> getPlayerBBPos() {
        return playerBBPos;
    }

    public Inventory getEditArenasInventory() {
        return editArenasInventory;
    }

    public void createArena(CommandSender sender, String name, String gamemode) {
        if (!existsArena(name)) {
            try {
                BBGameMode bbGameMode = BBGameMode.valueOf(gamemode.toUpperCase());
                arenas.put(name, new BBArena(name, bbGameMode));
                sender.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully created arena §e" + name + " §8[§e" + bbGameMode.name() + "§8]§a!");
                reloadAllArenasInventory();
                reloadArenaEditors();
            } catch (Exception e) {
                sender.sendMessage("§e§lBuildBattle Setup §8| §cInvalid arena type ! Valid types: §esolo, team");
            }
        } else {
            sender.sendMessage(Message.ARENA_EXISTS.getChatMessage().replaceAll("%arena%", name));
        }
    }

    public void removeArena(CommandSender sender, BBArena arena) {
        arena.delete(sender);
        arenas.remove(arena.getName());
        reloadAllArenasInventory();
        reloadArenaEditors();
    }


    public BBPlot getBBPlotFromLocation(Location l) {
        for (BBArena a : arenas.values()) {
            for (BBPlot plot : a.getBuildPlots()) {
                if (plot.isLocationInPlot(l)) {
                    return plot;
                }
            }
        }
        return null;
    }

    public BBPlot getBBPlotFromNearbyLocation(Location l) {
        for (BBArena a : arenas.values()) {
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
        for (BBArena arena : arenas.values()) {
            if (arena.getName().equalsIgnoreCase(answer)) {
                return true;
            }
        }
        return false;
    }

    public BBSign getArenaSign(Sign s) {
        for (BBArena a : arenas.values()) {
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
        for (BBArena a : arenas.values()) {
            if (a.getArenaEdit().getEditInventory().equals(inv)) {
                return a.getArenaEdit();
            }
        }
        return null;
    }

    public BBArena getArenaToAutoJoin(BBGameMode gamemode) {
        if (gamemode == null) {
            for (BBArena a : arenas.values()) {
                if ((a.getBBArenaState() == BBArenaState.LOBBY) && (!a.isFull())) {
                    return a;
                }
            }
        } else {
            for (BBArena a : arenas.values()) {
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
            arenas = new HashMap<>();
            for (String name : BuildBattle.getFileManager().getConfig("arenas.yml").get().getKeys(false)) {
                arenas.put(name, new BBArena(name));
                BuildBattle.info("§aArena §e" + name + " §aloaded !");
            }
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading arenas !");
            e.printStackTrace();
        }
        loadAllArenasInventory();
        loadArenaEditors();
    }


    public void reloadArenaEditors() {
        List<HumanEntity> viewers = editArenasInventory.getViewers();

        loadArenaEditors();

        for (HumanEntity e : new ArrayList<>(viewers)) {
            e.openInventory(editArenasInventory);
        }
    }

    public void loadArenaEditors() {
        editArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(arenas.values().size()), "Arena Editor");
        for (BBArena a : arenas.values()) {
            editArenasInventory.addItem(a.getArenaEdit().getArenaEditItemStack());
        }
    }

    private void reloadAllArenasInventory() {
        List<HumanEntity> viewers = allArenasInventory.getViewers();

        loadAllArenasInventory();

        for (HumanEntity e : new ArrayList<>(viewers)) {
            e.openInventory(allArenasInventory);
        }
    }

    public void refreshArenaItem(BBArena a) {
        for (ItemStack item : allArenasInventory.getContents()) {
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
            for (ItemStack item : soloArenasInventory.getContents()) {
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
            for (ItemStack item : teamArenasInventory.getContents()) {
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
        allArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(arenas.values().size()), Message.GUI_ARENA_LIST_TITLE.getMessage());
        teamArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(getArenasAmount(BBGameMode.TEAM)), Message.GUI_ARENA_LIST_TEAM_TITLE.getMessage());
        soloArenasInventory = Bukkit.createInventory(null, InventoryUtil.getInventorySize(getArenasAmount(BBGameMode.SOLO)), Message.GUI_ARENA_LIST_TEAM_TITLE.getMessage());

        for (BBArena a : arenas.values()) {
            if (a.getGameType() == BBGameMode.SOLO) {
                soloArenasInventory.addItem(a.getArenaStatusItem());
            } else {
                teamArenasInventory.addItem(a.getArenaStatusItem());
            }
            allArenasInventory.addItem(a.getArenaStatusItem());
        }

    }

    public BBArenaEdit getArenaEdit(ItemStack currentItem) {
        for (BBArena a : arenas.values()) {
            if (a.getArenaEdit().getArenaEditItemStack().equals(currentItem)) {
                return a.getArenaEdit();
            }
        }
        return null;
    }

    public void setPos(Player p, Block clickedBlock, int pos) {
        Location[] poses;
        if (playerBBPos.containsKey(p)) {
            poses = playerBBPos.get(p);
        } else {
            poses = new Location[2];
        }
        poses[pos - 1] = clickedBlock.getLocation();
        playerBBPos.put(p, poses);
        p.sendMessage(BBSettings.getPrefix() + "§aPosition §e" + pos + "§a set to §eWorld:" + clickedBlock.getLocation().getWorld().getName() + ", X:" + clickedBlock.getLocation().getBlockX() + ", Y:" + clickedBlock.getLocation().getBlockY() + ", Z:" + clickedBlock.getLocation().getBlockZ());
    }

    public boolean hasSelectionReady(Player p) {
        return (playerBBPos.get(p) != null) && (playerBBPos.get(p)[0] != null) && (playerBBPos.get(p)[1] != null);
    }

    public int getMissingSelection(Player p) {
        if (playerBBPos.get(p) == null) {
            return -1;
        } else if (playerBBPos.get(p)[0] == null) {
            return 1;
        } else if (playerBBPos.get(p)[1] == null) {
            return 2;
        }
        return -1;
    }

    public static Inventory getAllArenasInventory() {
        return allArenasInventory;
    }

    public static Inventory getSoloArenasInventory() {
        return soloArenasInventory;
    }

    public static Inventory getTeamArenasInventory() {
        return teamArenasInventory;
    }
}
