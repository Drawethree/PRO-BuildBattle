package me.drawe.buildbattle.commands.subcommands.npc;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.LocationUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBDelNPCSubCommand extends BBSubCommand {

    public BBDelNPCSubCommand() {
        super("delnpc", " delnpc §8» §7Command to remove floor change NPC", "buildbattlepro.setup",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (BBHook.getHook("Citizens")) {
            if (sender.hasPermission(getPermissionRequired())) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPC npc = LocationUtil.getClosestNPC(p);
                        if (npc != null) {
                            npc.destroy();
                            p.sendMessage(BBSettings.getPrefix() + " §aChange floor NPC removed!");
                            return true;
                        } else {
                            p.sendMessage(BBSettings.getPrefix() + " §cThere is no NPC close to your location!");
                        }
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + cmd.getName() + " delnpc §8| §7Delete closest Change floor NPC");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(BBSettings.getPrefix() + " §cCitizens plugin is not loaded!");
        }
        return false;
    }


}
