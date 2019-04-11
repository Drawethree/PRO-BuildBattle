package me.drawe.buildbattle.commands.subcommands.stats;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.MySQLManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import org.bukkit.command.CommandSender;

public class BBExportStatsSubCommand extends BBSubCommand {

    public BBExportStatsSubCommand() {
        super("exportstats", " exportstats §8» §7Export players stats from stats.yml into MySQL", "buildbattlepro.admin", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                if (BBSettings.getStatsType() == StatsType.MYSQL) {
                    BuildBattle.info("§cUser §e" + sender.getName() + " §chas requested exporting players stats to MySQL!");
                    BuildBattle.info("§7Starting exporting player stats from stats.yml into MySQL database...");
                    sender.sendMessage(BBSettings.getPrefix() + " §7§oStarting exporting players stats from stats.yml into MySQL database...");
                    int playersTransfered = 0;
                    for (BBPlayerStats stats : PlayerManager.getPlayerStats().values()) {
                        BuildBattle.info("§7Copying data of user §e" + stats.getUuid().toString() + " §7into MySQL");
                        MySQLManager.getInstance().addPlayerToTable(stats);
                        playersTransfered += 1;
                    }
                    BuildBattle.info("§aExport finished. §e" + playersTransfered + "§a players data have been transferred.");
                    sender.sendMessage(BBSettings.getPrefix() + " §2Done! §e" + playersTransfered + "§2 players have been transferred.");
                    return true;
                } else {
                    sender.sendMessage(BBSettings.getPrefix() + " §cTo export data, firstly please enable and setup MySQL!");
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " exportstats §8| §7Export players stats from stats.yml into MySQL");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
