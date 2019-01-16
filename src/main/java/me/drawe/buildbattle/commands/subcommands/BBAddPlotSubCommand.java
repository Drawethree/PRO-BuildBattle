package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBAddPlotSubCommand extends BBSubCommand {

    public BBAddPlotSubCommand() {
        super("addplot", "Command to add a plot to arena.", true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[0]);
                    //Selection sel = BuildBattle.getWorldEdit().getSelection(p);
                    if (arena != null) {
                        if (ArenaManager.getInstance().hasSelectionReady(p)) {
                            Location l1 = ArenaManager.getPlayerBBPos().get(p)[0];
                            Location l2 = ArenaManager.getPlayerBBPos().get(p)[1];
                            Location maxPoint;
                            Location minPoint;
                            if (l1.getY() > l2.getY()) {
                                maxPoint = l1;
                                minPoint = l2;
                            } else {
                                maxPoint = l2;
                                minPoint = l1;
                            }
                            arena.getBuildPlots().add(new BBPlot(arena, minPoint, maxPoint));
                            arena.saveIntoConfig();
                            ArenaManager.getInstance().refreshArenaItem(arena);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aPlot for arena §e" + arena.getName() + " §aadded !");
                            LocationUtil.showCreatedPlot(minPoint, maxPoint, p, 5);
                            return true;
                            // WORLD EDIT NOT WORKING
                       /*if (sel != null) {
                            BBPlot newPlot = new BBPlot(arena, sel.getMinimumPoint(), sel.getMaximumPoint());
                            newPlot.addIntoArenaPlots();
                            arena.saveIntoConfig();
                            OptionsManager.getInstance().refreshArenaItem(arena);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aPlot for arena §e" + arena.getName() + " §aadded !");
                            LocationUtil.showCreatedPlot(sel, p, 5);
                        } else {
                            p.sendMessage(Message.NO_SELECTION.getChatMessage().replaceAll("%arena%", args[1]));
                        }
                        */
                        } else {
                            int i = ArenaManager.getInstance().getMissingSelection(p);
                            switch (i) {
                                case -1:
                                    p.sendMessage(BBSettings.getPrefix() + "§cYou didn't set positions ! Please set them by §e/" + cmd.getName() + " pos");
                                    break;
                                case 1:
                                case 2:
                                    p.sendMessage(BBSettings.getPrefix() + "§cYou didn't set position §e" + i + " §c! Set it by §e/" + cmd.getName() + " pos");
                                    break;
                            }
                        }
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " addplot <arena> §8| §7Add a build plot to arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
