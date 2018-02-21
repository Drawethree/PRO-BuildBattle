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


    public void createParty(Player creator) {
        if(creator.hasPermission("buildbattlepro.party")) {
            if (getPlayerParty(creator) == null) {
                BBParty party = new BBParty(creator);
                parties.add(party);
            } else {
                creator.sendMessage(Message.PARTY_CREATE_FAILED.getChatMessage());
            }
        } else {
            creator.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
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
            if(party != null) {
                if(party.isCreator(whoInvited)) {
                    if(!party.isFull()) {
                        if (!getInvitedPlayers().containsKey(p)) {
                            if (getPlayerParty(p) == null) {
                                invitedPlayers.put(p, party);
                                whoInvited.sendMessage(Message.PARTY_INVITE.getChatMessage().replaceAll("%player%", p.getName()));
                                p.sendMessage(Message.PARTY_YOU_HAVE_BEEN_INVITED.getChatMessage().replaceAll("%creator%", party.getCreator().getName()));
                                p.sendMessage(Message.PARTY_ACCEPT_INFO.getChatMessage());
                                p.sendMessage(Message.PARTY_DECLINE_INFO.getChatMessage());
                                Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getInvitedPlayers().containsKey(p)) {
                                            getInvitedPlayers().remove(p);
                                            p.sendMessage(Message.PARTY_INVITE_EXPIRED.getChatMessage().replaceAll("%creator%", party.getCreator().getName()));
                                        }
                                    }
                                }, 20 * 180L);
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
                whoInvited.sendMessage(Message.PARTY_NOT_IN_PARTY.getChatMessage());
            }
        } else {
            whoInvited.sendMessage(Message.PARTY_PLAYER_NOT_ONLINE.getChatMessage());
        }
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
}
