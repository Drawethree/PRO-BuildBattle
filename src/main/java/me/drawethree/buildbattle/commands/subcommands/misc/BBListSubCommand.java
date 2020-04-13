package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBListSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBListSubCommand(BuildBattle plugin) {
        super("list", " list §8» §7Open GUI with all arenas", "buildbattlepro.join",false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.openInventory(plugin.getArenaManager().getAllArenasInventory());
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("solo")) {
                    p.openInventory(plugin.getArenaManager().getSoloArenasInventory());
                    return true;
                } else if (args[0].equalsIgnoreCase("team")) {
                    p.openInventory(plugin.getArenaManager().getTeamArenasInventory());
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
