package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBReportsSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBReportsSubCommand(BuildBattle plugin) {
        super("reports", " reports §8» §7Command to manage build reports","buildbattlepro.manage.reports", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(getPermissionRequired())) {
                if (plugin.getWorldEdit() != null) {
                    plugin.getReportManager().openReports(p, 1);
                    return true;
                } else {
                    p.sendMessage(plugin.getSettings().getPrefix() + "§cReports are turned off for version 1.13 and above because there is no stable version of WorldEdit.");
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
