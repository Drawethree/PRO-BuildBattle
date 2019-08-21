package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBPosSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBPosSubCommand(BuildBattle plugin) {
        super("pos", " pos §8» §7Gives you item to make selection of plot", "buildbattlepro.create",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission(getPermissionRequired())) {
                p.getInventory().addItem(plugin.getArenaManager().getPosSelectorItem());
                sender.sendMessage(plugin.getSettings().getPrefix() + " §aYou were given §ePlot Selector §a!");
                sender.sendMessage(plugin.getSettings().getPrefix() + " §eLeft-Click §ablock to selection §ePostion 1");
                sender.sendMessage(plugin.getSettings().getPrefix() + " §eRight-Click §ablock to selection §ePostion 2");
                return true;
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
