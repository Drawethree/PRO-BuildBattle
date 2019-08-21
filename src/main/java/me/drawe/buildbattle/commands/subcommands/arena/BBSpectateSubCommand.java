package me.drawe.buildbattle.commands.subcommands.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.Spectetable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBSpectateSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBSpectateSubCommand(BuildBattle plugin) {
        super("spectate", "spectate <arena> §8» §7Spectate an arena", "buildbattlepro.spectate", false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                BBArena currentArena = this.plugin.getPlayerManager().getPlayerArena(p);
                Spectetable currentSpectate = this.plugin.getPlayerManager().getSpectators().get(p);
                if (currentArena == null) {
                    if (currentSpectate == null) {
                        BBArena targetArena = this.plugin.getArenaManager().getArena(args[0]);
                        if(targetArena != null) {
                            this.plugin.getPlayerManager().spectate(p, targetArena);
                            return true;
                        } else {
                            p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                        }
                    } else {
                        this.plugin.getPlayerManager().unspectate(p);
                        return true;
                    }
                } else {
                    p.sendMessage(Message.FIRST_LEAVE_ARENA.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + cmd.getName() + " spectate <arena> §8| §7Spectate/Unspectate an arena");
            }
        }
        return false;
    }
}
