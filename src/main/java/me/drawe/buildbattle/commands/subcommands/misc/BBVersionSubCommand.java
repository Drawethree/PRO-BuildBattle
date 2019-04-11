package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;

public class BBVersionSubCommand extends BBSubCommand {

    public BBVersionSubCommand() {
        super("version", " version §8» §7Command to show current plugin's version.", "buildbattlepro.admin",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            sender.sendMessage(BBSettings.getPrefix() + " §aYou are running §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
            return true;
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
