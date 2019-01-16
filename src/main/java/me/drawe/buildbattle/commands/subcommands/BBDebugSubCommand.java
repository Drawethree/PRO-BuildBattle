package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import org.bukkit.command.CommandSender;

public class BBDebugSubCommand extends BBSubCommand {

    public BBDebugSubCommand() {
        super("debug", "Turn on/off debug mode.", "", true);
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
