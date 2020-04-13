package me.drawethree.buildbattle.commands.subcommands.misc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBEditorSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBEditorSubCommand(BuildBattle plugin) {
        super("editor", " editor §8» §7Open arena editor", "buildbattlepro.create",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(getPermissionRequired())) {
                p.openInventory(plugin.getArenaManager().getEditArenasInventory());
                p.playSound(p.getLocation(), CompSound.NOTE_PLING.getSound(), 1.0F, 1.0F);
                return true;
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
