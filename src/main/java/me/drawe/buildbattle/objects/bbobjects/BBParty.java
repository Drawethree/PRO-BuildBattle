package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.PartyManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BBParty {

    private Player creator;
    private List<Player> players;

    public BBParty(Player creator) {
        this.creator = creator;
        this.players = new ArrayList<>();
        players.add(creator);
        creator.sendMessage(Message.PARTY_CREATED.getChatMessage());
    }


    public void joinPlayer(Player p) {
        getPlayers().add(p);
        PartyManager.getInvitedPlayers().remove(p);
        p.sendMessage(Message.PARTY_JOIN.getChatMessage().replaceAll("%creator%", getCreator().getName()));
        for(Player p1 : getPlayers()) {
            if(!p1.equals(p)) {
                p1.sendMessage(Message.PARTY_PLAYER_JOINED.getChatMessage().replaceAll("%player%", p.getName()));
            }
        }
    }

    public void removePlayer(Player p) {
        if(!isCreator(p)) {
            getPlayers().remove(p);
            p.sendMessage(Message.PARTY_LEFT.getChatMessage().replaceAll("%creator%", getCreator().getName()));
            for(Player p1 : getPlayers()) {
                if(!p1.equals(p)) {
                    p1.sendMessage(Message.PARTY_PLAYER_LEFT.getChatMessage().replaceAll("%player%", p.getName()));
                }
            }
        } else {
            disbandParty();
        }
    }

    public void disbandParty() {
        for(Player p : getPlayers()) {
            p.sendMessage(Message.PARTY_DISBANDED.getChatMessage());
        }
        getPlayers().clear();
        PartyManager.getParties().remove(this);
        PartyManager.getInstance().clearInvitations(this);
        setCreator(null);
    }

    public void joinGame(BBArena a) {
        if(a.getPlayers().size() + getPlayers().size() <= a.getMaxPlayers()) {
            BBTeam team = a.getFreeBBTeamForParty(this);
            for (Player p : getPlayers()) {
                if (!p.equals(getCreator())) {
                    BBArena pArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (pArena != null) {
                        pArena.removePlayer(p);
                    }
                    a.addPlayer(p);
                }
                if (team != null) {
                    team.joinTeam(p);
                }
            }
        } else {
            getCreator().sendMessage(Message.PARTY_NO_SPACE_FOR_YOUR_PARTY.getChatMessage());
        }
    }

    public boolean isCreator(Player p) {
        return getCreator().equals(p);
    }
    public Player getCreator() {
        return creator;
    }
    public List<Player> getPlayers() {
        return players;
    }

    public void setCreator(Player p) {
        this.creator = creator;
    }

    public boolean isFull() {
        if(getCreator().isOp() || getCreator().hasPermission("buildbattlepro.party.size.*")) return false;
        return getPlayers().size() == PartyManager.getInstance().getMaxPlayersInParty(getCreator());
    }
}
