package me.drawe.buildbattle.commands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.subcommands.BBSubCommand;
import me.drawe.buildbattle.commands.subcommands.arena.*;
import me.drawe.buildbattle.commands.subcommands.arena.lobby.BBSetLobbySubCommand;
import me.drawe.buildbattle.commands.subcommands.arena.lobby.BBSetMainLobbySubCommand;
import me.drawe.buildbattle.commands.subcommands.arena.plot.BBAddPlotSubCommand;
import me.drawe.buildbattle.commands.subcommands.arena.plot.BBDelPlotSubCommand;
import me.drawe.buildbattle.commands.subcommands.misc.*;
import me.drawe.buildbattle.commands.subcommands.npc.BBAddNPCSubCommand;
import me.drawe.buildbattle.commands.subcommands.npc.BBDelNPCSubCommand;
import me.drawe.buildbattle.commands.subcommands.stats.BBExportStatsSubCommand;
import me.drawe.buildbattle.commands.subcommands.stats.BBStatsSubCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.utils.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.TreeMap;

public class BBCommand extends BukkitCommand {


    public static final TreeMap<String, BBSubCommand> subCommands = new TreeMap<>();

    static {
        //Admin commands
        subCommands.put("create", new BBCreateSubCommand());
        subCommands.put("delete", new BBDeleteSubCommand());
        subCommands.put("setlobby", new BBSetLobbySubCommand());
        subCommands.put("setmainlobby", new BBSetMainLobbySubCommand());
        subCommands.put("pos", new BBPosSubCommand());
        subCommands.put("addplot", new BBAddPlotSubCommand());
        subCommands.put("delplot", new BBDelPlotSubCommand());
        subCommands.put("addnpc", new BBAddNPCSubCommand());
        subCommands.put("delnpc", new BBDelNPCSubCommand());
        subCommands.put("start", new BBStartSubCommand());
        subCommands.put("forcestart", new BBForceStartSubCommand());
        subCommands.put("stop", new BBStopSubCommand());
        subCommands.put("lb", new BBLeaderBoardSubCommand());
        subCommands.put("debug", new BBDebugSubCommand());
        subCommands.put("editor", new BBEditorSubCommand());
        subCommands.put("supervote", new BBSuperVoteSubCommand());
        subCommands.put("exportstats", new BBExportStatsSubCommand());
        subCommands.put("reports", new BBReportsSubCommand());
        subCommands.put("reload", new BBReloadSubCommand());
        subCommands.put("version", new BBVersionSubCommand());
        //Player commands
        subCommands.put("join", new BBJoinSubCommand());
        subCommands.put("leave", new BBLeaveSubCommand());
        subCommands.put("list", new BBListSubCommand());
        subCommands.put("stats", new BBStatsSubCommand());
        subCommands.put("party", new BBPartySubCommand());
    }

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
        return subCommands.get(name);
    }


    private boolean commandUsage(CommandSender p) {
        if (p.hasPermission("buildbattlepro.create")) {
            sendCommands(p, true, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Admin Commands");
        }
        if (p.hasPermission("buildbattlepro.player")) {
            sendCommands(p, false, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Player Commands");
        }
        return true;
    }

    private void sendCommands(CommandSender p, boolean adminOnly, String title) {
        FancyMessage.sendCenteredMessage(p, title);
        for (BBSubCommand subCommand : subCommands.values()) {
            if (subCommand.isAdminCommand() == adminOnly) {
                p.sendMessage("§e/" + getName() + subCommand.getDescription());
            }
        }
    }
}
