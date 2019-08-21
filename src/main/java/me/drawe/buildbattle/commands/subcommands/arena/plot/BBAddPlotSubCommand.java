package me.drawe.buildbattle.commands.subcommands.arena.plot;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBAddPlotSubCommand extends BBSubCommand {

    private BuildBattle plugin;

    public BBAddPlotSubCommand(BuildBattle plugin) {
        super("addplot", " addplot <arena_name> §8» §7Add build plot for arena, must have selected positions !", "buildbattlepro.create",true);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    BBArena arena = this.plugin.getArenaManager().getArena(args[0]);
                    //Selection sel = BuildBattle.getWorldEdit().getSelection(p);
                    if (arena != null) {
                        if (this.plugin.getArenaManager().hasSelectionReady(p)) {
                            Location l1 = this.plugin.getArenaManager().getPlayerBBPos().get(p)[0];
                            Location l2 = this.plugin.getArenaManager().getPlayerBBPos().get(p)[1];
                            Location maxPoint;
                            Location minPoint;
                            if (l1.getY() > l2.getY()) {
                                maxPoint = l1;
                                minPoint = l2;
                            } else {
                                maxPoint = l2;
                                minPoint = l1;
                            }
                            arena.getBuildPlots().add(new BBPlot(this.plugin,arena, minPoint, maxPoint));
                            arena.saveIntoConfig();
                            this.plugin.getArenaManager().refreshArenaItem(arena);
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
                            int i = this.plugin.getArenaManager().getMissingSelection(p);
                            switch (i) {
                                case -1:
                                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§cYou didn't set positions ! Please set them by §e/" + cmd.getName() + " pos");
                                    break;
                                case 1:
                                case 2:
                                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§cYou didn't set position §e" + i + " §c! Set it by §e/" + cmd.getName() + " pos");
                                    break;
                            }
                        }
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[0]));
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
