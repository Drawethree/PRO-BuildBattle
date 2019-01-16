package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBLeaveSubCommand extends BBSubCommand {

    public BBLeaveSubCommand() {
        super("leave", "Leave arena command.", "buildbattlepro.join", false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
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
