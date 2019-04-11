package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBStartSubCommand extends BBSubCommand {

    public BBStartSubCommand() {
        super("start", " start [<arena>] [<theme>] " + "§8» " + "§7Start Arena", "buildbattlepro.start",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, false);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 1) {
                BBArena arena = ArenaManager.getInstance().getArena(args[0]);
                if (arena != null) {
                    arena.forceStart(sender, false);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[0]);
                String theme = args[1];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, false);
                        return true;
                    } else {
                        sender.sendMessage(BBSettings.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " start  §8| §7Start arena you are in");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " start <arena> §8| §7Start specific arena");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " start <arena> <theme> §8| §7Start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
