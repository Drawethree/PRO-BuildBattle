package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;

public class BBReloadSubCommand extends BBSubCommand {

    public BBReloadSubCommand() {
        super("reload", "Command to reload plugin.","buildbattlepro.admin", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 1) {
                BuildBattle.getInstance().reloadPlugin();
                sender.sendMessage(BBSettings.getPrefix() + " §aPlugin reloaded !");
                return true;
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() +" reload §8| §7Reloads plugin");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
