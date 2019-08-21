package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBForceStartSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBForceStartSubCommand(BuildBattle plugin) {
        super("forcestart", " forcestart [<arena>] [<theme>] §8» §7Force start Arena", "buildbattlepro.admin",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, true);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 1) {
                BBArena arena = this.plugin.getArenaManager().getArena(args[0]);
                if (arena != null) {
                    arena.forceStart(sender, true);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 2) {
                BBArena arena = this.plugin.getArenaManager().getArena(args[0]);
                String theme = args[1];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, true);
                        return true;
                    } else {
                        sender.sendMessage(this.plugin.getSettings().getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " forcestart  §8| §7Force start arena you are in");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " forcestart <arena> §8| §7Force start specific arena");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " forcestart <arena> <theme> §8| §7Force start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
