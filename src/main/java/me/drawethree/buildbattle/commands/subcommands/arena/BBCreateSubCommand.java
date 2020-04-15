package me.drawethree.buildbattle.commands.subcommands.arena;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 3) {
            return Arrays.stream(BBGameMode.values()).map(BBGameMode::getName).collect(Collectors.toList());
        }
        return null;
    }
}
