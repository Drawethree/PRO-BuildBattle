package me.drawe.buildbattle.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.leaderboards.Leaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.LocationUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BBCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("buildbattle")) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "join":
                        joinSubCommand(sender, args);
                        break;
                    case "leave":
                        leaveSubCommand(sender, args);
                        break;
                    case "create":
                        createSubCommand(sender, args);
                        break;
                    case "delete":
                        delArenaSubCommand(sender, args);
                        break;
                    case "addplot":
                        addPlotSubCommand(sender, args);
                        break;
                    case "setlobby":
                        setLobbySubCommand(sender, args);
                        break;
                    case "delplot":
                        delPlotSubCommand(sender,args);
                        break;
                    case "start":
                        startSubCommand(sender,args);
                        break;
                    case "stop":
                        stopSubCommand(sender,args);
                        break;
                    case "stats":
                        sendBBStats(sender, args);
                        break;
                    case "reload":
                        reloadSubCommand(sender,args);
                        break;
                    case "list":
                        listArenasSubCommand(sender, args);
                        break;
                    case "help":
                        commandUsage(sender);
                        break;
                    case "exportstats":
                        exportStatsSubCommand(sender,args);
                        break;
                    case "lb":
                        leaderBoardSubCommand(sender,args);
                        break;
                    case "leaderboard":
                        leaderBoardSubCommand(sender,args);
                        break;
                    case "addnpc":
                        addFloorNPC(sender,args);
                        break;
                    case "party":
                        partySubCommand(sender,args);
                        break;
                }
            } else {
                commandUsage(sender);
            }

        }
        return true;
    }

    private void partySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (GameManager.isPartiesEnabled()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        case "create":
                            PartyManager.getInstance().createParty(p);
                            break;
                        case "accept":
                            PartyManager.getInstance().manageInvite(p, true);
                            break;
                        case "decline":
                            PartyManager.getInstance().manageInvite(p, false);
                            break;
                        case "leave":
                            PartyManager.getInstance().leaveParty(p);
                            break;
                        case "invite":
                            if (args.length == 3) {
                                BBParty playerParty = PartyManager.getInstance().getPlayerParty(p);
                                Player target = Bukkit.getPlayer(args[2]);
                                PartyManager.getInstance().invitePlayer(p, target, playerParty);
                            } else {
                                p.sendMessage(Message.PARTY_INVALID_USAGE.getChatMessage());
                            }
                            break;
                    }
                } else {
                    p.sendMessage("§cInvalid usage!");
                    p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
                    p.sendMessage("§e/bb party invite <player> " + "§8» " + "§7Invite player to your party");
                    p.sendMessage("§e/bb party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
                    p.sendMessage("§e/bb party leave " + "§8» " + "§7Leave your current party");
                }
            } else {
                p.sendMessage(Message.PARTIES_NOT_ALLOWED.getChatMessage());
            }
        }
    }

    private void addFloorNPC(CommandSender sender, String[] args) {
        if(Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            if (sender.hasPermission("buildbattlepro.setup")) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPCRegistry registry = CitizensAPI.getNPCRegistry();
                        NPC npc = registry.createNPC(GameManager.getFloorChangeNPCtype(), Message.CHANGE_FLOOR_NPC_NAME.getMessage());
                        npc.spawn(p.getLocation());
                        npc.setProtected(true);
                        p.sendMessage(GameManager.getPrefix() + " §aChange floor NPC spawned!");
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/bb addnpc §8| §7Create Change floor NPC at your location");
                }
            }
        } else {
            sender.sendMessage(GameManager.getPrefix() + " §cCitizens plugin is not loaded!");
        }
    }

    private void leaderBoardSubCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("buildbattlepro.setup")) {
            if(BuildBattle.getInstance().isUseHolographicDisplays()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        case "refresh":
                            LeaderboardManager.getInstance().refreshAllLeaderBoards();
                            sender.sendMessage(GameManager.getPrefix() + " §aLeaderboards refreshed !");
                            break;
                        case "create":
                            if (args.length == 3) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    Location loc = p.getLocation();
                                    try {
                                        LeaderboardType type = LeaderboardType.valueOf(args[2].toUpperCase());
                                        LeaderboardManager.getInstance().createLeaderboard(p, loc, type);
                                    } catch (Exception e) {
                                        p.sendMessage("§cInvalid type ! Available types :  §e§lWINS, PLAYED, BLOCKS_PLACED, PARTICLES_PLACED");
                                        return;
                                    }
                                }
                            } else {
                                sender.sendMessage("§cUsage >> /bb lb create <type> §8| §7Create leaderboard with specified type");
                            }
                            break;
                        case "select":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                LeaderboardManager.getInstance().selectLeaderboard(p);
                            }
                            break;
                        case "delete":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.delete();
                                    p.sendMessage(GameManager.getPrefix() + " §aHologram at location §e" + LocationUtil.getStringFromLocation(selected.getLocation()) + " §asuccessfully removed!");
                                } else {
                                    p.sendMessage(GameManager.getPrefix() + " §cPlease select a hologram near you by §e/bb lb select §c!");
                                }
                            }
                            break;
                        case "teleport":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.teleport(p.getLocation());
                                } else {
                                    p.sendMessage(GameManager.getPrefix() + " §cPlease select a hologram near you by §e/bb lb select §c!");
                                }
                            }
                            break;
                        default:
                            sender.sendMessage("§cUsages >> §e/bb lb create <type> §8| §7Create leaderboard with specified type");
                            sender.sendMessage("         §c>> §e/bb lb select §8| §7Select leaderboard closest to you");
                            sender.sendMessage("         §c>> §e/bb lb delete §8| §7Deletes selected leaderboard");
                            sender.sendMessage("         §c>> §e/bb lb teleport §8| §7Teleports selected leaderboard to your position");
                            sender.sendMessage("         §c>> §e/bb lb refresh §8| §7Refresh all leaderboards");
                            break;
                    }
                } else {
                    sender.sendMessage("§cUsages >> §e/bb lb create <type> §8| §7Create leaderboard with specified type");
                    sender.sendMessage("         §c>> §e/bb lb select §8| §7Select leaderboard closest to you");
                    sender.sendMessage("         §c>> §e/bb lb delete §8| §7Deletes selected leaderboard");
                    sender.sendMessage("         §c>> §e/bb lb teleport §8| §7Teleports selected leaderboard to your position");
                    sender.sendMessage("         §c>> §e/bb lb refresh §8| §7Refresh all leaderboards");
                }
            } else {
                sender.sendMessage(GameManager.getPrefix() + " §cHolographicDisplays plugin is required to manage leaderboards !");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void exportStatsSubCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("buildbattlepro.admin")) {
            if(args.length == 1) {
                if(GameManager.getStatsType() == StatsType.MYSQL) {
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §cUser §e" + sender.getName() + " §chas requested exporting players stats to MySQL!");
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §7Starting exporting player stats from stats.yml into MySQL database...");
                    sender.sendMessage(GameManager.getPrefix() + " §7§oStarting exporting players stats from stats.yml into MySQL database...");
                    int playersTransfered = 0;
                    for(BBPlayerStats stats : PlayerManager.getPlayerStats()) {
                        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §7Copying data of user §e" + stats.getUuid().toString() + " §7into MySQL");
                        MySQLManager.getInstance().addPlayerToTable(stats);
                        playersTransfered += 1;
                    }
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aExport finished. §e" + playersTransfered + "§a players data have been transferred.");
                    sender.sendMessage(GameManager.getPrefix() + " §2Done! §e" + playersTransfered + "§2 players have been transferred.");
                } else {
                    sender.sendMessage(GameManager.getPrefix() + " §cTo export data, firstly please enable and setup MySQL!");
                }
            } else {
                sender.sendMessage("§cUsage >> /bb exportstats §8| §7Export players stats from stats.yml into MySQL");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }


    private void startSubCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("buildbattlepro.start")) {
            if(args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender);
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if(args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStart(sender);
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if(args.length == 3) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                String theme = args[2];
                if (arena != null) {
                    if(theme != null) {
                        arena.forceStart(sender, theme);
                    } else {
                        sender.sendMessage(GameManager.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /bb start  §8| §7Force start arena you are in");
                sender.sendMessage("§cUsage >> /bb start <arena> §8| §7Force start specific arena");
                sender.sendMessage("§cUsage >> /bb start <arena> <theme> §8| §7Force start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void stopSubCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("buildbattlepro.stop")) {
            if(args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStop(sender);
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if(args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if(arena != null) {
                    arena.forceStop(sender);
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /bb stop §8| §7Force stop arena you are in");
                sender.sendMessage("§cUsage >> /bb stop <arena> §8| §7Force stop specified arena");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }
    private void sendBBStats(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                BBPlayerStats ps = PlayerManager.getInstance().getPlayerStats(p);
                if (ps != null) {
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p,Message.STATS_TITLE.getMessage());
                    FancyMessage.sendCenteredMessage(p,Message.STATS_PLAYED.getMessage().replaceAll("%played%", String.valueOf(ps.getPlayed())));
                    FancyMessage.sendCenteredMessage(p,Message.STATS_WINS.getMessage().replaceAll("%wins%", String.valueOf(ps.getWins())));
                    FancyMessage.sendCenteredMessage(p,Message.STATS_MOST_POINTS.getMessage().replaceAll("%most_points%", String.valueOf(ps.getMostPoints())));
                    FancyMessage.sendCenteredMessage(p,Message.STATS_BLOCKS_PLACED.getMessage().replaceAll("%blocks%", String.valueOf(ps.getBlocksPlaced())));
                    FancyMessage.sendCenteredMessage(p,Message.STATS_PARTICLES_PLACED.getMessage().replaceAll("%particles%", String.valueOf(ps.getParticlesPlaced())));
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                } else {
                    p.sendMessage(Message.NOT_PLAYED.getChatMessage());
                }
            }
        } else {
            sender.sendMessage("§cUsage >> /bb stats §8| §7Show your BuildBattlePro stats");
        }
    }

    private void delArenaSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    ArenaManager.getInstance().removeArena(sender, arena);
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                }
            } else {
                sender.sendMessage("§cUsage >> /bb delete <arena> §8| §7Deletes arena.");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }


    private void delPlotSubCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("buildbattlepro.create")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena a = ArenaManager.getInstance().getArena(args[1]);
                        if(a != null) {
                            int lastIndex = a.getBuildPlots().size()-1;
                            if(lastIndex < 0) {
                                p.sendMessage("§e§lBuildBattle Setup §8| §cArena §e" + a.getName() + " §chas no build plots ! Create some !");
                                return;
                            } else {
                                a.getBuildPlots().remove(lastIndex);
                                a.saveIntoConfig();
                                OptionsManager.getInstance().refreshArenaItem(a);
                                p.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully removed plot §e" + (lastIndex + 1) + " §afrom arena §e" + a.getName() + " §a!");
                            }
                        }
                } else {
                    sender.sendMessage("§cUsage >> /bb delplot <arena> §8| §7Deletes plot at your current location ");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void createSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                if (args.length == 3) {
                    String arenaName = args[1];
                    String gameMode = args[2];
                    ArenaManager.getInstance().createArena(sender, arenaName, gameMode);
                } else {
                    sender.sendMessage("§cUsage >> /bb create <name> <solo/team> §8| §7Create an buildbattle arena");
                }
            }
        }
    }

    private void addPlotSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                    Selection sel = BuildBattle.getWorldEdit().getSelection(p);
                    if (arena != null) {
                        if (sel != null) {
                            BBPlot newPlot = new BBPlot(arena, sel.getMinimumPoint(), sel.getMaximumPoint());
                            newPlot.addIntoArenaPlots();
                            arena.saveIntoConfig();
                            OptionsManager.getInstance().refreshArenaItem(arena);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aPlot for arena §e" + arena.getName() + " §aadded !");
                            LocationUtil.showCreatedPlot(sel, p, 5);
                        } else {
                            p.sendMessage(Message.NO_SELECTION.getChatMessage().replaceAll("%arena%", args[1]));
                        }
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /bb addplot <arena> §8| §7Add a build plot to arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void setLobbySubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                    Location playerLoc = p.getLocation();
                    if (arena != null) {
                        arena.setLobbyLocation(playerLoc);
                        p.sendMessage("§e§lBuildBattle Setup §8| §aLobby Location for arena §e" + arena.getName() + " §aset !");
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /bb setlobby <arena> §8| §7Set Lobby location for arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void leaveSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null) {
                arena.removePlayer(p);
            } else {
                p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
            }
        }
    }

    private void listArenasSubCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(OptionsManager.getAllArenasInventory());
            }
        } else {
            sender.sendMessage("§cUsage >> /bb list §8| §7Show all arenas and their status");
        }
    }

    private void reloadSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                BuildBattle.getInstance().reloadPlugin();
                sender.sendMessage(GameManager.getPrefix() + " §aPlugin reloaded !");
            } else {
                sender.sendMessage("§cUsage >> /bb reload §8| §7Reloads plugin");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }
    private void commandUsage(CommandSender p) {
        if (p.hasPermission("buildbattlepro.create")) {
            FancyMessage.sendCenteredMessage(p, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Admin Commands");
            p.sendMessage("§e/bb create <arena_name> <solo/team> " + "§8» " + "§7Create Arena");
            p.sendMessage("§e/bb delete <arena_name> " + "§8» " + "§7Remove Arena");
            p.sendMessage("§e/bb addplot <arena_name> " + "§8» " + "§7Add build plot for arena, must have selection !");
            p.sendMessage("§e/bb delplot <arena> " + "§8» " + "§7Removes latest added plot in arena");
            p.sendMessage("§e/bb setlobby <arena> " + "§8» " + "§7Set lobby for Arena");
            p.sendMessage("§e/bb start " + "§8» " + "§7Force start Arena you are currently in");
            p.sendMessage("§e/bb start <arena> " + "§8» " + "§7Force start Arena");
            p.sendMessage("§e/bb start <arena> <theme> " + "§8» " + "§7Force start Arena with specified theme");
            p.sendMessage("§e/bb stop " + "§8» " + "§7Force stop Arena you are currently in");
            p.sendMessage("§e/bb stop <arena> " + "§8» " + "§7Force stop Arena");
            p.sendMessage("§e/bb reload " + "§8» " + "§7Reload plugin");
            p.sendMessage("§e/bb lb create <type> " + "§8» " + "§7Create leaderboard with specified type");
            p.sendMessage("§e/bb lb select " + "§8» " + "§7Select leaderboard closest to you");
            p.sendMessage("§e/bb lb delete " + "§8» " +  "§7Deletes selected leaderboard");
            p.sendMessage("§e/bb lb teleport " + "§8» " + "§7Teleports selected leaderboard to your position");
            p.sendMessage("§e/bb lb refresh " + "§8» " + "§7Refresh all leaderboards");
            p.sendMessage("§e/settheme <theme> " + "§8» " + "§7Force-set theme for your current arena");
            p.sendMessage("§e/bb exportstats " + "§8» " + "§7Export players stats from stats.yml into MySQL");
            FancyMessage.sendCenteredMessage(p,"§6✪§e§lBuildBattlePro§6✪ §8- §6Player Commands");
            p.sendMessage("§e/bb join " + "§8» " + "§7Automatic join to first available Arena");
            p.sendMessage("§e/bb join <arena> " + "§8» " + "§7Join specific Arena");
            p.sendMessage("§e/bb leave " + "§8» " + "§7Leave Arena");
            p.sendMessage("§e/bb list " + "§8» " + "§7Open GUI with all arenas");
            p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
            p.sendMessage("§e/bb party invite <player> " + "§8» " + "§7Invite player to your party");
            p.sendMessage("§e/bb party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
            p.sendMessage("§e/bb party leave " + "§8» " + "§7Leave your current party");
            return;
        } else {
            FancyMessage.sendCenteredMessage(p,"§6✪ §e§lBuildBattlePro §6✪ §8- §6Player Commands");
            p.sendMessage("§e/bb join " + "§8» " + "§7Automatic join to first available Arena");
            p.sendMessage("§e/bb join <arena> " + "§8» " + "§7Join specific Arena");
            p.sendMessage("§e/bb leave " + "§8» " + "§7Leave Arena");
            p.sendMessage("§e/bb list " + "§8» " + "§7Open GUI with all arenas");
            p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
            p.sendMessage("§e/bb party invite <player> " + "§8» " + "§7Invite player to your party");
            p.sendMessage("§e/bb party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
            p.sendMessage("§e/bb party leave " + "§8» " + "§7Leave your current party");
        }
    }

    private void joinSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 2) {
                BBArena argArena = ArenaManager.getInstance().getArena(args[1]);
                BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                if (argArena != null) {
                    if (playerArena == null) {
                        argArena.addPlayer(p);
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage().replaceAll("%arena%", playerArena.getName()));
                    }
                } else {
                    p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                }
            } else {
                BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin();
                BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                if (arena != null) {
                    if(playerArena == null) {
                        arena.addPlayer(p);
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                }
            }
        }
    }
}
