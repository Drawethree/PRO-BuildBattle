package me.drawe.buildbattle.commands;

import lombok.Getter;
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
import me.drawe.buildbattle.utils.FancyMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class BBCommand extends BukkitCommand implements TabCompleter {


    @Getter
    private final BuildBattle plugin;
    private final TreeMap<String, BBSubCommand> subCommands;


    public BBCommand(BuildBattle plugin) {
        super(plugin.getFileManager().getConfig("config.yml").get().getString("main_command.name"));
        this.plugin = plugin;
        this.description = plugin.getFileManager().getConfig("config.yml").get().getString("main_command.description");
        this.setAliases(plugin.getFileManager().getConfig("config.yml").get().getStringList("main_command.aliases"));

        this.subCommands = new TreeMap<>();
        //Admin commands
        this.subCommands.put("create", new BBCreateSubCommand(plugin));
        this.subCommands.put("delete", new BBDeleteSubCommand(plugin));
        this.subCommands.put("setlobby", new BBSetLobbySubCommand(plugin));
        this.subCommands.put("setmainlobby", new BBSetMainLobbySubCommand(plugin));
        this.subCommands.put("pos", new BBPosSubCommand(plugin));
        this.subCommands.put("addplot", new BBAddPlotSubCommand(plugin));
        this.subCommands.put("delplot", new BBDelPlotSubCommand(plugin));
        this.subCommands.put("addnpc", new BBAddNPCSubCommand(plugin));
        this.subCommands.put("delnpc", new BBDelNPCSubCommand(plugin));
        this.subCommands.put("start", new BBStartSubCommand(plugin));
        this.subCommands.put("forcestart", new BBForceStartSubCommand(plugin));
        this.subCommands.put("stop", new BBStopSubCommand(plugin));
        this.subCommands.put("lb", new BBLeaderBoardSubCommand(plugin));
        this.subCommands.put("debug", new BBDebugSubCommand(plugin));
        this.subCommands.put("editor", new BBEditorSubCommand(plugin));
        this.subCommands.put("supervote", new BBSuperVoteSubCommand(plugin));
        this.subCommands.put("exportstats", new BBExportStatsSubCommand(plugin));
        this.subCommands.put("reports", new BBReportsSubCommand(plugin));
        this.subCommands.put("reload", new BBReloadSubCommand(plugin));
        this.subCommands.put("version", new BBVersionSubCommand(plugin));
        //Player commands
        this.subCommands.put("join", new BBJoinSubCommand(plugin));
        this.subCommands.put("leave", new BBLeaveSubCommand(plugin));
        this.subCommands.put("spectate", new BBSpectateSubCommand(plugin));
        this.subCommands.put("list", new BBListSubCommand(plugin));
        this.subCommands.put("stats", new BBStatsSubCommand(plugin));
        this.subCommands.put("party", new BBPartySubCommand(plugin));
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
                    p.openInventory(this.plugin.getArenaManager().getAllArenasInventory());
                    return true;
                }
            }
        } else {
            return commandUsage(sender);
        }
        return false;
    }

    private BBSubCommand getSubCommand(String name) {
        return this.subCommands.get(name);
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
        FancyMessage.sendCenteredMessage(p, "§b[] §8- §7Optional argument §b<> §8- §7Required argument");
        for (BBSubCommand subCommand : this.subCommands.values()) {
            if (subCommand.isAdminCommand() == adminOnly) {
                p.sendMessage("§e/" + getName() + subCommand.getDescription());
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(this.subCommands.keySet());
    }
}
