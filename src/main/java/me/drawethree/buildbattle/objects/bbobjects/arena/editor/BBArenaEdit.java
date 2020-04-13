package me.drawethree.buildbattle.objects.bbobjects.arena.editor;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.arena.editor.options.*;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BBArenaEdit {

    private static final int GAMEMODE_OPTION_SLOT = 2;
    private static final int GAME_TIME_OPTION_SLOT = 3;
    private static final int MIN_PLAYERS_OPTION_SLOT = 4;
    private static final int TEAM_SIZE_OPTION_SLOT = 5;


    private BuildBattle plugin;
    private BBArena arena;
    private Inventory editInventory;
    private ItemStack arenaEditItemStack;
    private HashMap<Integer, BBArenaEditOption> editOptions;

    public BBArenaEdit(BuildBattle plugin, BBArena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.editInventory = Bukkit.createInventory(null, 9, "Editing Arena: " + arena.getName());
        this.editOptions = new HashMap<>();
        this.editOptions.put(GAMEMODE_OPTION_SLOT, new GameModeOption(arena.getGameType()));
        this.editOptions.put(GAME_TIME_OPTION_SLOT, new GameTimeOption(arena.getGameTime()));
        this.editOptions.put(MIN_PLAYERS_OPTION_SLOT, new MinPlayersOption(arena.getMinPlayers()));
        this.editOptions.put(TEAM_SIZE_OPTION_SLOT, new TeamSizeOption(arena.getTeamSize()));
        this.arenaEditItemStack = ItemUtil.create(CompMaterial.BRICKS, 1, "§e" + arena.getName(), ItemUtil.makeLore("&7Click to edit arena " + arena.getName()));
        this.loadInventory();
    }

    private void loadInventory() {
        this.editInventory.setItem(0, plugin.getOptionsManager().getBackItem());
        this.editInventory.setItem(6, plugin.getOptionsManager().getDeleteArenaItem());
        this.editInventory.setItem(8, plugin.getOptionsManager().getSaveItem());

        for (int i : editOptions.keySet()) {
            this.editInventory.setItem(i, editOptions.get(i).getItem());
        }
    }

    public void refreshGUI() {
        for (int i : editOptions.keySet()) {
            this.editInventory.setItem(i, editOptions.get(i).getItem());
        }
    }

    public void saveOptions() {
        arena.setMinPlayers((Integer) getOption(MIN_PLAYERS_OPTION_SLOT).getValue());
        arena.setGameType((BBGameMode) getOption(GAMEMODE_OPTION_SLOT).getValue());
        arena.setTeamSize((Integer) getOption(TEAM_SIZE_OPTION_SLOT).getValue());
        arena.setGameTime((Integer) getOption(GAME_TIME_OPTION_SLOT).getValue());
        arena.saveIntoConfig();
    }

    public BBArena getArena() {
        return arena;
    }

    public Inventory getEditInventory() {
        return editInventory;
    }

    public ItemStack getArenaEditItemStack() {
        return arenaEditItemStack;
    }

    public BBArenaEditOption getOption(int slot) {
        return this.editOptions.get(slot);
    }
}
