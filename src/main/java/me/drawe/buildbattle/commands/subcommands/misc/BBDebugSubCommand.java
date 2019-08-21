package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
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
            sender.sendMessage(plugin.getSettings().getPrefix() + "§aDebug mode >> §e" + plugin.enableDebugMode());
            return true;
        }
        return false;
    }
}
