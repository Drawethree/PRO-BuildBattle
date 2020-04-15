package me.drawethree.buildbattle.commands.subcommands.arena;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.spectateapi.Spectatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BBSpectateSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBSpectateSubCommand(BuildBattle plugin) {
        super("spectate", " spectate <arena> §8» §7Spectate an arena", "buildbattlepro.spectate", false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                BBArena currentArena = this.plugin.getPlayerManager().getPlayerArena(p);
                Spectatable currentSpectate = this.plugin.getSpectatorManager().getSpectators().get(p);
                if (currentArena == null) {
                    if (currentSpectate == null) {
                        BBArena targetArena = this.plugin.getArenaManager().getArena(args[0]);
                        if(targetArena != null) {
                            this.plugin.getSpectatorManager().spectate(p, targetArena);
                            return true;
                        } else {
                            p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                        }
                    } else {
                        this.plugin.getSpectatorManager().unspectate(p);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(this.plugin.getArenaManager().getArenas().keySet());
        }
        return null;
    }
}
