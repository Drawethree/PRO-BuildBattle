package me.drawe.buildbattle.commands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.subcommands.*;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.utils.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BBCommand extends BukkitCommand {

    public static final BBSubCommand[] subCommands = new BBSubCommand[]{
            new BBAddNPCSubCommand(),
            new BBAddPlotSubCommand(),
            new BBCreateSubCommand(),
            new BBDebugSubCommand(),
            new BBDeleteSubCommand(),
            new BBDelNPCSubCommand(),
            new BBDelPlotSubCommand(),
            new BBEditorSubCommand(),
            new BBExportStatsSubCommand(),
            new BBForceStartSubCommand(),
            new BBJoinSubCommand(),
            new BBLeaderBoardSubCommand(),
            new BBLeaveSubCommand(),
            new BBListSubCommand(),
            new BBPartySubCommand(),
            new BBPosSubCommand(),
            new BBReloadSubCommand(),
            new BBReportsSubCommand(),
            new BBSetLobbySubCommand(),
            new BBSetMainLobbySubCommand(),
            new BBStartSubCommand(),
            new BBStatsSubCommand(),
            new BBStopSubCommand(),
            new BBSuperVoteSubCommand(),
            new BBVersionSubCommand()
    };

    public BBCommand() {
        super(BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_command.name"));
        this.description = BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_command.description");
        this.setAliases(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("main_command.aliases"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length > 0) {
            final BBSubCommand subCommand = getSubCommand(args[0]);
            if (subCommand != null) {
                return subCommand.execute(this, sender, Arrays.copyOfRange(args, 1, args.length));
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    p.openInventory(ArenaManager.getAllArenasInventory());
                    return true;
                }
            }
        } else {
            return commandUsage(sender);
        }
        return false;
    }

    private BBSubCommand getSubCommand(String name) {
        for (BBSubCommand subCommand : subCommands) {
            if (subCommand.getSubCommand().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }


    private boolean commandUsage(CommandSender p) {
        if (p.hasPermission("buildbattlepro.create")) {
            FancyMessage.sendCenteredMessage(p, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Admin Commands");
            p.sendMessage("§c§lNEW §e/" + getName() + " pos " + "§8» " + "§7Gives you item to make selection of plot");
            p.sendMessage("§e/" + getName() + " create <arena_name> <solo/team> " + "§8» " + "§7Create Arena");
            p.sendMessage("§e/" + getName() + " delete <arena_name> " + "§8» " + "§7Remove Arena");
            p.sendMessage("§e/" + getName() + " addplot <arena_name> " + "§8» " + "§7Add build plot for arena, must have selected positions !");
            p.sendMessage("§e/" + getName() + " delplot <arena> " + "§8» " + "§7Removes latest added plot in arena");
            p.sendMessage("§e/" + getName() + " setlobby <arena> " + "§8» " + "§7Set lobby for Arena");
            p.sendMessage("§e/" + getName() + " setmainlobby " + "§e» " + "§7Set main lobby");
            p.sendMessage("§e/" + getName() + " forcestart " + "§8» " + "§7Force start Arena you are currently in");
            p.sendMessage("§e/" + getName() + " forcestart <arena> " + "§8» " + "§7Force start Arena");
            p.sendMessage("§e/" + getName() + " forcestart <arena> <theme> " + "§8» " + "§7Force start Arena with specified theme");
            p.sendMessage("§e/" + getName() + " start " + "§8» " + "§7Start Arena you are currently in");
            p.sendMessage("§e/" + getName() + " start <arena> " + "§8» " + "§7Start Arena");
            p.sendMessage("§e/" + getName() + " start <arena> <theme> " + "§8» " + "§7Start Arena with specified theme");
            p.sendMessage("§e/" + getName() + " stop " + "§8» " + "§7Force stop Arena you are currently in");
            p.sendMessage("§e/" + getName() + " stop <arena> " + "§8» " + "§7Force stop Arena");
            p.sendMessage("§e/" + getName() + " reload " + "§8» " + "§7Reload plugin");
            p.sendMessage("§e/" + getName() + " editor " + "§8» " + "§7Open arena editor");
            p.sendMessage("§e/" + getName() + " lb create <type> " + "§8» " + "§7Create leaderboard with specified type");
            p.sendMessage("§e/" + getName() + " lb select " + "§8» " + "§7Select leaderboard closest to you");
            p.sendMessage("§e/" + getName() + " lb delete " + "§8» " + "§7Deletes selected leaderboard");
            p.sendMessage("§e/" + getName() + " lb teleport " + "§8» " + "§7Teleports selected leaderboard to your position");
            p.sendMessage("§e/" + getName() + " lb refresh " + "§8» " + "§7Refresh all leaderboards");
            p.sendMessage("§e/" + getName() + " supervote <give/take> <player> <amount> " + "§8» " + "§7Give/take supervotes from player");
            p.sendMessage("§e/settheme <theme> " + "§8» " + "§7Force-set theme for your current arena");
            p.sendMessage("§e/" + getName() + " exportstats " + "§8» " + "§7Export players stats from stats.yml into MySQL");
        }
        if (p.hasPermission("buildbattlepro.player")) {
            FancyMessage.sendCenteredMessage(p, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Player Commands");
            p.sendMessage("§e/" + getName() + " join " + "§8» " + "§7Automatic join to first available Arena");
            p.sendMessage("§e/" + getName() + " join <arena> " + "§8» " + "§7Join specific Arena");
            p.sendMessage("§e/" + getName() + " join <team/solo> " + "§8» " + "§7Automatic join to first available solo/team arena");
            p.sendMessage("§e/" + getName() + " leave " + "§8» " + "§7Leave Arena");
            p.sendMessage("§e/" + getName() + " list " + "§8» " + "§7Open GUI with all arenas");
            p.sendMessage("§e/" + getName() + " party create " + "§8» " + "§7Create party");
            p.sendMessage("§e/" + getName() + " party invite <player> " + "§8» " + "§7Invite player to your party");
            p.sendMessage("§e/" + getName() + " party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
            p.sendMessage("§e/" + getName() + " party leave " + "§8» " + "§7Leave your current party");
        }
        return true;
    }
}
