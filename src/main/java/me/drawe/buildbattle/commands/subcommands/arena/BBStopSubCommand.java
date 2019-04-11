package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBStopSubCommand extends BBSubCommand {

    public BBStopSubCommand() {
        super("stop", " stop [<arena>] §8» §7Stop Arena", "buildbattlepro.stop",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStop(sender);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 1) {
                BBArena arena = ArenaManager.getInstance().getArena(args[0]);
                if (arena != null) {
                    arena.forceStop(sender);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " stop §8| §7Force stop arena you are in");
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " stop <arena> §8| §7Force stop specified arena");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
