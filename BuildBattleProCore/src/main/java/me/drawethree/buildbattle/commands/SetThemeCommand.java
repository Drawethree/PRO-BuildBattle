package me.drawethree.buildbattle.commands;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class SetThemeCommand extends BukkitCommand {

    private BuildBattle plugin;

    public SetThemeCommand(BuildBattle plugin) {
        super(plugin.getFileManager().getConfig("src/main/resources/config.yml").get().getString("set_theme_command.name"));
        this.plugin = plugin;
        this.description = plugin.getFileManager().getConfig("src/main/resources/config.yml").get().getString("set_theme_command.description");
        this.setAliases(plugin.getFileManager().getConfig("src/main/resources/config.yml").get().getStringList("set_theme_command.aliases"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.settheme")) {
                if (args.length > 0) {
                    String theme = this.getThemeFromArguments(args);
                    if (plugin.getSettings().isThemeOK(theme)) {
                        BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
                        if (a != null) {
                            if (a.getBBArenaState() == BBArenaState.LOBBY || a.getBBArenaState() == BBArenaState.THEME_VOTING) {
                                if (a.isMinimumPlayersRequirementMet()) {
                                    a.startGame(theme, a.getBBArenaState() == BBArenaState.LOBBY);
                                    return true;
                                } else {
                                    p.sendMessage(Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
                                }
                            } else if(a.getBBArenaState() == BBArenaState.INGAME) {
                                a.changeTheme(theme);
                                return true;
                            } else {
                                p.sendMessage(Message.CANNOT_SET_THEME.getChatMessage());
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

    private String getThemeFromArguments(String[] args) {
        return String.join(" ", args);
    }

}
