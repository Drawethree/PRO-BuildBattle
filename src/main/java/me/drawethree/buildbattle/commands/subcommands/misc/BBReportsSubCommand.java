package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
