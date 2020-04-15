package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BBVersionSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBVersionSubCommand(BuildBattle plugin) {
        super("version", " version §8» §7Command to show current plugin's version.", "buildbattlepro.admin",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            sender.sendMessage(plugin.getSettings().getPrefix() + " §aYou are running §e" + plugin.getDescription().getName() + " v." + plugin.getDescription().getVersion() + "§a by §eDrawethree.");
            return true;
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
