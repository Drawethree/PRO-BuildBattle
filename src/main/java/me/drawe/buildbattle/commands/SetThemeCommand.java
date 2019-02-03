package me.drawe.buildbattle.commands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class SetThemeCommand extends BukkitCommand {

    public SetThemeCommand() {
        super(BuildBattle.getFileManager().getConfig("config.yml").get().getString("set_theme_command.name"));
        this.description = BuildBattle.getFileManager().getConfig("config.yml").get().getString("set_theme_command.description");
        this.setAliases(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("set_theme_command.aliases"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.settheme")) {
                if (args.length == 1) {
                    String theme = args[0];
                    if (BBSettings.isThemeOK(theme)) {
                        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                        if (a != null) {
                            if (a.isMinimumPlayersRequirementMet()) {
                                if (a.getBBArenaState() == BBArenaState.LOBBY) {
                                    a.startGame(theme, true);
                                    return true;
                                } else if (a.getBBArenaState() == BBArenaState.THEME_VOTING) {
                                    a.startGame(theme, false);
                                    return true;
                                } else {
                                    p.sendMessage(Message.CANNOT_SET_THEME.getChatMessage());
                                }
                            } else {
                                p.sendMessage(Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
                            }
                        } else {
                            p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                        }
                    } else {
                        p.sendMessage(Message.THEME_BLACKLISTED.getChatMessage());
                    }
                } else {
                    p.sendMessage("§cUsage >> /" + getName() + " <theme> §8| §7Set theme for current arena");
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }

}
