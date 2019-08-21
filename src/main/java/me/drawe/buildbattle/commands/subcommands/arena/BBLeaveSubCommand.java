package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBLeaveSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBLeaveSubCommand(BuildBattle plugin) {
        super("leave", " leave §8» §7Leave Arena", "buildbattlepro.join", false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
            if (arena != null) {
                arena.removePlayer(p);
                return true;
            } else {
                p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
            }
        }
        return false;
    }
}
