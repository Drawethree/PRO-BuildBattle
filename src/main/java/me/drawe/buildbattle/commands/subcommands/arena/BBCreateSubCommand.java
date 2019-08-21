package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBCreateSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBCreateSubCommand(BuildBattle plugin) {
        super("create", " create <arena_name> <solo/team> §8» §7Create Arena", "buildbattlepro.create",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                if (args.length == 2) {
                    String arenaName = args[0];
                    String gameMode = args[1];
                    this.plugin.getArenaManager().createArena(sender, arenaName, gameMode);
                    return true;
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " create <name> <solo/team> §8| §7Create an buildbattle arena");
                }
            }
        }
        return false;
    }

}
