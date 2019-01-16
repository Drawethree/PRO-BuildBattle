package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBSetMainLobbySubCommand extends BBSubCommand {

    public BBSetMainLobbySubCommand() {
        super("setmainlobby", "Command to set main lobby.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (p.hasPermission("buildbattlepro.create")) {
                    BBSettings.setMainLobbyLocation(p);
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
}
