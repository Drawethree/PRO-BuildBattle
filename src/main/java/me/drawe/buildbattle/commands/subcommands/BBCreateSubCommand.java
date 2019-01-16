package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBCreateSubCommand extends BBSubCommand{

    public BBCreateSubCommand() {
        super("create", "Command to create an arena.", "buildbattlepro.create",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                if (args.length == 2) {
                    String arenaName = args[0];
                    String gameMode = args[1];
                    ArenaManager.getInstance().createArena(sender, arenaName, gameMode);
                    return true;
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " create <name> <solo/team> §8| §7Create an buildbattle arena");
                }
            }
        }
        return false;
    }

}
