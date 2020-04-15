package me.drawethree.buildbattle.commands.subcommands.arena;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.commands.BBCommand;
import me.drawethree.buildbattle.commands.subcommands.BBSubCommand;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BBJoinSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBJoinSubCommand(BuildBattle plugin) {
        super("join", " join [<arena>] [<team/solo>] §8» §7Join an arena", "buildbattlepro.join", false);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("solo")) {
                    BBArena arena = this.plugin.getArenaManager().getArenaToAutoJoin(BBGameMode.SOLO);
                    BBArena playerArena = this.plugin.getPlayerManager().getPlayerArena(p);
                    if (arena != null) {
                        if (playerArena == null) {
                            arena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                        }
                    } else {
                        p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    }
                } else if (args[0].equalsIgnoreCase("team")) {
                    BBArena arena = this.plugin.getArenaManager().getArenaToAutoJoin(BBGameMode.TEAM);
                    BBArena playerArena = this.plugin.getPlayerManager().getPlayerArena(p);
                    if (arena != null) {
                        if (playerArena == null) {
                            arena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                        }
                    } else {
                        p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    }
                } else {
                    BBArena argArena = this.plugin.getArenaManager().getArena(args[0]);
                    BBArena playerArena = this.plugin.getPlayerManager().getPlayerArena(p);
                    if (argArena != null) {
                        if (playerArena == null) {
                            argArena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage().replace("%arena%", playerArena.getName()));
                        }
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replace("%arena%", args[0]));
                    }
                }
            } else {
                BBArena arena = this.plugin.getArenaManager().getArenaToAutoJoin(null);
                BBArena playerArena = this.plugin.getPlayerManager().getPlayerArena(p);
                if (arena != null) {
                    if (playerArena == null) {
                        arena.addPlayer(p);
                        return true;
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(this.plugin.getArenaManager().getArenas().keySet());
        } else if (args.length == 3) {
            Arrays.asList(BBGameMode.values()).stream().map(BBGameMode::getName).collect(Collectors.toList());
        }
        return null;
    }
}
