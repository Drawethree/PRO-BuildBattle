package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBListSubCommand extends BBSubCommand {

    public BBListSubCommand() {
        super("list", "Command to list arenas in GUI.", "buildbattlepro.join",false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.openInventory(ArenaManager.getAllArenasInventory());
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("solo")) {
                    p.openInventory(ArenaManager.getSoloArenasInventory());
                    return true;
                } else if (args[0].equalsIgnoreCase("team")) {
                    p.openInventory(ArenaManager.getTeamArenasInventory());
                    return true;
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " list <solo/team> §8| §7Show all team/solo arenas and their status");
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " list §8| §7Show all arenas and their status");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " list <solo/team> §8| §7Show all team/solo arenas and their status");
            }
        }
        return false;
    }
}
