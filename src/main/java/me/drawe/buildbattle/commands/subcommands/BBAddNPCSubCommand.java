package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBAddNPCSubCommand extends BBSubCommand {

    public BBAddNPCSubCommand() {
        super("addnpc", "Command to add a floor change NPC.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (BuildBattle.getInstance().isUseCitizens()) {
            if (sender.hasPermission("buildbattlepro.setup")) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPCRegistry registry = CitizensAPI.getNPCRegistry();
                        NPC npc = registry.createNPC(BBSettings.getFloorChangeNPCtype(), Message.CHANGE_FLOOR_NPC_NAME.getMessage());
                        npc.spawn(p.getLocation());
                        npc.setProtected(true);
                        p.sendMessage(BBSettings.getPrefix() + " §aChange floor NPC spawned!");
                        return true;
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + cmd.getName() + " addnpc §8| §7Create Change floor NPC at your location");
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
