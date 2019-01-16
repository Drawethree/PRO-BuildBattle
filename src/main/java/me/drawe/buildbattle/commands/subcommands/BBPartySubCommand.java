package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PartyManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBParty;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBPartySubCommand extends BBSubCommand {

    public BBPartySubCommand() {
        super("party", "Command to manage party.", false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BBSettings.isPartiesEnabled()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        /*case "create":
                            PartyManager.getInstance().createParty(p);
                            break;
                            */
                        case "accept":
                            PartyManager.getInstance().manageInvite(p, true);
                            return true;
                        case "decline":
                            PartyManager.getInstance().manageInvite(p, false);
                            return true;
                        case "leave":
                            PartyManager.getInstance().leaveParty(p);
                            return true;
                        case "invite":
                            if (args.length == 3) {
                                BBParty playerParty = PartyManager.getInstance().getPlayerParty(p);
                                Player target = Bukkit.getPlayer(args[2]);
                                PartyManager.getInstance().invitePlayer(p, target, playerParty);
                                return true;
                            } else {
                                p.sendMessage(Message.PARTY_INVALID_USAGE.getChatMessage());
                            }
                    }
                } else {
                    p.sendMessage("§cInvalid usage!");
                    //p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
                    p.sendMessage("§e/" + cmd.getName() + " party invite <player> " + "§8» " + "§7Invite player to your party");
                    p.sendMessage("§e/" + cmd.getName() + " party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
                    p.sendMessage("§e/" + cmd.getName() + " party leave " + "§8» " + "§7Leave your current party");
                }
            } else {
                p.sendMessage(Message.PARTIES_NOT_ALLOWED.getChatMessage());
            }
        }
        return false;
    }
}
