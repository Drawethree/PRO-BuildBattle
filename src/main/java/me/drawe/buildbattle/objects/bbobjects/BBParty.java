package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BBParty {

    private Player creator;
    private List<Player> players;

    public BBParty(Player creator) {
        this.creator = creator;
        this.players = new ArrayList<>();
        this.players.add(creator);
        creator.sendMessage(Message.PARTY_CREATED.getChatMessage());
    }


    public void joinPlayer(Player p) {
        p.sendMessage(Message.PARTY_JOIN.getChatMessage().replace("%creator%", getCreator().getName()));
        for (Player p1 : players) {
            p1.sendMessage(Message.PARTY_PLAYER_JOINED.getChatMessage().replace("%player%", p.getName()));
        }
        players.add(p);
        BuildBattle.getInstance().getPartyManager().getInvitedPlayers().remove(p);
    }

    public void removePlayer(Player p) {
        if (!isCreator(p)) {
            players.remove(p);
            p.sendMessage(Message.PARTY_LEFT.getChatMessage().replace("%creator%", getCreator().getName()));
            for (Player p1 : players) {
                p1.sendMessage(Message.PARTY_PLAYER_LEFT.getChatMessage().replace("%player%", p.getName()));
            }
        } else {
            this.disbandParty();
        }
    }

    private void disbandParty() {
        for (Player p : players) {
            BuildBattle.getInstance().getPartyManager().getPlayersInParties().remove(p);
            p.sendMessage(Message.PARTY_DISBANDED.getChatMessage());
        }
        players.clear();
        BuildBattle.getInstance().getPartyManager().clearInvitations(this);
        this.creator = null;
    }

    public void joinGame(BBArena a) {
        if (a.getPlayers().size() + players.size() <= a.getMaxPlayers()) {
            BBTeam team = a.getFreeBBTeamForParty(this);
            for (Player p : players) {
                if (!p.equals(creator)) {
                    BBArena pArena = BuildBattle.getInstance().getPlayerManager().getPlayerArena(p);
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
            creator.sendMessage(Message.PARTY_NO_SPACE_FOR_YOUR_PARTY.getChatMessage());
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

    public boolean isFull() {
        if (creator.isOp() || creator.hasPermission("buildbattlepro.party.size.*")) return false;
        return getPlayers().size() == BuildBattle.getInstance().getPartyManager().getMaxPlayersInParty(getCreator());
    }
}
