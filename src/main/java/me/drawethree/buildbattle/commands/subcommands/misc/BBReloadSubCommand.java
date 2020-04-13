package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;

public class BBReloadSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBReloadSubCommand(BuildBattle plugin) {
        super("reload", " reload §8» §7Reload plugin","buildbattlepro.admin", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                plugin.reloadPlugin();
                sender.sendMessage(plugin.getSettings().getPrefix() + " §aPlugin reloaded !");
                return true;
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " reload §8| §7Reloads plugin");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
