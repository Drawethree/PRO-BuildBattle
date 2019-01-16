package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;

public class BBVersionSubCommand extends BBSubCommand {

    public BBVersionSubCommand() {
        super("version", "Command to show current plugin's version.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            sender.sendMessage(BBSettings.getPrefix() + " §aYou are running §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
            return true;
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
