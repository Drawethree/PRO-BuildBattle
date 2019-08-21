package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;

public class BBDeleteSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBDeleteSubCommand(BuildBattle plugin) {
        super("delete", " delete <arena_name> §8» §7Remove Arena", "buildbattlepro.create",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 1) {
                BBArena arena = this.plugin.getArenaManager().getArena(args[0]);
                if (arena != null) {
                    this.plugin.getArenaManager().removeArena(sender, arena);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[0]));
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " delete <arena> §8| §7Deletes arena.");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

}
