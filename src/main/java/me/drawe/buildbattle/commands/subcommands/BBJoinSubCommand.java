package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBJoinSubCommand extends BBSubCommand {

    public BBJoinSubCommand() {
        super("join", "Command to join arena", false);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("solo")) {
                    BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.SOLO);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
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
                    BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.TEAM);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
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
                    BBArena argArena = ArenaManager.getInstance().getArena(args[0]);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (argArena != null) {
                        if (playerArena == null) {
                            argArena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage().replaceAll("%arena%", playerArena.getName()));
                        }
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else {
                BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(null);
                BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
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
}
