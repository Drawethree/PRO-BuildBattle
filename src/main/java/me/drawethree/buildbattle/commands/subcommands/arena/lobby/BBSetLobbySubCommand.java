package me.drawethree.buildbattle.commands.subcommands.arena.lobby;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BBSetLobbySubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBSetLobbySubCommand(BuildBattle plugin) {
        super("setlobby", " setlobby <arena> §8» §7Set lobby for Arena", "buildbattlepro.create", true);

        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    BBArena arena = this.plugin.getArenaManager().getArena(args[0]);
                    Location playerLoc = p.getLocation();
                    if (arena != null) {
                        arena.setLobbyLocation(playerLoc);
                        p.sendMessage("§e§lBuildBattle Setup §8| §aLobby Location for arena §e" + arena.getName() + " §aset !");
                        return true;
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replace("%arena%", args[0]));
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(this.plugin.getArenaManager().getArenas().keySet());
        }
        return null;
    }
}
