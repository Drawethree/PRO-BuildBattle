package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.ItemCreator;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BBTeam {

    private int maxPlayers;
    private int ID;
    private List<Player> players;
    private BBPlot assignedPlot;
    private BBArena arena;
    private ItemStack statusItemstack;

    public BBTeam(BBArena arena, int ID) {
        this.ID = ID;
        this.arena = arena;
        this.maxPlayers = arena.getTeamSize();
        this.players = new ArrayList<>();
        this.assignedPlot = null;
        this.statusItemstack = ItemCreator.create(getStatusMaterial(),1, Message.GUI_TEAMS_ITEMS_DISPLAYNAME.getMessage().replaceAll("%id%", String.valueOf(getID())), ItemCreator.createTeamLore(this), null,null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public BBPlot getAssignedPlot() {
        return assignedPlot;
    }

    public Player getCaptain() {
        return getPlayers().get(0);
    }

    public void resetTeam() {
        setPlayers(new ArrayList<>());
        assignPlot(null);
        updateStatusItemStack();
    }

    public int getLeftSlots() {
        return getMaxPlayers() - getPlayers().size();
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void assignPlot(BBPlot plot) {
        this.assignedPlot = plot;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return getPlayers().size() == getMaxPlayers();
    }

    public String getPlayersInCommaSeparatedString() {
        String str = "";
        for (Player p : getPlayers()) {
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
            BBTeam previousTeam = getArena().getPlayerTeam(p);
            if(previousTeam != null) {
                previousTeam.leaveTeam(p);
            }
            getPlayers().add(p);
            if (getPlayers().size() > 1) {
                p.sendMessage(Message.YOUR_TEAMMATE.getChatMessage().replaceAll("%players%", getPlayersInCommaSeparatedString()));
            }
            updateStatusItemStack();
        } else {
            p.sendMessage(Message.TEAM_IS_FULL.getChatMessage());
        }
    }

    public void leaveTeam(Player p) {
        getPlayers().remove(p);
        updateStatusItemStack();
    }

    public BBArena getArena() {
        return arena;
    }

    public ItemStack getStatusItemStack() {
        return statusItemstack;
    }

    public boolean isEmpty() {
        return getPlayers().size() == 0;
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
        ItemMeta meta = getStatusItemStack().getItemMeta();
        meta.setLore(ItemCreator.convertLore(ItemCreator.createTeamLore(this)));
        getStatusItemStack().setItemMeta(meta);
        getStatusItemStack().setType(getStatusMaterial().getMaterial());
        getArena().getTeamsInventory().setItem(getID()-1, getStatusItemStack());
    }


    public String getPlayerName(int index) {
        try {
            return "&a" + getPlayers().get(index).getName();
        } catch (Exception e) {
            return Message.GUI_TEAM_ITEMS_NOBODY.getMessage();
        }
    }

    public String getOtherPlayers(Player player) {
        String str = "";
        for(Player p : getPlayers()) {
            if(!p.equals(player)) {
                str = str + p.getName() + ",";
            }
        }
        if (str.isEmpty()) {
            return Message.GUI_TEAM_ITEMS_NOBODY.getMessage();
        } else {
            return str.substring(0, str.length() - 1);
        }
    }
}
