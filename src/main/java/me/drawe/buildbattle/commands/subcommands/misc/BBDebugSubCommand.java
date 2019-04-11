package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.BBSettings;
import org.bukkit.command.CommandSender;

public class BBDebugSubCommand extends BBSubCommand {

    public BBDebugSubCommand() {
        super("debug", " debug §8» Turn on/off debug messages", "", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.isOp()) {
            sender.sendMessage(BBSettings.getPrefix() + "§aDebug mode >> §e" + BuildBattle.enableDebugMode());
            return true;
        }
        return false;
    }
}
