package me.drawe.buildbattle.commands.subcommands.arena.lobby;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}
