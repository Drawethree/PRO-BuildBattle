package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBSetLobbySubCommand extends BBSubCommand {

    public BBSetLobbySubCommand() {
        super("setlobby", "Command to set arena lobby.","buildbattlepro.create", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[0]);
                    Location playerLoc = p.getLocation();
                    if (arena != null) {
                        arena.setLobbyLocation(playerLoc);
                        p.sendMessage("§e§lBuildBattle Setup §8| §aLobby Location for arena §e" + arena.getName() + " §aset !");
                        return true;
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[0]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " setlobby <arena> §8| §7Set Lobby location for arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
