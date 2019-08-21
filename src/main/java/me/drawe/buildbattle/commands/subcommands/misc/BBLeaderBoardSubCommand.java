package me.drawe.buildbattle.commands.subcommands.misc;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.leaderboards.BBLeaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBLeaderBoardSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBLeaderBoardSubCommand(BuildBattle plugin) {
        super("lb", " lb §8» §7Command to manage leaderboards", "buildbattlepro.setup",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (BBHook.getHook("HolographicDisplays")) {
                if (args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    switch (subCommand) {
                        case "refresh":
                            plugin.getLeaderboardManager().refreshAllLeaderBoards();
                            sender.sendMessage(plugin.getSettings().getPrefix() + " §aLeaderboards refreshed !");
                            return true;
                        case "create":
                            if (args.length == 2) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    Location loc = p.getLocation();
                                    try {
                                        LeaderboardType type = LeaderboardType.valueOf(args[1].toUpperCase());
                                        plugin.getLeaderboardManager().createLeaderboard(p, loc, type);
                                        return true;
                                    } catch (Exception e) {
                                        p.sendMessage("§cInvalid type ! Available types :  §e§lWINS, PLAYED, BLOCKS_PLACED, PARTICLES_PLACED");
                                    }
                                }
                            } else {
                                sender.sendMessage("§cUsage >> /" + cmd.getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                            }
                            break;
                        case "select":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                plugin.getLeaderboardManager().selectLeaderboard(p);
                                return true;
                            }
                            break;
                        case "delete":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                BBLeaderboard selected = plugin.getLeaderboardManager().getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.delete();
                                    p.sendMessage(plugin.getSettings().getPrefix() + " §aHologram at location §e" + LocationUtil.getStringFromLocation(selected.getHologram().getLocation()) + " §asuccessfully removed!");
                                    return true;
                                } else {
                                    p.sendMessage(plugin.getSettings().getPrefix() + " §cPlease select a hologram near you by §e/" + cmd.getName() + " lb select §c!");
                                }
                            }
                            break;
                        case "teleport":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                BBLeaderboard selected = plugin.getLeaderboardManager().getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.teleport(p.getLocation());
                                    return true;
                                } else {
                                    p.sendMessage(plugin.getSettings().getPrefix() + " §cPlease select a hologram near you by §e/" + cmd.getName() + " lb select §c!");
                                }
                            }
                            break;
                        default:
                            usage(cmd,sender);
                            break;
                    }
                } else {
                    usage(cmd,sender);
                }
            } else {
                sender.sendMessage(plugin.getSettings().getPrefix() + " §cHolographicDisplays plugin is required to manage leaderboards !");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private void usage(BBCommand cmd,CommandSender sender) {
        sender.sendMessage("§cUsages >> §e/" + cmd.getName() + " lb create <type> §8| §7Create leaderboard with specified type");
        sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb select §8| §7Select leaderboard closest to you");
        sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb delete §8| §7Deletes selected leaderboard");
        sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb teleport §8| §7Teleports selected leaderboard to your position");
        sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb refresh §8| §7Refresh all leaderboards");
    }
}
