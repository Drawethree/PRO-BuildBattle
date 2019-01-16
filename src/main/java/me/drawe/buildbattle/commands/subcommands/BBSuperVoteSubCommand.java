package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.SuperVoteManager;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class BBSuperVoteSubCommand extends BBSubCommand {

    public BBSuperVoteSubCommand() {
        super("supervote", "Command to manage super votes.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        // /bb supervote <give/take> <player> <amount>
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 3) {
                try {
                    String action = args[0].toLowerCase();
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    switch (action) {
                        case "give":
                            if (SuperVoteManager.getInstance().giveSuperVote(player, amount)) {
                                sender.sendMessage(BBSettings.getPrefix() + " §aYou have successfully given §e" + amount + " §asupervote(s) to player §e" + player.getName() + "§a!");
                                return true;
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        case "take":
                            if (SuperVoteManager.getInstance().takeSuperVote(player, amount)) {
                                sender.sendMessage(BBSettings.getPrefix() + " §aYou have successfully taken §e" + amount + " §asupervote(s) from player §e" + player.getName() + "§a!");
                                return true;
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        default:
                            sender.sendMessage("§e" + args[0] + " §cis not a valid action ! Please use: §7[§egive,take§7]");
                            break;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§e" + args[2] + " §cis not a valid amount !");
                }
            } else {
                sender.sendMessage("§cUsage >> §e/" + cmd.getName() + " supervote <give/take> <player> <amount> §8| §7Give/take supervote(s) from/to player");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
