package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.utils.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBStatsSubCommand extends BBSubCommand {

    public BBStatsSubCommand() {
        super("stats", "Command to show your stats.", "buildbattlepro.player",false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                BBPlayerStats ps = PlayerManager.getInstance().getPlayerStats(p);
                if (ps != null) {
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.STATS_TITLE.getMessage());
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PLAYED.getMessage().replaceAll("%played%", String.valueOf(ps.getPlayed())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_WINS.getMessage().replaceAll("%wins%", String.valueOf(ps.getWins())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_MOST_POINTS.getMessage().replaceAll("%most_points%", String.valueOf(ps.getMostPoints())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_BLOCKS_PLACED.getMessage().replaceAll("%blocks%", String.valueOf(ps.getBlocksPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PARTICLES_PLACED.getMessage().replaceAll("%particles%", String.valueOf(ps.getParticlesPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_SUPER_VOTES.getMessage().replaceAll("%super_votes%", String.valueOf(ps.getSuperVotes())));
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    return true;
                } else {
                    p.sendMessage(Message.NOT_PLAYED.getChatMessage());
                }
            }
        } else {
            sender.sendMessage("§cUsage >> /" + cmd.getName() + " stats §8| §7Show your BuildBattlePro stats");
        }
        return false;
    }
}
