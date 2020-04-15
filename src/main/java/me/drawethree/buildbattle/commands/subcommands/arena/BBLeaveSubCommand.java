package me.drawethree.buildbattle.commands.subcommands.arena;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
