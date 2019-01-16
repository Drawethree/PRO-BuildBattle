package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBEditorSubCommand extends BBSubCommand {

    public BBEditorSubCommand() {
        super("editor", "Command to open arena editors.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.create")) {
                p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                p.playSound(p.getLocation(), CompSound.NOTE_PLING.getSound(), 1.0F, 1.0F);
                return true;
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }
}
