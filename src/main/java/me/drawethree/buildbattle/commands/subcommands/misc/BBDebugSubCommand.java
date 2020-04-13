package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.command.CommandSender;

public class BBDebugSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBDebugSubCommand(BuildBattle plugin) {
        super("debug", " debug §8» §7Turn on/off debug messages", "", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.isOp()) {
            sender.sendMessage(plugin.getSettings().getPrefix() + "§aDebug mode >> §e" + BooleanUtils.toStringOnOff(plugin.enableDebugMode()));
            return true;
        }
        return false;
    }
}
