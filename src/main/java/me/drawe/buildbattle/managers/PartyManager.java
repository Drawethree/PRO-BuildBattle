package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PartyManager {
    private static PartyManager ourInstance = new PartyManager();
    private static HashMap<Player, BBParty> invitedPlayers = new HashMap<>();
    private static List<BBParty> parties = new ArrayList<>();

    public static PartyManager getInstance() {
        return ourInstance;
    }

    private PartyManager() {
    }

    public static List<BBParty> getParties() {
        return parties;
    }


    public BBParty createParty(Player creator) {
        if(creator.hasPermission("buildbattlepro.party")) {
            if (getPlayerParty(creator) == null) {
                BBParty party = new BBParty(creator);
                parties.add(party);
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
        if(getInvitedPlayers().containsKey(p)) {
            BBParty party = getInvitedPlayers().get(p);
            if(join) {
                party.joinPlayer(p);
            } else {
                p.sendMessage(Message.PARTY_INVITE_DECLINE.getChatMessage().replaceAll("%creator%", party.getCreator().getName()));
            }
            getInvitedPlayers().remove(p);
        } else {
            p.sendMessage(Message.PARTY_NO_PENDING_INVITES.getChatMessage());
        }

    }

    public void invitePlayer(Player whoInvited, Player p, BBParty party) {
        if(p != null) {
            if(!whoInvited.equals(p)) {
                if (party != null) {
                    if (party.isCreator(whoInvited)) {
                        if (!party.isFull()) {
                            if (!getInvitedPlayers().containsKey(p)) {
                                if (getPlayerParty(p) == null) {
                                    doInviteCommands(p, party);
                                } else {
                                    party.getCreator().sendMessage(Message.PARTY_ALREADY_IN_PARTY.getChatMessage().replaceAll("%player%", p.getName()));
                                }
                            } else {
                                party.getCreator().sendMessage(Message.PARTY_ALREADY_INVITED.getChatMessage().replaceAll("%player%", p.getName()));
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
                        doInviteCommands(p, createdParty);
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
        party.getCreator().sendMessage(Message.PARTY_INVITE.getChatMessage().replaceAll("%player%", p.getName()));
        p.sendMessage(Message.PARTY_YOU_HAVE_BEEN_INVITED.getChatMessage().replaceAll("%creator%", party.getCreator().getName()));
        p.sendMessage(Message.PARTY_ACCEPT_INFO.getChatMessage());
        p.sendMessage(Message.PARTY_DECLINE_INFO.getChatMessage());
        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
            if (getInvitedPlayers().containsKey(p)) {
                getInvitedPlayers().remove(p);
                p.sendMessage(Message.PARTY_INVITE_EXPIRED.getChatMessage().replaceAll("%creator%", party.getCreator().getName()));
            }
        }, 20 * 180L);
    }

    public static HashMap<Player, BBParty> getInvitedPlayers() {
        return invitedPlayers;
    }
    public BBParty getPlayerParty(Player p) {
        for(BBParty party : getParties()) {
            if(party.getPlayers().contains(p)) {
                return party;
            }
        }
        return null;
    }

    public void leaveParty(Player p) {
        BBParty pParty = getPlayerParty(p);
        if(pParty != null) {
            pParty.removePlayer(p);
        } else {
            p.sendMessage(Message.PARTY_NOT_IN_PARTY.getChatMessage());
        }
    }

    public void clearInvitations(BBParty bbParty) {
        Iterator it = getInvitedPlayers().keySet().iterator();
        while(it.hasNext()) {
            Player p = (Player) it.next();
            if(getInvitedPlayers().get(p).equals(bbParty)) {
                it.remove();
            }
        }
    }

    public int getMaxPlayersInParty(Player creator) {
        for(int i = 100;i>GameManager.getPartyMaxPlayers();i--) {
            if(creator.hasPermission("buildbattlepro.party.size." + i)) {
                return i;
            }
        }
        return GameManager.getPartyMaxPlayers();
    }
}
