package me.drawe.buildbattle.commands;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetThemeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("settheme")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("buildbattlepro.settheme")) {
                    if (args.length == 1) {
                        String theme = args[0];
                        if (GameManager.getInstance().isThemeOK(theme)) {
                            BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                            if (a != null) {
                                if (a.isMinimumPlayersRequirementMet()) {
                                    if (a.getBBArenaState() == BBArenaState.LOBBY) {
                                        a.startGame(theme, true);
                                    } else if (a.getBBArenaState() == BBArenaState.THEME_VOTING) {
                                        a.startGame(theme, false);
                                    } else {
                                        p.sendMessage(Message.CANNOT_SET_THEME.getChatMessage());
                                        return false;
                                    }
                                } else {
                                    p.sendMessage(Message.NOT_ENOUGH_PLAYERS.getChatMessage());
                                }
                            } else {
                                p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                                return false;
                            }
                        } else {
                            p.sendMessage(Message.THEME_BLACKLISTED.getChatMessage());
                            return false;
                        }
                    } else {
                        p.sendMessage("§cUsage >> /settheme <theme> §8| §7Set theme for current arena");
                        return false;
                    }
                } else {
                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                    return false;
                }
            }
        }
        return true;
    }
}
