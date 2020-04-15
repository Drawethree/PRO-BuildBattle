package me.drawethree.buildbattle.commands.subcommands.npc;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.hooks.BBHook;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.utils.LocationUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BBDelNPCSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBDelNPCSubCommand(BuildBattle plugin) {
        super("delnpc", " delnpc §8» §7Command to remove floor change NPC", "buildbattlepro.setup",true);
        this.plugin = plugin;
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
                            p.sendMessage(plugin.getSettings().getPrefix() + " §aChange floor NPC removed!");
                            return true;
                        } else {
                            p.sendMessage(plugin.getSettings().getPrefix() + " §cThere is no NPC close to your location!");
                        }
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + cmd.getName() + " delnpc §8| §7Delete closest Change floor NPC");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(plugin.getSettings().getPrefix() + " §cCitizens plugin is not loaded!");
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
