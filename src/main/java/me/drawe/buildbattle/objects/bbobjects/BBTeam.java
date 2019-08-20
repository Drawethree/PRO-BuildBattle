package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BBTeam {

    private int maxPlayers;
    private int ID;
    private List<Player> players;
    private BBArena arena;
    private ItemStack statusItemstack;

    public BBTeam(BBArena arena, int ID) {
        this.ID = ID;
        this.arena = arena;
        this.maxPlayers = arena.getTeamSize();
        this.players = new ArrayList<>();
        this.statusItemstack = ItemUtil.create(getStatusMaterial(),1, Message.GUI_TEAMS_ITEMS_DISPLAYNAME.getMessage().replaceAll("%id%", String.valueOf(ID)), ItemUtil.createTeamLore(this), null,null);
    }

    public List<Player> getPlayers() {
        return players;
    }


    public Player getCaptain() {
        return players.get(0);
    }

    public void resetTeam() {
        this.players = new ArrayList<>();
        this.updateStatusItemStack();
    }

    public int getLeftSlots() {
        return maxPlayers - players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    public String getPlayersInCommaSeparatedString() {
        String str = "";
        for (Player p : players) {
            str = str + p.getName() + ",";
        }
        if (str.isEmpty()) {
            return "";
        } else {
            return str.substring(0, str.length() - 1);
        }
    }

    public void joinTeam(Player p) {
        if(!isFull()) {
            BBTeam previousTeam = arena.getPlayerTeam(p);

            if(previousTeam != null) {
                previousTeam.leaveTeam(p);
            }

            players.add(p);
            if (players.size() > 1) {
                p.sendMessage(Message.YOUR_TEAMMATE.getChatMessage().replaceAll("%players%", getPlayersInCommaSeparatedString()));
            }

            updateStatusItemStack();
        } else {
            p.sendMessage(Message.TEAM_IS_FULL.getChatMessage());
        }
    }

    public void leaveTeam(Player p) {
        players.remove(p);
        updateStatusItemStack();
    }

    public BBArena getArena() {
        return arena;
    }

    public ItemStack getStatusItemStack() {
        return statusItemstack;
    }

    public boolean isEmpty() {
        return players.size() == 0;
    }

    public CompMaterial getStatusMaterial() {
        if(isFull()) {
            return CompMaterial.RED_TERRACOTTA;
        } else if(isEmpty()) {
            return CompMaterial.LIME_TERRACOTTA;
        } else {
            return CompMaterial.YELLOW_TERRACOTTA;
        }
    }

    public int getID() {
        return ID;
    }

    private void updateStatusItemStack() {
        this.statusItemstack = ItemUtil.create(getStatusMaterial(),1, Message.GUI_TEAMS_ITEMS_DISPLAYNAME.getMessage().replaceAll("%id%", String.valueOf(ID)), ItemUtil.createTeamLore(this), null,null);
        arena.getTeamsInventory().setItem(ID-1,statusItemstack);
    }


    public String getPlayerName(int index) {
        try {
            return "&a" + players.get(index).getName();
        } catch (Exception e) {
            return Message.GUI_TEAM_ITEMS_NOBODY.getMessage();
        }
    }

    public String getOtherPlayers(Player player) {
        String str = "";
        for(Player p : players) {
            if(!p.equals(player)) {
                str = str + p.getName() + ",";
            }
        }
        if (str.isEmpty()) {
            return Message.SCOREBOARD_NO_TEAMMATES.getMessage();
        } else {
            return str.substring(0, str.length() - 1);
        }
    }
}
