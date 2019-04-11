package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBPosSubCommand extends BBSubCommand {

    public BBPosSubCommand() {
        super("pos", " pos §8» §7Gives you item to make selection of plot", "buildbattlepro.create",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission(getPermissionRequired())) {
                p.getInventory().addItem(ArenaManager.posSelectorItem);
                sender.sendMessage(BBSettings.getPrefix() + " §aYou were given §ePlot Selector §a!");
                sender.sendMessage(BBSettings.getPrefix() + " §eLeft-Click §ablock to selection §ePostion 1");
                sender.sendMessage(BBSettings.getPrefix() + " §eRight-Click §ablock to selection §ePostion 2");
                return true;
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
