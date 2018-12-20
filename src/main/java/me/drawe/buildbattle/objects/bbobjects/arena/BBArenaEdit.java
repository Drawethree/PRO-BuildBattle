package me.drawe.buildbattle.objects.bbobjects.arena;

import me.drawe.buildbattle.managers.OptionsManager;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.utils.ItemCreator;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BBArenaEdit {

    private BBArena arena;
    private Inventory editInventory;
    private ItemStack arenaEditItemStack;
    private ItemStack minPlayersItem;
    private ItemStack gameModeItem;
    private ItemStack teamSizeItem;
    private ItemStack gameTimeItem;
    private int minPlayers;
    private BBGameMode gameMode;
    private int teamSize;
    private int gameTime;

    public BBArenaEdit(BBArena arena) {
        this.arena = arena;
        this.editInventory = Bukkit.createInventory(null, 9, "Editing Arena: " + arena.getName());
        this.minPlayers = arena.getMinPlayers();
        this.gameMode = arena.getGameType();
        this.teamSize = arena.getTeamSize();
        this.gameTime = arena.getGameTime();
        this.arenaEditItemStack = ItemCreator.create(CompMaterial.BRICKS, 1, "§e" + arena.getName(), ItemCreator.makeLore("&7Click to edit arena " + arena.getName()));
        this.minPlayersItem = ItemCreator.create(CompMaterial.PAPER, 1, "Min Players: §e" + minPlayers, ItemCreator.makeLore("", "&8(Click to adjust)", "&7< &c-1   &a+1 &7>"));
        this.gameModeItem = ItemCreator.create(CompMaterial.PLAYER_HEAD, 1, "Game Mode: §e" + gameMode.name(), ItemCreator.makeLore("", "&8(Click to change)"));
        this.teamSizeItem = ItemCreator.create(CompMaterial.PAPER, 1, "Team Size: §e" + teamSize, ItemCreator.makeLore("", "&8(Click to adjust)", "&7< &c-1   &a+1 &7>"));
        this.gameTimeItem = ItemCreator.create(CompMaterial.PAPER, 1, "Game Time: §e" + gameTime + "s", ItemCreator.makeLore("", "&8(Click to adjust)", "&7< &c-10s   &a+10s &7>"));
        this.loadInventory();
    }

    private void loadInventory() {
        this.editInventory.setItem(0, OptionsManager.getBackItem());
        this.editInventory.setItem(2, gameModeItem);
        this.editInventory.setItem(3, gameTimeItem);
        this.editInventory.setItem(4, minPlayersItem);
        this.editInventory.setItem(5, teamSizeItem);
        this.editInventory.setItem(6, OptionsManager.getDeleteArenaItem());
        this.editInventory.setItem(8, OptionsManager.getSaveItem());
    }

    public boolean editMinPlayers(ClickType click) {
        if(click == ClickType.RIGHT) {
            minPlayers += 1;
        } else if(click == ClickType.LEFT || click == ClickType.DOUBLE_CLICK) {
            if(minPlayers - 1 != 0) {
                minPlayers -= 1;
            } else {
                return false;
            }
        } else {
            return false;
        }
        ItemMeta meta = minPlayersItem.getItemMeta();
        meta.setDisplayName("Min Players: §e" + minPlayers);
        minPlayersItem.setItemMeta(meta);
        editInventory.setItem(4, minPlayersItem);
        return true;
    }

    public boolean editTeamSize(ClickType click) {
        if(click == ClickType.RIGHT) {
            teamSize += 1;
        } else if(click == ClickType.LEFT || click == ClickType.DOUBLE_CLICK) {
            if(teamSize - 1 != 0) {
                teamSize -= 1;
            } else {
                return false;
            }
        } else {
            return false;
        }
        ItemMeta meta = teamSizeItem.getItemMeta();
        meta.setDisplayName("Team Size: §e" + teamSize);
        teamSizeItem.setItemMeta(meta);
        editInventory.setItem(5, teamSizeItem);
        return true;
    }

    public boolean editGameTime(ClickType click) {
        if(click == ClickType.RIGHT) {
            gameTime += 10;
        } else if(click == ClickType.LEFT || click == ClickType.DOUBLE_CLICK) {
            if(gameTime - 10 != 0) {
                gameTime -= 10;
            } else {
                return false;
            }
        } else {
            return false;
        }
        ItemMeta meta = gameTimeItem.getItemMeta();
        meta.setDisplayName("Game Time: §e" + gameTime + "s");
        gameTimeItem.setItemMeta(meta);
        editInventory.setItem(3, gameTimeItem);
        return true;
    }

    public boolean editGameMode() {
        if(gameMode == BBGameMode.SOLO) {
            gameMode = BBGameMode.TEAM;
        } else {
            gameMode = BBGameMode.SOLO;
        }
        ItemMeta meta = gameModeItem.getItemMeta();
        meta.setDisplayName("Game Mode: §e" + gameMode.name());
        gameModeItem.setItemMeta(meta);
        editInventory.setItem(2, gameModeItem);
        return true;
    }

    public void saveOptions() {
        arena.setMinPlayers(minPlayers);
        arena.setGameType(gameMode);
        arena.setTeamSize(teamSize);
        arena.setGameTime(gameTime);
        arena.saveIntoConfig();
    }

    public BBArena getArena() {
        return arena;
    }

    public Inventory getEditInventory() {
        return editInventory;
    }

    public ItemStack getMinPlayersItem() {
        return minPlayersItem;
    }

    public ItemStack getGameModeItem() {
        return gameModeItem;
    }

    public ItemStack getTeamSizeItem() {
        return teamSizeItem;
    }

    public ItemStack getGameTimeItem() {
        return gameTimeItem;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public BBGameMode getGameMode() {
        return gameMode;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getGameTime() {
        return gameTime;
    }

    public ItemStack getArenaEditItemStack() {
        return arenaEditItemStack;
    }
}
