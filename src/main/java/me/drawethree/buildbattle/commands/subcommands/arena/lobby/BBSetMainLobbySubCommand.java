package me.drawethree.buildbattle.commands.subcommands.arena.lobby;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BBSetMainLobbySubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBSetMainLobbySubCommand(BuildBattle plugin) {
        super("setmainlobby", " setmainlobby §e» §7Set main lobby", "buildbattlepro.create", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                if (p.hasPermission(getPermissionRequired())) {
                    this.plugin.getSettings().setMainLobbyLocation(p);
                    return true;
                } else {
                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " setmainlobby §8| §7Show the main lobby location");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
