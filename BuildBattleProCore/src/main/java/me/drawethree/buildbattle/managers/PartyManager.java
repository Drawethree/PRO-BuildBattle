package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;

public class PartyManager {

    private HashMap<Player, BBParty> invitedPlayers = new HashMap<>();
    private HashMap<Player, BBParty> playersInParties = new HashMap<>();

    private BuildBattle plugin;

    public PartyManager(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public HashMap<Player, BBParty> getPlayersInParties() {
        return playersInParties;
    }

    public BBParty createParty(Player creator) {
        if (creator.hasPermission("buildbattlepro.party")) {
            if (getPlayerParty(creator) == null) {
                BBParty party = new BBParty(creator);
                this.playersInParties.put(creator, party);
                return party;
            } else {
                creator.sendMessage(Message.PARTY_CREATE_FAILED.getChatMessage());
            }
        } else {
            creator.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return null;
    }

    public void manageInvite(Player p, boolean join) {
        if (this.invitedPlayers.containsKey(p)) {
            BBParty party = this.invitedPlayers.get(p);
            if (join) {
                party.joinPlayer(p);
                this.playersInParties.put(p, party);
            } else {
                p.sendMessage(Message.PARTY_INVITE_DECLINE.getChatMessage().replace("%creator%", party.getCreator().getName()));
            }
            this.invitedPlayers.remove(p);
        } else {
            p.sendMessage(Message.PARTY_NO_PENDING_INVITES.getChatMessage());
        }
    }

    public void invitePlayer(Player whoInvited, Player p, BBParty party) {
        if (p != null) {
            if (!whoInvited.equals(p)) {
                if (party != null) {
                    if (party.isCreator(whoInvited)) {
                        if (!party.isFull()) {
                            if (!this.invitedPlayers.containsKey(p)) {
                                if (getPlayerParty(p) == null) {
                                    doInviteCommands(p, party);
                                } else {
                                    party.getCreator().sendMessage(Message.PARTY_ALREADY_IN_PARTY.getChatMessage().replace("%player%", p.getName()));
                                }
                            } else {
                                party.getCreator().sendMessage(Message.PARTY_ALREADY_INVITED.getChatMessage().replace("%player%", p.getName()));
                            }
                        } else {
                            whoInvited.sendMessage(Message.PARTY_FULL.getChatMessage());
                        }
                    } else {
                        whoInvited.sendMessage(Message.PARTY_NOT_ALLOWED_TO_INVITE.getChatMessage());
                    }
                } else {
                    BBParty createdParty = createParty(whoInvited);
                    if (createdParty != null) {
                        this.doInviteCommands(p, createdParty);
                    }
                }
            } else {
                whoInvited.sendMessage(Message.PARTY_CANT_INVITE_YOURSELF.getChatMessage());
            }
        } else {
            whoInvited.sendMessage(Message.PARTY_PLAYER_NOT_ONLINE.getChatMessage());
        }
    }

    private void doInviteCommands(Player p, BBParty party) {
        invitedPlayers.put(p, party);
        party.getCreator().sendMessage(Message.PARTY_INVITE.getChatMessage().replace("%player%", p.getName()));

        p.sendMessage(Message.PARTY_YOU_HAVE_BEEN_INVITED.getChatMessage().replace("%creator%", party.getCreator().getName()));
        p.sendMessage(Message.PARTY_ACCEPT_INFO.getChatMessage());
        p.sendMessage(Message.PARTY_DECLINE_INFO.getChatMessage());

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if (invitedPlayers.containsKey(p)) {
                invitedPlayers.remove(p);
                p.sendMessage(Message.PARTY_INVITE_EXPIRED.getChatMessage().replace("%creator%", party.getCreator().getName()));
            }
        }, 20 * 180L);
    }

    public HashMap<Player, BBParty> getInvitedPlayers() {
        return invitedPlayers;
    }

    public BBParty getPlayerParty(Player p) {
        return playersInParties.get(p);
    }

    public void leaveParty(Player p) {
        BBParty pParty = getPlayerParty(p);
        if (pParty != null) {
            pParty.removePlayer(p);
            playersInParties.remove(p);
        } else {
            p.sendMessage(Message.PARTY_NOT_IN_PARTY.getChatMessage());
        }
    }

    public void clearInvitations(BBParty bbParty) {
        Iterator it = invitedPlayers.keySet().iterator();
        while (it.hasNext()) {
            Player p = (Player) it.next();
            if (invitedPlayers.get(p).equals(bbParty)) {
                it.remove();
            }
        }
    }

    public int getMaxPlayersInParty(Player creator) {
        for (int i = 100; i > this.plugin.getSettings().getPartyMaxPlayers(); i--) {
            if (creator.hasPermission("buildbattlepro.party.size." + i)) {
                return i;
            }
        }
        return this.plugin.getSettings().getPartyMaxPlayers();
    }
}
