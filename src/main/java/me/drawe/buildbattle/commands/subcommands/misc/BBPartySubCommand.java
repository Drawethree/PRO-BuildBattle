package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PartyManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBParty;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBPartySubCommand extends BBSubCommand {

    public BBPartySubCommand() {
        super("party", " party §8» §7Command to manage parties", "buildbattlepro.party",false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BBSettings.isPartiesEnabled()) {
                if (args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    switch (subCommand) {
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
                            if (args.length == 2) {
                                BBParty playerParty = PartyManager.getInstance().getPlayerParty(p);
                                Player target = Bukkit.getPlayer(args[1]);
                                PartyManager.getInstance().invitePlayer(p, target, playerParty);
                                return true;
                            } else {
                                p.sendMessage(Message.PARTY_INVALID_USAGE.getChatMessage());
                            }
                            default:
                                usage(cmd,sender);
                    }
                } else {
                    usage(cmd,sender);
                }
            } else {
                p.sendMessage(Message.PARTIES_NOT_ALLOWED.getChatMessage());
            }
        }
        return false;
    }

    private void usage(BBCommand cmd, CommandSender sender) {
        sender.sendMessage("§cInvalid usage!");
        sender.sendMessage("§e/" + cmd.getName() + " party invite <player> " + "§8» " + "§7Invite player to your party");
        sender.sendMessage("§e/" + cmd.getName() + " party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
        sender.sendMessage("§e/" + cmd.getName() + " party leave " + "§8» " + "§7Leave your current party");
    }
}
