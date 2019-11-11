package me.drawe.buildbattle.commands.subcommands.stats;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.drawe.buildbattle.utils.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBStatsSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBStatsSubCommand(BuildBattle plugin) {
        super("stats", " stats §8» §7Command to show your stats.", "buildbattlepro.player",false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                BBPlayerStats ps = plugin.getPlayerManager().getPlayerStats(p);
                if (ps != null) {
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.STATS_TITLE.getMessage());
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PLAYED.getMessage().replace("%played%", String.valueOf(ps.getStat(BBStat.PLAYED))));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_WINS.getMessage().replace("%wins%", String.valueOf(ps.getStat(BBStat.WINS))));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_MOST_POINTS.getMessage().replace("%most_points%", String.valueOf(ps.getStat(BBStat.MOST_POINTS))));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_BLOCKS_PLACED.getMessage().replace("%blocks%", String.valueOf(ps.getStat(BBStat.BLOCKS_PLACED))));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PARTICLES_PLACED.getMessage().replace("%particles%", String.valueOf(ps.getStat(BBStat.PARTICLES_PLACED))));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_SUPER_VOTES.getMessage().replace("%super_votes%", String.valueOf(ps.getStat(BBStat.SUPER_VOTES))));
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
