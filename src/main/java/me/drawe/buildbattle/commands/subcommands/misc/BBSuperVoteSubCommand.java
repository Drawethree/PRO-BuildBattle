package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class BBSuperVoteSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBSuperVoteSubCommand(BuildBattle plugin) {
        super("supervote", " supervote <give/take> <player> <amount> §8» §7Give/take supervotes from player", "buildbattlepro.admin",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        // /bb supervote <give/take> <player> <amount>
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 3) {
                try {
                    String action = args[0].toLowerCase();
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    switch (action) {
                        case "give":
                            if (plugin.getSuperVoteManager().giveSuperVote(player, amount)) {
                                sender.sendMessage(plugin.getSettings().getPrefix() + " §aYou have successfully given §e" + amount + " §asupervote(s) to player §e" + player.getName() + "§a!");
                                return true;
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        case "take":
                            if (plugin.getSuperVoteManager().takeSuperVote(player, amount)) {
                                sender.sendMessage(plugin.getSettings().getPrefix() + " §aYou have successfully taken §e" + amount + " §asupervote(s) from player §e" + player.getName() + "§a!");
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
