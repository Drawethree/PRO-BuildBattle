package me.drawe.buildbattle.commands.subcommands.npc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.objects.Message;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBAddNPCSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBAddNPCSubCommand(BuildBattle plugin) {
        super("addnpc", " addnpc §8» §7Command to add a floor change NPC", "buildbattlepro.setup", true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (BBHook.getHook("Citizens")) {
            if (sender.hasPermission(getPermissionRequired())) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        final Player p = (Player) sender;
                        final NPCRegistry registry = CitizensAPI.getNPCRegistry();
                        final NPC npc = registry.createNPC(plugin.getSettings().getFloorChangeNPCtype(), Message.CHANGE_FLOOR_NPC_NAME.getMessage());
                        npc.spawn(p.getLocation());
                        npc.setProtected(true);
                        p.sendMessage(plugin.getSettings().getPrefix() + " §aChange floor NPC spawned!");
                        return true;
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + cmd.getName() + " addnpc §8| §7Create Change floor NPC at your location");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(plugin.getSettings().getPrefix() + " §cCitizens plugin is not loaded!");
        }
        return false;
    }
}
