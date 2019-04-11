package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.ReportManager;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBReportsSubCommand extends BBSubCommand {

    public BBReportsSubCommand() {
        super("reports", " reports §8» §7Command to manage build reports","buildbattlepro.manage.reports", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(getPermissionRequired())) {
                if (BuildBattle.getWorldEdit() != null) {
                    ReportManager.getInstance().openReports(p, 1);
                    return true;
                } else {
                    p.sendMessage(BBSettings.getPrefix() + "§cReports are turned off for version 1.13 and above because there is no stable version of WorldEdit.");
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
