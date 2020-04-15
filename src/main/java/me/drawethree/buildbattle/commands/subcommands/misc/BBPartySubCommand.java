package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBParty;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BBPartySubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBPartySubCommand(BuildBattle plugin) {
        super("party", " party §8» §7Command to manage parties", "buildbattlepro.party",false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (plugin.getSettings().isPartiesEnabled()) {
                if (args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    switch (subCommand) {
                        case "accept":
                            plugin.getPartyManager().manageInvite(p, true);
                            return true;
                        case "decline":
                            plugin.getPartyManager().manageInvite(p, false);
                            return true;
                        case "leave":
                            plugin.getPartyManager().leaveParty(p);
                            return true;
                        case "invite":
                            if (args.length == 2) {
                                BBParty playerParty = plugin.getPartyManager().getPlayerParty(p);
                                Player target = Bukkit.getPlayer(args[1]);
                                plugin.getPartyManager().invitePlayer(p, target, playerParty);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("invite", "accept", "decline", "leave");
        } else if (args.length == 3 && args[1].equalsIgnoreCase("invite")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }
}
