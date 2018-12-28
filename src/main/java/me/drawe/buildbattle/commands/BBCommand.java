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
import me.kangarko.compatbridge.model.CompSound;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("buildbattle")) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "debug":
                        debugSubCommand(sender);
                        break;
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
                    case "setmainlobby":
                        setMainLobbySubCommand(sender, args);
                        break;
                    case "delplot":
                        delPlotSubCommand(sender, args);
                        break;
                    case "supervote":
                        superVoteSubCommand(sender, args);
                        break;
                    case "start":
                        startSubCommand(sender, args);
                        break;
                    case "stop":
                        stopSubCommand(sender, args);
                        break;
                    case "forcestart":
                        forceStartSubCommand(sender, args);
                        break;
                    case "stats":
                        sendBBStats(sender, args);
                        break;
                    case "reload":
                        reloadSubCommand(sender, args);
                        break;
                    case "list":
                        listArenasSubCommand(sender, args);
                        break;
                    case "help":
                        commandUsage(sender);
                        break;
                    case "exportstats":
                        exportStatsSubCommand(sender, args);
                        break;
                    case "lb":
                        leaderBoardSubCommand(sender, args);
                        break;
                    case "version":
                        versionSubCommand(sender);
                        break;
                    case "leaderboard":
                        leaderBoardSubCommand(sender, args);
                        break;
                    case "addnpc":
                        addFloorNPC(sender, args);
                        break;
                    case "delnpc":
                        delFloorNPC(sender, args);
                        break;
                    case "party":
                        partySubCommand(sender, args);
                        break;
                    case "editor":
                        openEditor(sender);
                        break;
                    case "pos":
                        posSubCommand(sender);
                        break;
                    case "reports":
                        openReports(sender);
                        break;
                    default:
                        pluginInfo(sender);
                        break;

                }
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    p.openInventory(OptionsManager.getAllArenasInventory());
                }
            }
        }
        return true;
    }

    private void debugSubCommand(CommandSender sender) {
        if (sender.isOp()) {
            sender.sendMessage(GameManager.getPrefix() + "§aDebug mode >> §e" + BuildBattle.enableDebugMode());
        }
    }

    private void posSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sender.hasPermission("buildbattlepro.create")) {
                p.getInventory().addItem(ArenaManager.getPosSelectorItem());
                sender.sendMessage(GameManager.getPrefix() + " §aYou were given §ePlot Selector §a!");
                sender.sendMessage(GameManager.getPrefix() + " §eLeft-Click §ablock to selection §ePostion 1");
                sender.sendMessage(GameManager.getPrefix() + " §eRight-Click §ablock to selection §ePostion 2");
                return;
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
    }

    private void delFloorNPC(CommandSender sender, String[] args) {
        if (BuildBattle.getInstance().isUseCitizens()) {
            if (sender.hasPermission("buildbattlepro.setup")) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        NPC npc = LocationUtil.getClosestNPC(p);
                        if (npc != null) {
                            npc.destroy();
                            p.sendMessage(GameManager.getPrefix() + " §aChange floor NPC removed!");
                        } else {
                            p.sendMessage(GameManager.getPrefix() + " §cThere is no NPC close to your location!");
                        }
                    }
                } else {
                    sender.sendMessage("§cUsage >> §e/bb delnpc §8| §7Delete closest Change floor NPC");
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(GameManager.getPrefix() + " §cCitizens plugin is not loaded!");
        }
    }

    private void forceStartSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, true);
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStart(sender, true);
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 3) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                String theme = args[2];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, true);
                    } else {
                        sender.sendMessage(GameManager.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /bb forcestart  §8| §7Force start arena you are in");
                sender.sendMessage("§cUsage >> /bb forcestart <arena> §8| §7Force start specific arena");
                sender.sendMessage("§cUsage >> /bb forcestart <arena> <theme> §8| §7Force start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }

    }

    private void openReports(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.manage.reports")) {
                if(BuildBattle.getWorldEdit() != null) {
                    ReportManager.getInstance().openReports(p, 1);
                } else {
                    p.sendMessage(GameManager.getPrefix() + "§cReports are turned off for version 1.13 and above because there is no stable version of WorldEdit.");
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
    }

    private void superVoteSubCommand(CommandSender sender, String[] args) {
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
                                sender.sendMessage(GameManager.getPrefix() + " §aYou have successfully given §e" + amount + " §asupervote(s) to player §e" + player.getName() + "§a!");
                            } else {
                                sender.sendMessage("§cThis player has never player BuildBattlePro ! Can't add supervote(s) !");
                            }
                            break;
                        case "take":
                            if (SuperVoteManager.getInstance().takeSuperVote(player, amount)) {
                                sender.sendMessage(GameManager.getPrefix() + " §aYou have successfully taken §e" + amount + " §asupervote(s) from player §e" + player.getName() + "§a!");
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
                sender.sendMessage("§cUsage >> §e/bb supervote <give/take> <player> <amount> §8| §7Give/take supervote(s) from/to player");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }

    }

    private void pluginInfo(CommandSender sender) {
        sender.sendMessage(GameManager.getPrefix() + " §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
        sender.sendMessage(GameManager.getPrefix() + " §aType §e/bb help §afor help.");
    }

    private void versionSubCommand(CommandSender sender) {
        if (sender.hasPermission("buildbattlepro.admin")) {
            sender.sendMessage(GameManager.getPrefix() + " §aYou are running §e" + BuildBattle.getInstance().getDescription().getName() + " v." + BuildBattle.getInstance().getDescription().getVersion() + "§a by §eDrawethree.");
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void openEditor(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("buildbattlepro.create")) {
                p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                p.playSound(p.getLocation(), CompSound.NOTE_PLING.getSound(), 1.0F, 1.0F);
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        }
    }

    private void partySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (GameManager.isPartiesEnabled()) {
                if (args.length > 1) {
                    String subCommand = args[1].toLowerCase();
                    switch (subCommand) {
                        /*case "create":
                            PartyManager.getInstance().createParty(p);
                            break;
                            */
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
                    //p.sendMessage("§e/bb party create " + "§8» " + "§7Create party");
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
        if (BuildBattle.getInstance().isUseCitizens()) {
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
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }
        } else {
            sender.sendMessage(GameManager.getPrefix() + " §cCitizens plugin is not loaded!");
        }
    }

    private void leaderBoardSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.setup")) {
            if (BuildBattle.getInstance().isUseHolographicDisplays()) {
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
        if (sender.hasPermission("buildbattlepro.admin")) {
            if (args.length == 1) {
                if (GameManager.getStatsType() == StatsType.MYSQL) {
                    BuildBattle.info("§cUser §e" + sender.getName() + " §chas requested exporting players stats to MySQL!");
                    BuildBattle.info("§7Starting exporting player stats from stats.yml into MySQL database...");
                    sender.sendMessage(GameManager.getPrefix() + " §7§oStarting exporting players stats from stats.yml into MySQL database...");
                    int playersTransfered = 0;
                    for (BBPlayerStats stats : PlayerManager.getPlayerStats()) {
                        BuildBattle.info("§7Copying data of user §e" + stats.getUuid().toString() + " §7into MySQL");
                        MySQLManager.getInstance().addPlayerToTable(stats);
                        playersTransfered += 1;
                    }
                    BuildBattle.info("§aExport finished. §e" + playersTransfered + "§a players data have been transferred.");
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
        if (sender.hasPermission("buildbattlepro.start")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStart(sender, false);
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
                    arena.forceStart(sender, false);
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else if (args.length == 3) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                String theme = args[2];
                if (arena != null) {
                    if (theme != null) {
                        arena.forceStart(sender, theme, false);
                    } else {
                        sender.sendMessage(GameManager.getPrefix() + "§cYou must specify theme !");
                    }
                } else {
                    sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /bb start  §8| §7Start arena you are in");
                sender.sendMessage("§cUsage >> /bb start <arena> §8| §7Start specific arena");
                sender.sendMessage("§cUsage >> /bb start <arena> <theme> §8| §7Start specific arena with specific theme");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
    }

    private void stopSubCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("buildbattlepro.stop")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                    if (a != null) {
                        a.forceStop(sender);
                    } else {
                        p.sendMessage(Message.NOT_IN_ARENA.getChatMessage());
                    }
                }
            } else if (args.length == 2) {
                BBArena arena = ArenaManager.getInstance().getArena(args[1]);
                if (arena != null) {
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
                    FancyMessage.sendCenteredMessage(p, Message.STATS_TITLE.getMessage());
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PLAYED.getMessage().replaceAll("%played%", String.valueOf(ps.getPlayed())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_WINS.getMessage().replaceAll("%wins%", String.valueOf(ps.getWins())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_MOST_POINTS.getMessage().replaceAll("%most_points%", String.valueOf(ps.getMostPoints())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_BLOCKS_PLACED.getMessage().replaceAll("%blocks%", String.valueOf(ps.getBlocksPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_PARTICLES_PLACED.getMessage().replaceAll("%particles%", String.valueOf(ps.getParticlesPlaced())));
                    FancyMessage.sendCenteredMessage(p, Message.STATS_SUPER_VOTES.getMessage().replaceAll("%super_votes%", String.valueOf(ps.getSuperVotes())));
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
        if (sender.hasPermission("buildbattlepro.create")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    BBArena a = ArenaManager.getInstance().getArena(args[1]);
                    if (a != null) {
                        int lastIndex = a.getBuildPlots().size() - 1;
                        if (lastIndex < 0) {
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
                            BBPlot newPlot = new BBPlot(arena, minPoint, maxPoint);
                            newPlot.addIntoArenaPlots();
                            arena.saveIntoConfig();
                            OptionsManager.getInstance().refreshArenaItem(arena);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aPlot for arena §e" + arena.getName() + " §aadded !");
                            LocationUtil.showCreatedPlot(minPoint, maxPoint, p, 5);
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
                                    p.sendMessage(GameManager.getPrefix() + "§cYou didn't set positions ! Please set them by §e/bb pos");
                                    break;
                                case 1:
                                case 2:
                                    p.sendMessage(GameManager.getPrefix() + "§cYou didn't set position §e" + i + " §c! Set it by §e/bb pos");
                                    break;
                            }
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
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                p.openInventory(OptionsManager.getAllArenasInventory());
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("solo")) {
                    p.openInventory(OptionsManager.getSoloArenasInventory());
                } else if (args[1].equalsIgnoreCase("team")) {
                    p.openInventory(OptionsManager.getTeamArenasInventory());
                } else {
                    sender.sendMessage("§cUsage >> /bb list <solo/team> §8| §7Show all team/solo arenas and their status");
                }
            } else {
                sender.sendMessage("§cUsage >> /bb list §8| §7Show all arenas and their status");
                sender.sendMessage("§cUsage >> /bb list <solo/team> §8| §7Show all team/solo arenas and their status");
            }
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
            p.sendMessage("§c§lNEW §e/bb pos " + "§8» " + "§7Gives you item to make selection of plot");
            p.sendMessage("§e/bb create <arena_name> <solo/team> " + "§8» " + "§7Create Arena");
            p.sendMessage("§e/bb delete <arena_name> " + "§8» " + "§7Remove Arena");
            p.sendMessage("§e/bb addplot <arena_name> " + "§8» " + "§7Add build plot for arena, must have selected positions !");
            p.sendMessage("§e/bb delplot <arena> " + "§8» " + "§7Removes latest added plot in arena");
            p.sendMessage("§e/bb setlobby <arena> " + "§8» " + "§7Set lobby for Arena");
            p.sendMessage("§e/bb setmainlobby " + "§e» " + "§7Set main lobby");
            p.sendMessage("§e/bb forcestart " + "§8» " + "§7Force start Arena you are currently in");
            p.sendMessage("§e/bb forcestart <arena> " + "§8» " + "§7Force start Arena");
            p.sendMessage("§e/bb forcestart <arena> <theme> " + "§8» " + "§7Force start Arena with specified theme");
            p.sendMessage("§e/bb start " + "§8» " + "§7Start Arena you are currently in");
            p.sendMessage("§e/bb start <arena> " + "§8» " + "§7Start Arena");
            p.sendMessage("§e/bb start <arena> <theme> " + "§8» " + "§7Start Arena with specified theme");
            p.sendMessage("§e/bb stop " + "§8» " + "§7Force stop Arena you are currently in");
            p.sendMessage("§e/bb stop <arena> " + "§8» " + "§7Force stop Arena");
            p.sendMessage("§e/bb reload " + "§8» " + "§7Reload plugin");
            p.sendMessage("§e/bb editor " + "§8» " + "§7Open arena editor");
            p.sendMessage("§e/bb lb create <type> " + "§8» " + "§7Create leaderboard with specified type");
            p.sendMessage("§e/bb lb select " + "§8» " + "§7Select leaderboard closest to you");
            p.sendMessage("§e/bb lb delete " + "§8» " + "§7Deletes selected leaderboard");
            p.sendMessage("§e/bb lb teleport " + "§8» " + "§7Teleports selected leaderboard to your position");
            p.sendMessage("§e/bb lb refresh " + "§8» " + "§7Refresh all leaderboards");
            p.sendMessage("§e/bb supervote <give/take> <player> <amount> " + "§8» " + "§7Give/take supervotes from player");
            p.sendMessage("§e/settheme <theme> " + "§8» " + "§7Force-set theme for your current arena");
            p.sendMessage("§e/bb exportstats " + "§8» " + "§7Export players stats from stats.yml into MySQL");
            FancyMessage.sendCenteredMessage(p, "§6✪§e§lBuildBattlePro§6✪ §8- §6Player Commands");
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
            FancyMessage.sendCenteredMessage(p, "§6✪ §e§lBuildBattlePro §6✪ §8- §6Player Commands");
            p.sendMessage("§e/bb join " + "§8» " + "§7Automatic join to first available Arena");
            p.sendMessage("§e/bb join <arena> " + "§8» " + "§7Join specific Arena");
            p.sendMessage("§e/bb join <team/solo> " + "§8» " + "§7Automatic join to first available solo/team arena");
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
                if (args[1].equalsIgnoreCase("solo")) {
                    BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.SOLO);
                    BBArena playerArena = PlayerManager.getInstance().getPlayerArena(p);
                    if (arena != null) {
                        if (playerArena == null) {
                            arena.addPlayer(p);
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
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                }
            }
        }
    }

    private void setMainLobbySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (p.hasPermission("buildbattlepro.create")) {
                    GameManager.getInstance().setMainLobbyLocation(p);
                } else {
                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
            } else {
                sender.sendMessage("§cUsage >> /bb setmainlobby §8| §7Show the main lobby location");
            }
        }
    }
}
