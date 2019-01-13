package me.drawe.buildbattle.commands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.leaderboards.Leaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.BBParty;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.FancyMessage;
import me.drawe.buildbattle.utils.LocationUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class BBCommand extends BukkitCommand {

    public BBCommand() {
        super(BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_command.name"));
        this.description = BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_command.description");
        this.setAliases(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("main_command.aliases"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "debug":
                    return debugSubCommand(sender);
                case "join":
                    return joinSubCommand(sender, args);
                case "leave":
                    return leaveSubCommand(sender, args);
                case "create":
                    return createSubCommand(sender, args);
                case "delete":
                    return delArenaSubCommand(sender, args);
                case "addplot":
                    return addPlotSubCommand(sender, args);
                case "setlobby":
                    return setLobbySubCommand(sender, args);
                case "setmainlobby":
                    return setMainLobbySubCommand(sender, args);
                case "delplot":
                    return delPlotSubCommand(sender, args);
                case "supervote":
                    return superVoteSubCommand(sender, args);
                case "start":
                    return startSubCommand(sender, args);
                case "stop":
                    return stopSubCommand(sender, args);
                case "forcestart":
                    return forceStartSubCommand(sender, args);
                case "stats":
                    return sendBBStats(sender, args);
                case "reload":
                    return reloadSubCommand(sender, args);
                case "list":
                    return listArenasSubCommand(sender, args);
                case "help":
                    return commandUsage(sender);
                case "exportstats":
                    return exportStatsSubCommand(sender, args);
                case "lb":
                    return leaderBoardSubCommand(sender, args);
                case "version":
                    return versionSubCommand(sender);
                case "leaderboard":
                    return leaderBoardSubCommand(sender, args);
                case "addnpc":
                    return addFloorNPC(sender, args);
                case "delnpc":
                    return delFloorNPC(sender, args);
                case "party":
                    return partySubCommand(sender, args);
                case "editor":
                    return openEditor(sender);
                case "pos":
                    return posSubCommand(sender);
                case "reports":
                    return openReports(sender);
                default:
                    return pluginInfo(sender);
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(ArenaManager.getAllArenasInventory());
                return true;
            }
        }
        return false;
    }

    private boolean debugSubCommand(CommandSender sender) {
        if (sender.isOp()) {
            sender.sendMessage(BBSettings.getPrefix() + "§aDebug mode >> §e" + BuildBattle.enableDebugMode());
            return true;
        }
        return false;
    }

    private boolean posSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("buildbattlepro.create")) {
                p.getInventory().addItem(ArenaManager.posSelectorItem);
                sender.sendMessage(BBSettings.getPrefix() + " §aYou were given §ePlot Selector §a!");
                sender.sendMessage(BBSettings.getPrefix() + " §eLeft-Click §ablock to selection §ePostion 1");
                sender.sendMessage(BBSettings.getPrefix() + " §eRight-Click §ablock to selection §ePostion 2");
                return true;
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }

    private boolean delFloorNPC(CommandSender sender, String[] args) {
        if (BuildBattle.getInstance().isUseCitizens()) {
            if (sender.hasPermission("buildbattlepro.setup")) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPC npc = LocationUtil.getClosestNPC(p);
                        if (npc != null) {
                            npc.destroy();
                            p.sendMessage(BBSettings.getPrefix() + " §aChange floor NPC removed!");
                            return true;
                        } else {
                            p.sendMessage(BBSettings.getPrefix() + " §cThere is no NPC close to your location!");
                        }
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + getName() + " delnpc §8| §7Delete closest Change floor NPC");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(BBSettings.getPrefix() + " §cCitizens plugin is not loaded!");
        }
        return false;
    }

    private boolean forceStartSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, true);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStart(sender, true);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 3) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                String theme = args[2];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, true);
                        return true;
                    } else {
                        sender.sendMessage(BBSettings.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " forcestart  §8| §7Force start arena you are in");
                sender.sendMessage("§cUsage >> /" + getName() + " forcestart <arena> §8| §7Force start specific arena");
                sender.sendMessage("§cUsage >> /" + getName() + " forcestart <arena> <theme> §8| §7Force start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean openReports(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.manage.reports")) {
                if (BuildBattle.getWorldEdit() != null) {
                    ReportManager.getInstance().openReports(p, 1);
                    return true;
                } else {
                    p.sendMessage(BBSettings.getPrefix() + "§cReports are turned off for version 1.13 and above because there is no stable version of WorldEdit.");
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }

    private boolean superVoteSubCommand(CommandSender sender, String[] args) {
        // /bb supervote <give/take> <player> <amount>
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 4) {
                try {
                    String action = args[1].toLowerCase();
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                    int amount = Integer.parseInt(args[3]);
                    switch (action) {
                        case "give":
                            if (SuperVoteManager.getInstance().giveSuperVote(player, amount)) {
                                sender.sendMessage(BBSettings.getPrefix() + " §aYou have successfully given §e" + amount + " §asupervote(s) to player §e" + player.getName() + "§a!");
                                return true;
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        case "take":
                            if (SuperVoteManager.getInstance().takeSuperVote(player, amount)) {
                                sender.sendMessage(BBSettings.getPrefix() + " §aYou have successfully taken §e" + amount + " §asupervote(s) from player §e" + player.getName() + "§a!");
                                return true;
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        default:
                            sender.sendMessage("§e" + args[1] + " §cis not a valid action ! Please use: §7[§egive,take§7]");
                            break;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§e" + args[3] + " §cis not a valid amount !");
                }
            } else {
                sender.sendMessage("§cUsage >> §e/" + getName() + " supervote <give/take> <player> <amount> §8| §7Give/take supervote(s) from/to player");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean pluginInfo(CommandSender sender) {
        sender.sendMessage(BBSettings.getPrefix() + " §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
        sender.sendMessage(BBSettings.getPrefix() + " §aType §e/" + getName() + " help §afor help.");
        return true;
    }

    private boolean versionSubCommand(CommandSender sender) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            sender.sendMessage(BBSettings.getPrefix() + " §aYou are running §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
            return true;
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean openEditor(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.create")) {
                p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                p.playSound(p.getLocation(), CompSound.NOTE_PLING.getSound(), 1.0F, 1.0F);
                return true;
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
        return false;
    }

    private boolean partySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BBSettings.isPartiesEnabled()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        /*case "create":
                            PartyManager.getInstance().createParty(p);
                            break;
                            */
                        case "accept":
                            PartyManager.getInstance().manageInvite(p, true);
                            return true;
                        case "decline":
                            PartyManager.getInstance().manageInvite(p, false);
                            return true;
                        case "leave":
                            PartyManager.getInstance().leaveParty(p);
                            return true;
                        case "invite":
                            if (args.length == 3) {
                                BBParty playerParty = PartyManager.getInstance().getPlayerParty(p);
                                Player target = Bukkit.getPlayer(args[2]);
                                PartyManager.getInstance().invitePlayer(p, target, playerParty);
                                return true;
                            } else {
                                p.sendMessage(Message.PARTY_INVALID_USAGE.getChatMessage());
                            }
                    }
                } else {
                    p.sendMessage("§cInvalid usage!");
                    //p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
                    p.sendMessage("§e/" + getName() + " party invite <player> " + "§8» " + "§7Invite player to your party");
                    p.sendMessage("§e/" + getName() + " party <accept/decline> " + "§8» " + "§7Accept/Decline party invite");
                    p.sendMessage("§e/" + getName() + " party leave " + "§8» " + "§7Leave your current party");
                }
            } else {
                p.sendMessage(Message.PARTIES_NOT_ALLOWED.getChatMessage());
            }
        }
        return false;
    }

    private boolean addFloorNPC(CommandSender sender, String[] args) {
        if (BuildBattle.getInstance().isUseCitizens()) {
            if (sender.hasPermission("buildbattlepro.setup")) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPCRegistry registry = CitizensAPI.getNPCRegistry();
                        NPC npc = registry.createNPC(BBSettings.getFloorChangeNPCtype(), Message.CHANGE_FLOOR_NPC_NAME.getMessage());
                        npc.spawn(p.getLocation());
                        npc.setProtected(true);
                        p.sendMessage(BBSettings.getPrefix() + " §aChange floor NPC spawned!");
                        return true;
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/" + getName() + " addnpc §8| §7Create Change floor NPC at your location");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(BBSettings.getPrefix() + " §cCitizens plugin is not loaded!");
        }
        return false;
    }

    private boolean leaderBoardSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.setup")) {
            if (BuildBattle.getInstance().isUseHolographicDisplays()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        case "refresh":
                            LeaderboardManager.getInstance().refreshAllLeaderBoards();
                            sender.sendMessage(BBSettings.getPrefix() + " §aLeaderboards refreshed !");
                            return true;
                        case "create":
                            if (args.length == 3) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    Location loc = p.getLocation();
                                    try {
                                        LeaderboardType type = LeaderboardType.valueOf(args[2].toUpperCase());
                                        LeaderboardManager.getInstance().createLeaderboard(p, loc, type);
                                        return true;
                                    } catch (Exception e) {
                                        p.sendMessage("§cInvalid type ! Available types :  §e§lWINS, PLAYED, BLOCKS_PLACED, PARTICLES_PLACED");
                                    }
                                }
                            } else {
                                sender.sendMessage("§cUsage >> /" + getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                            }
                            break;
                        case "select":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                LeaderboardManager.getInstance().selectLeaderboard(p);
                                return true;
                            }
                            break;
                        case "delete":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.delete();
                                    p.sendMessage(BBSettings.getPrefix() + " §aHologram at location §e" + LocationUtil.getStringFromLocation(selected.getLocation()) + " §asuccessfully removed!");
                                    return true;
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + " §cPlease select a hologram near you by §e/" + getName() + " lb select §c!");
                                }
                            }
                            break;
                        case "teleport":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.teleport(p.getLocation());
                                    return true;
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + " §cPlease select a hologram near you by §e/" + getName() + " lb select §c!");
                                }
                            }
                            break;
                        default:
                            sender.sendMessage("§cUsages >> §e/" + getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                            sender.sendMessage("         §c>> §e/" + getName() + " lb select §8| §7Select leaderboard closest to you");
                            sender.sendMessage("         §c>> §e/" + getName() + " lb delete §8| §7Deletes selected leaderboard");
                            sender.sendMessage("         §c>> §e/" + getName() + " lb teleport §8| §7Teleports selected leaderboard to your position");
                            sender.sendMessage("         §c>> §e/" + getName() + " lb refresh §8| §7Refresh all leaderboards");
                            break;
                    }
                } else {
                    sender.sendMessage("§cUsages >> §e/" + getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                    sender.sendMessage("         §c>> §e/" + getName() + " lb select §8| §7Select leaderboard closest to you");
                    sender.sendMessage("         §c>> §e/" + getName() + " lb delete §8| §7Deletes selected leaderboard");
                    sender.sendMessage("         §c>> §e/" + getName() + " lb teleport §8| §7Teleports selected leaderboard to your position");
                    sender.sendMessage("         §c>> §e/" + getName() + " lb refresh §8| §7Refresh all leaderboards");
                }
            } else {
                sender.sendMessage(BBSettings.getPrefix() + " §cHolographicDisplays plugin is required to manage leaderboards !");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean exportStatsSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                if (BBSettings.getStatsType() == StatsType.MYSQL) {
                    BuildBattle.info("§cUser §e" + sender.getName() + " §chas requested exporting players stats to MySQL!");
                    BuildBattle.info("§7Starting exporting player stats from stats.yml into MySQL database...");
                    sender.sendMessage(BBSettings.getPrefix() + " §7§oStarting exporting players stats from stats.yml into MySQL database...");
                    int playersTransfered = 0;
                    for (BBPlayerStats stats : PlayerManager.getPlayerStats().values()) {
                        BuildBattle.info("§7Copying data of user §e" + stats.getUuid().toString() + " §7into MySQL");
                        MySQLManager.getInstance().addPlayerToTable(stats);
                        playersTransfered += 1;
                    }
                    BuildBattle.info("§aExport finished. §e" + playersTransfered + "§a players data have been transferred.");
                    sender.sendMessage(BBSettings.getPrefix() + " §2Done! §e" + playersTransfered + "§2 players have been transferred.");
                    return true;
                } else {
                    sender.sendMessage(BBSettings.getPrefix() + " §cTo export data, firstly please enable and setup MySQL!");
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " exportstats §8| §7Export players stats from stats.yml into MySQL");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }


    private boolean startSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.start")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, false);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStart(sender, false);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 3) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                String theme = args[2];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, false);
                        return true;
                    } else {
                        sender.sendMessage(BBSettings.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " start  §8| §7Start arena you are in");
                sender.sendMessage("§cUsage >> /" + getName() + " start <arena> §8| §7Start specific arena");
                sender.sendMessage("§cUsage >> /" + getName() + " start <arena> <theme> §8| §7Start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean stopSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.stop")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStop(sender);
                        return true;
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStop(sender);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " stop §8| §7Force stop arena you are in");
                sender.sendMessage("§cUsage >> /" + getName() + " stop <arena> §8| §7Force stop specified arena");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean sendBBStats(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                BBPlayerStats ps = PlayerManager.getInstance().getPlayerStats(p);
                if (ps != null) {
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.STATS_TITLE.getMessage());
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PLAYED.getMessage().replaceAll("%played%", String.valueOf(ps.getPlayed())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_WINS.getMessage().replaceAll("%wins%", String.valueOf(ps.getWins())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_MOST_POINTS.getMessage().replaceAll("%most_points%", String.valueOf(ps.getMostPoints())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_BLOCKS_PLACED.getMessage().replaceAll("%blocks%", String.valueOf(ps.getBlocksPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PARTICLES_PLACED.getMessage().replaceAll("%particles%", String.valueOf(ps.getParticlesPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_SUPER_VOTES.getMessage().replaceAll("%super_votes%", String.valueOf(ps.getSuperVotes())));
                    p.sendMessage("");
                    FancyMessage.sendCenteredMessage(p, Message.LINE_SPACER.getMessage());
                    return true;
                } else {
                    p.sendMessage(Message.NOT_PLAYED.getChatMessage());
                }
            }
        } else {
            sender.sendMessage("§cUsage >> /" + getName() + " stats §8| §7Show your BuildBattlePro stats");
        }
        return false;
    }

    private boolean delArenaSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    ArenaManager.getInstance().removeArena(sender, arena);
                    return true;
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " delete <arena> §8| §7Deletes arena.");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }


    private boolean delPlotSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena a = ArenaManager.getInstance().getArena(args[1]);
                    if (a != null) {
                        int lastIndex = a.getBuildPlots().size() - 1;
                        if (lastIndex < 0) {
                            p.sendMessage("§e§lBuildBattle Setup §8| §cArena §e" + a.getName() + " §chas no build plots ! Create some !");
                        } else {
                            a.getBuildPlots().remove(lastIndex);
                            a.saveIntoConfig();
                            ArenaManager.getInstance().refreshArenaItem(a);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully removed plot §e" + (lastIndex + 1) + " §afrom arena §e" + a.getName() + " §a!");
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + getName() + " delplot <arena> §8| §7Deletes plot at your current location ");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean createSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                if (args.length == 3) {
                    String arenaName = args[1];
                    String gameMode = args[2];
                    ArenaManager.getInstance().createArena(sender, arenaName, gameMode);
                    return true;
                } else {
                    sender.sendMessage("§cUsage >> /" + getName() + " create <name> <solo/team> §8| §7Create an buildbattle arena");
                }
            }
        }
        return false;
    }

    private boolean addPlotSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[1]);
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
                                    p.sendMessage(BBSettings.getPrefix() + "§cYou didn't set positions ! Please set them by §e/" + getName() + " pos");
                                    break;
                                case 1:
                                case 2:
                                    p.sendMessage(BBSettings.getPrefix() + "§cYou didn't set position §e" + i + " §c! Set it by §e/" + getName() + " pos");
                                    break;
                            }
                        }
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + getName() + " addplot <arena> §8| §7Add a build plot to arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean setLobbySubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                    Location playerLoc = p.getLocation();
                    if (arena != null) {
                        arena.setLobbyLocation(playerLoc);
                        p.sendMessage("§e§lBuildBattle Setup §8| §aLobby Location for arena §e" + arena.getName() + " §aset !");
                        return true;
                    } else {
                        p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[1]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + getName() + " setlobby <arena> §8| §7Set Lobby location for arena");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }

    private boolean leaveSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null) {
                arena.removePlayer(p);
                return true;
            } else {
                p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
            }
        }
        return false;
    }

    private boolean listArenasSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                p.openInventory(ArenaManager.getAllArenasInventory());
                return true;
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("solo")) {
                    p.openInventory(ArenaManager.getSoloArenasInventory());
                    return true;
                } else if (args[1].equalsIgnoreCase("team")) {
                    p.openInventory(ArenaManager.getTeamArenasInventory());
                    return true;
                } else {
                    sender.sendMessage("§cUsage >> /" + getName() + " list <solo/team> §8| §7Show all team/solo arenas and their status");
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " list §8| §7Show all arenas and their status");
                sender.sendMessage("§cUsage >> /" + getName() + " list <solo/team> §8| §7Show all team/solo arenas and their status");
            }
        }
        return false;
    }

    private boolean reloadSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                BuildBattle.getInstance().reloadPlugin();
                sender.sendMessage(BBSettings.getPrefix() + " §aPlugin reloaded !");
                return true;
            } else {
                sender.sendMessage("§cUsage >> /" + getName() +" reload §8| §7Reloads plugin");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
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
            p.sendMessage("§e/"+ getName() + " exportstats " + "§8» " + "§7Export players stats from stats.yml into MySQL");
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

    private boolean joinSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("solo")) {
                    BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.SOLO);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (arena != null) {
                        if (playerArena == null) {
                            arena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                        }
                    } else {
                        p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    }
                } else if (args[1].equalsIgnoreCase("team")) {
                    BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.TEAM);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (arena != null) {
                        if (playerArena == null) {
                            arena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                        }
                    } else {
                        p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    }
                } else {
                    BBArena argArena = ArenaManager.getInstance().getArena(args[1]);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (argArena != null) {
                        if (playerArena == null) {
                            argArena.addPlayer(p);
                            return true;
                        } else {
                            p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage().replaceAll("%arena%", playerArena.getName()));
                        }
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else {
                BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(null);
                BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                if (arena != null) {
                    if (playerArena == null) {
                        arena.addPlayer(p);
                        return true;
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                }
            }
        }
        return false;
    }

    private boolean setMainLobbySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (p.hasPermission("buildbattlepro.create")) {
                    BBSettings.setMainLobbyLocation(p);
                    return true;
                } else {
                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /" + getName() + " setmainlobby §8| §7Show the main lobby location");
            }
        }
        return false;
    }
}
