package me.drawe.buildbattle.commands.subcommands.stats;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BBExportStatsSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBExportStatsSubCommand(BuildBattle plugin) {
        super("exportstats", " exportstats §8» §7Export players stats from stats.yml into MySQL", "buildbattlepro.admin", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (args.length == 0) {
                if (plugin.getSettings().getStatsType() == StatsType.MYSQL) {
                    plugin.info("§cUser §e" + sender.getName() + " §chas requested exporting players stats to MySQL!");
                    plugin.info("§7Starting exporting player stats from stats.yml into MySQL database...");
                    sender.sendMessage(plugin.getSettings().getPrefix() + " §7§oStarting exporting players stats from stats.yml into MySQL database...");
                    int playersTransfered = 0;
                    for (String s : plugin.getFileManager().getConfig("stats.yml").get().getKeys(false)) {
                        BBPlayerStats stats = new BBPlayerStats(UUID.fromString(s));
                        for(BBStat stat : BBStat.values()) {
                            stats.setStat(stat, plugin.getFileManager().getConfig("stats.yml").get().get(s + "." + stat.getConfigKey()));
                        }
                        plugin.info("§7Copying data of user §e" + s + " §7into MySQL");
                        plugin.getMySQLManager().addPlayerToTable(stats);
                        playersTransfered += 1;
                    }
                    plugin.info("§aExport finished. §e" + playersTransfered + "§a players data have been transferred.");
                    sender.sendMessage(plugin.getSettings().getPrefix() + " §2Done! §e" + playersTransfered + "§2 players have been transferred.");
                    return true;
                } else {
                    sender.sendMessage(plugin.getSettings().getPrefix() + " §cTo export data, firstly please enable and setup MySQL!");
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
