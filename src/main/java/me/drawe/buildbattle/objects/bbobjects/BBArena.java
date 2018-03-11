package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.events.BBGameEndEvent;
import me.drawe.buildbattle.events.BBGameStartEvent;
import me.drawe.buildbattle.events.BBGameStateSwitchEvent;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BBArena {
    private String name;
    private int min_players;
    private int gameTime;
    private Location lobbyLocation;
    private List<Player> players;
    private List<BBTeam> teams;
    private List<BBPlot> buildPlots;
    private List<BBPlot> votingPlots;
    private List<BBSign> arenaSigns;
    private List<BBBoard> playerBoards;
    private String theme;
    private BBPlot winner;
    private BBPlot currentVotingPlot;
    private BBGameMode gameMode;
    private int teamSize;
    private BBThemeVoting themeVoting;
    private BBArenaState bbArenaState;
    private BukkitTask lobbyCountdown;
    private BukkitTask gameCountdown;
    private BukkitTask votingCountdown;
    private BukkitTask endCountdown;
    private BukkitTask themeVotingCountdown;
    private Inventory teamsInventory;

    //LOADING
    public BBArena(String name, int min_players, int playTime, BBGameMode gameMode, int teamSize, Location lobbyLocation, List<Player> players, List<BBPlot> buildPlots, List<BBSign> signs) {
        this.name = name;
        this.min_players = min_players;
        this.gameTime = playTime;
        this.gameMode = gameMode;
        this.teamSize = teamSize;
        this.lobbyLocation = lobbyLocation;
        this.players = players;
        this.buildPlots = buildPlots;
        this.arenaSigns = signs;
        this.theme = null;
        this.winner = null;
        this.currentVotingPlot = null;
        this.themeVoting = new BBThemeVoting(this);
        this.bbArenaState = BBArenaState.LOBBY;
        this.playerBoards = new ArrayList<>();
        this.teams = new ArrayList<>();
        addIntoAllArenas();
    }

    //CREATING
    public BBArena(String name, BBGameMode gameMode) {
        this.name = name;
        this.min_players = 2;
        this.gameTime = GameManager.getDefaultGameTime();
        this.gameMode = gameMode;
        this.players = new ArrayList<>();
        this.buildPlots = new ArrayList<>();
        this.arenaSigns = new ArrayList<>();
        this.playerBoards = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.theme = null;
        this.winner = null;
        this.currentVotingPlot = null;
        this.themeVoting = new BBThemeVoting(this);
        this.bbArenaState = BBArenaState.LOBBY;
        if(gameMode == BBGameMode.SOLO) {
            this.teamSize = 1;
        } else if(gameMode == BBGameMode.TEAM) {
            this.teamSize = 2;
        }
        setupTeams();
        setupTeamInventory();
        addIntoAllArenas();
        OptionsManager.getInstance().refreshAllArenasInventory();
        saveIntoConfig();
        ArenaManager.getInstance().loadArenaEditors();
    }

    public List<BBPlot> getBuildPlots() {
        return buildPlots;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player p) {
        if (getBBArenaState() == BBArenaState.LOBBY) {
            if (getPlayers().size() < getMaxPlayers()) {
                try {
                    joinCommands(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(Message.UNABLE_TO_JOIN.getChatMessage());
                }
                if (getPlayers().size() == getMinPlayers()) {
                    startLobby();
                }
            } else {
                if(p.hasPermission("buildbattlepro.joinfull")) {
                    try {
                        joinVIP(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                        p.sendMessage(Message.UNABLE_TO_JOIN.getChatMessage());
                    }
                } else {
                    p.sendMessage(Message.ARENA_FULL.getChatMessage());
                }
            }
        } else {
            p.sendMessage(Message.ARENA_ALREADY_STARTED.getChatMessage());
        }
    }

    protected void leaveCommands(Player p) {
        getPlayers().remove(p);
        getPlayerBoard(p).removeBoard();
        BBTeam team = PlayerManager.getInstance().getPlayerTeam(this,p);
        if(team != null) {
            team.leaveTeam(p);
        }
        if (GameManager.isScoreboardEnabled()) {
            PlayerManager.getInstance().removeScoreboard(p);
        }
        PlayerManager.getInstance().restorePlayerData(p);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        if (BuildBattle.getInstance().isUseBungeecord()) {
            BungeeUtils.connectPlayerToServer(p, GameManager.getInstance().getRandomFallbackServer());
            if(p.isOnline()) {
                p.kickPlayer("");
            }
        }
        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.PLAYER_LEFT.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));
        if(getBBArenaState() == BBArenaState.LOBBY) {
            if(getPlayers().size() < getMinPlayers()) {
                stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
            }
        } else {
            if(getPlayers().size() <= getTeamSize()) {
                stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
            }
        }
    }

    protected void joinCommands(Player p) {
            BBParty party = PartyManager.getInstance().getPlayerParty(p);
            if ((party != null) && (party.isCreator(p))) {
                party.joinGame(this);
            }
            PlayerManager.getInstance().createNewPlayerData(p);
            PlayerManager.getInstance().createPlayerStatsIfNotExists(p);
            p.getInventory().clear();
            p.setExp(0F);
            p.setLevel(0);
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setGameMode(GameMode.ADVENTURE);
            getPlayers().add(p);
            getPlayerBoards().add(new BBBoard(this, p));
            p.teleport(getLobbyLocation().clone().add(0, 0.5, 0));
            if (getGameType() == BBGameMode.TEAM) {
                p.getInventory().setItem(0, OptionsManager.getTeamsItem());
            }
            p.getInventory().setItem(8, OptionsManager.getLeaveItem());
            if (GameManager.isScoreboardEnabled()) {
                if (lobbyCountdown == null) {
                    updateAllScoreboards(0);
                } else {
                    if (!Bukkit.getScheduler().isCurrentlyRunning(lobbyCountdown.getTaskId())) {
                        updateAllScoreboards(0);
                    }
                }
            }
            PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.PLAYER_JOINED.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));
            OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
            updateAllSigns();
    }

    public BBTeam getFreeBBTeamForParty(BBParty party) {
        int amountOfPlayers = party.getPlayers().size();
        for(BBTeam team : getTeams()) {
            if(team.getLeftSlots() >= amountOfPlayers) {
                return team;
            }
        }
        return null;
    }
    public void removePlayer(Player p) {
        leaveCommands(p);
    }

    public void startLobby() {
        setBBArenaState(BBArenaState.LOBBY);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        lobbyCountdown = new BukkitRunnable() {
            int countdown = GameManager.getLobbyTime();

            @Override
            public void run() {
                if (isMinimumPlayersRequirementMet()) {
                    if (countdown == 0) {
                        if (GameManager.isVotingForThemes()) {
                            startThemeVoting();
                        } else {
                            switch (getGameType()) {
                                case SOLO:
                                    startGame(GameManager.getRandomSoloTheme(), true);
                                    break;
                                case TEAM:
                                    startGame(GameManager.getRandomTeamTheme(), true);
                                    break;
                            }
                        }
                        cancel();
                        return;
                    } else if (countdown % 15 == 0) {
                        PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.ORB_PICKUP.getSound());
                        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.GAME_STARTS_IN.getChatMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                    } else if (countdown < 6) {
                        PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.ORB_PICKUP.getSound());
                        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.GAME_STARTS_IN.getChatMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                    }
                } else {
                    PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.NOT_ENOUGH_PLAYERS.getChatMessage());
                    if (GameManager.isScoreboardEnabled()) {
                        updateAllScoreboards(0);
                    }
                    lobbyCountdown.cancel();
                    return;
                }
                if (GameManager.isScoreboardEnabled()) {
                    updateAllScoreboards(countdown);
                }
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);
    }

    public void startThemeVoting() {
        setBBArenaState(BBArenaState.THEME_VOTING);
        updateAllScoreboards(0);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        setPlotsToTeams();
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        getThemeVoting().updateVoting();
        for (Player p : getPlayers()) {
            p.openInventory(getThemeVoting().getVoteInventory());
        }
        themeVotingCountdown = new BukkitRunnable() {
            @Override
            public void run() {
                getThemeVoting().setWinner();
                startGame();
                for (Player p : getPlayers()) {
                    p.closeInventory();
                }
            }
        }.runTaskLater(BuildBattle.getInstance(), (long) (GameManager.getThemeVotingTime() * 20L));
    }

    public void resetAllScoreboards() {
        for(BBBoard board : getPlayerBoards()) {
            board.reset();
            board.update();
        }
    }

    public void startGame() {
        setBBArenaState(BBArenaState.INGAME);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        setTheme(getThemeVoting().getWinner().getName());
        setVotingPlots();
        PlayerManager.getInstance().closeInventoryAllPlayersInArena(getArenaInstance());
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        PlayerManager.getInstance().addPlayedToAllPlayers(getArenaInstance());
        OptionsManager.getInstance().giveAllPlayersItem(getArenaInstance(), OptionsManager.getOptionsItem());
        PlayerManager.getInstance().sendStartMessageToAllPlayers(getArenaInstance());

        gameCountdown = new BukkitRunnable() {
            int countdown = getGameTime();

            @Override
            public void run() {
                if (countdown == 0) {
                    if (GameManager.isScoreboardEnabled()) {
                        updateAllScoreboards(countdown);
                    }
                    startVotingCountdown();
                    cancel();
                    return;
                } else if (countdown % 60 == 0 && countdown >= 60 && countdown != getGameTime()) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                } else if (countdown % 30 == 0 && countdown < 60) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                } else if (countdown < 11) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                }
                if (GameManager.isScoreboardEnabled()) {
                    updateAllScoreboards(countdown);
                }
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);
        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameStartEvent(getArenaInstance()));
    }

    public void startGame(String theme, boolean fromLobby) {
        if (lobbyCountdown != null) {
            lobbyCountdown.cancel();
        }
        if (themeVotingCountdown != null) {
            themeVotingCountdown.cancel();
        }
        setBBArenaState(BBArenaState.INGAME);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        setTheme(theme);
        if (fromLobby) {
            setPlotsToTeams();
        }
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        setVotingPlots();
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        PlayerManager.getInstance().addPlayedToAllPlayers(getArenaInstance());
        OptionsManager.getInstance().giveAllPlayersItem(getArenaInstance(), OptionsManager.getOptionsItem());
        PlayerManager.getInstance().sendStartMessageToAllPlayers(getArenaInstance());

        gameCountdown = new BukkitRunnable() {
            int countdown = getGameTime();

            @Override
            public void run() {
                if (countdown == 0) {
                    if (GameManager.isScoreboardEnabled()) {
                        updateAllScoreboards(countdown);
                    }
                    startVotingCountdown();
                    cancel();
                    return;
                } else if (countdown % 60 == 0 && countdown >= 60 && countdown != getGameTime()) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                } else if (countdown % 30 == 0 && countdown < 60) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                } else if (countdown < 11) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                }
                if (GameManager.isScoreboardEnabled()) {
                    updateAllScoreboards(countdown);
                }
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);
        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameStartEvent(getArenaInstance()));
    }

    public List<BBPlot> getVotingPlots() {
        return votingPlots;
    }

    public void endGame() {
        setBBArenaState(BBArenaState.ENDING);
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        calculateResults();
        teleportAllPlayersToPlot(getWinner());
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        spawnWinnerFireworks();
        PlayerManager.getInstance().addWinsToWinner(getArenaInstance());
        PlayerManager.getInstance().sendResultsToAllPlayers(getArenaInstance());
        PlayerManager.getInstance().setAllPlayersMostPoints(getArenaInstance());
        if (GameManager.isEndCommandValid()) {
            for(Player p : getWinner().getTeam().getPlayers()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), GameManager.getEndCommand().replaceAll("%winner%", p.getName()));
            }
        }
        RewardManager.getInstance().giveRewards(this);
        ArenaManager.setTotalPlayedGames(ArenaManager.getTotalPlayedGames() + 1);
        endCountdown = new BukkitRunnable() {

            @Override
            public void run() {
                stopArena(Message.ARENA_ENDED.getChatMessage(), false);
            }
        }.runTaskLater(BuildBattle.getInstance(), GameManager.getEndTime() * 20L);

        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameEndEvent(getArenaInstance()));
    }

    public String getTotalPlayers() {
        return getPlayers().size() + "/" + getMaxPlayers();
    }


    public void startVotingCountdown() {
        setBBArenaState(BBArenaState.VOTING);
        PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), Message.TITLE_VOTING.getMessage(), Message.SUBTITLE_VOTING.getMessage());
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        setGamemodeToAllPlayers(GameMode.SURVIVAL);
        PlayerManager.getInstance().setAllPlayersFlying(getArenaInstance());
        PlayerManager.getInstance().removeAllPotionEffects(getArenaInstance());
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
        updateAllSigns();
        setVotingPlots();
        if (getVotingPlots() == null) {
            stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
        } else {
            setCurrentVotingPlot(getVotingPlots().get(0));
            votingCountdown = new BukkitRunnable() {
                double timeLeft = GameManager.getVotingTime();
                int index = 0;

                @Override
                public void run() {
                    if (timeLeft == GameManager.getVotingTime()) {
                        teleportAllPlayersToPlot(getCurrentVotingPlot());
                        updateAllScoreboards(0);
                        PlayerManager.getInstance().giveVoteItemsAllPlayers(getArenaInstance());
                    }
                    if (timeLeft == 0) {
                        getCurrentVotingPlot().setFinalPoints();
                        try {
                            index += 1;
                            setCurrentVotingPlot(getVotingPlots().get(index));
                            timeLeft = GameManager.getVotingTime();
                            return;
                        } catch (Exception e) {
                            cancel();
                            endGame();
                            return;
                        }
                    } else if (timeLeft < 6) {
                        PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), Sounds.CLICK.getSound());
                    }
                    PlayerManager.getInstance().setLevelsToAllPlayers(getArenaInstance(), (int) timeLeft);
                    PlayerManager.getInstance().sendActionBarToAllPlayers(getArenaInstance(), Message.VOTE_TIME.getMessage().replaceAll("%time%", new Time((int) timeLeft, TimeUnit.SECONDS).toString()));
                    timeLeft -= 0.5;
                }
            }.runTaskTimer(BuildBattle.getInstance(), 100L, 10L);
        }
    }

    public void teleportAllPlayersToPlot(BBPlot plot) {
        for (Player p : getPlayers()) {
            p.setAllowFlight(true);
            p.setFlying(true);
            p.teleport(plot.getTeleportLocation());
            p.setPlayerTime(plot.getOptions().getCurrentTime().getTime(), false);
            p.setPlayerWeather(plot.getOptions().getCurrentWeather());
        }
        if(plot.getTeam().getPlayers().size() == 1) {
            PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), Message.VOTING_BUILDER.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        } else if(plot.getTeam().getPlayers().size() == 2) {
            PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), Message.VOTING_BUILDERS.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        }
    }

    public boolean isFull() {
        return getPlayers().size() == getMaxPlayers();
    }

    public void stopArena(String message, boolean forced) {
        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), message);
        switch (getBBArenaState()) {
            case LOBBY:
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                    updateAllScoreboards(0);
                    return;
                }
                break;
            case THEME_VOTING:
                if (themeVotingCountdown != null) {
                    themeVotingCountdown.cancel();
                }
                break;
            case INGAME:
                gameCountdown.cancel();
                break;
            case VOTING:
                votingCountdown.cancel();
                break;
            case ENDING:
                endCountdown.cancel();
                break;
            default:
                break;
        }
        setWinner(null);
        setCurrentVotingPlot(null);
        setTheme(null);
        if (GameManager.isVotingForThemes()) {
            getThemeVoting().setVotedPlayers(new HashMap<>());
            getThemeVoting().setThemesVoted(getThemeVoting().getRandomThemesToVote());
            getThemeVoting().resetInventory();
        }
        ArenaManager.getInstance().resetAllPlots(getArenaInstance());
        resetAllTeams();
        setBBArenaState(BBArenaState.LOBBY);
        if (forced || GameManager.isRemovePlayersAfterGame()) {
            kickAllPlayers();
        } else {
            if (GameManager.isScoreboardEnabled()) {
                updateAllScoreboards(0);
            }
            PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
            PlayerManager.getInstance().teleportAllPlayersToLobby(getArenaInstance());
            setGamemodeToAllPlayers(GameMode.ADVENTURE);
            if(getGameType() == BBGameMode.TEAM) {
                PlayerManager.getInstance().giveAllPlayersTeamsItem(getArenaInstance());
            }
            PlayerManager.getInstance().giveAllPlayersLeaveItem(getArenaInstance());
            if (getPlayers().size() >= getMinPlayers()) {
                startLobby();
            }
        }
        updateAllSigns();
        OptionsManager.getInstance().refreshArenaItem(getArenaInstance());
    }

    private void resetAllTeams() {
        for(BBTeam t : getTeams()) {
            t.resetTeam();
        }
    }

    public void setPlotsToTeams() {
        for(Player p : getPlayers()) {
            if(getPlayerTeam(p) == null) {
                autoAssignTeamToPlayer(p);
            }
        }
        List<BBTeam> teams = new ArrayList<>(getValidTeams());
        for (BBPlot plot : getBuildPlots()) {
            if (!teams.isEmpty()) {
                plot.setTeam(teams.get(0));
                plot.teleportTeamToPlot(plot.getTeam());
                plot.getOptions().setCurrentTime(BBPlotTime.NOON, false);
            } else {
                break;
            }
            teams.remove(0);
        }
    }

    private void autoAssignTeamToPlayer(Player p) {
        for(BBTeam team : getTeams()) {
            if(!team.isFull()) {
                team.getPlayers().add(p);
                break;
            }
        }
    }

    /*
    public void setPlotsToPlayers() {
        List<Player> players = new ArrayList<>(getPlayers());
        for (BBPlot plot : getBuildPlots()) {
            if (!players.isEmpty()) {
                plot.setPlayer(players.get(0));
                players.get(0).teleport(plot.getTeleportExactCenterLocation());
                plot.getOptions().setCurrentTime(BBPlotTime.NOON, false);
            } else {
                break;
            }
            players.remove(0);
        }
    }
    */

    public void joinVIP(Player vip) {
        Player lastNonVIPPlayer = getLastNonVipPlayer();
        if(lastNonVIPPlayer != null) {
            removePlayer(lastNonVIPPlayer);
            lastNonVIPPlayer.sendMessage(Message.KICKED_DUE_TO_VIP_JOIN.getChatMessage().replaceAll("%player%", vip.getDisplayName()));
            joinCommands(vip);
            if (getPlayers().size() == getMinPlayers()) {
                startLobby();
            }
        } else {
            vip.sendMessage(Message.NO_VIP_SLOT_FREE.getChatMessage());
        }
    }

    public void kickAllPlayers() {
        Iterator it = getPlayers().iterator();
        while (it.hasNext()) {
            Player p = (Player) it.next();
            if (GameManager.isScoreboardEnabled()) {
                getPlayerBoard(p).removeBoard();
            }
            PlayerManager.getInstance().restorePlayerData(p);
            if (BuildBattle.getInstance().isUseBungeecord()) {
                BungeeUtils.connectPlayerToServer(p, GameManager.getInstance().getRandomFallbackServer());
            }
            it.remove();
        }
    }

    public void setGamemodeToAllPlayers(GameMode gm) {
        for (Player p : getPlayers()) {
            p.setGameMode(gm);
        }
    }

    public void saveIntoConfig() {
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".lobbyLocation", LocationUtil.getStringFromLocation(getLobbyLocation()));
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".gameTime", getGameTime());
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".min_players", getMinPlayers());
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".mode", getGameType().name());
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".teamSize", getTeamSize());
        for(BBPlot plot : getBuildPlots()) {
            BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".plots." + getBuildPlots().indexOf(plot) + ".min", LocationUtil.getStringFromLocation(plot.getMinPoint()));
            BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".plots." + getBuildPlots().indexOf(plot) + ".max", LocationUtil.getStringFromLocation(plot.getMaxPoint()));
        }
        BuildBattle.info("§aArena §e" + getName() + " §asuccessfully saved into config !");
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
    }

    public String getTheme() {
        if(theme == null) {
            return Message.SCOREBOARD_WAITING.getMessage();
        } else {
            return theme;
        }
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public BBPlot getWinner() {
        return winner;
    }

    public void setWinner(BBPlot winner) {
        this.winner = winner;
    }

    public BBPlot getCurrentVotingPlot() {
        return currentVotingPlot;
    }

    public void setCurrentVotingPlot(BBPlot currentVotingPlot) {
        this.currentVotingPlot = currentVotingPlot;
    }

    public int getMinPlayers() {
        return min_players;
    }

    public BBArenaState getBBArenaState() {
        return bbArenaState;
    }

    public void setBBArenaState(BBArenaState BBArenaState) {
        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameStateSwitchEvent(this, getBBArenaState(), BBArenaState));
        this.bbArenaState = BBArenaState;
    }

    public BBArena getArenaInstance() {
        return this;
    }

    public void setMinPlayers(int minPlayers) {
        this.min_players = minPlayers;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
        BuildBattle.getFileManager().getConfig("arenas.yml").set(getName() + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
    }

    public void addIntoAllArenas() {
        if(!ArenaManager.getArenas().contains(this)) {
            ArenaManager.getArenas().add(this);
        }
    }

    public void delete(CommandSender sender) {
        stopArena(Message.ARENA_REMOVED.getChatMessage(), true);
        BuildBattle.getFileManager().getConfig("arenas.yml").get().set(getName(), null);
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
        ArenaManager.getArenas().remove(this);
        OptionsManager.getInstance().refreshAllArenasInventory();
        ArenaManager.getInstance().loadArenaEditors();
        sender.sendMessage(Message.ARENA_REMOVED.getChatMessage());
    }

    public int getMaxPlayers() {
        return getBuildPlots().size() * getTeamSize();
    }

    public void forceStart(CommandSender sender) {
        if(getBBArenaState() == BBArenaState.LOBBY) {
            if (isMinimumPlayersRequirementMet()) {
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                }
                if(GameManager.isVotingForThemes()) {
                    startThemeVoting();
                } else {
                    switch (getGameType()) {
                        case TEAM:
                            startGame(GameManager.getRandomTeamTheme(), true);
                            break;
                        case SOLO:
                            startGame(GameManager.getRandomSoloTheme(), true);
                            break;
                    }
                }
            } else {
                sender.sendMessage(Message.NOT_ENOUGH_PLAYERS.getChatMessage());
            }
        } else {
            sender.sendMessage(Message.ARENA_ALREADY_STARTED.getChatMessage());
        }
    }

    public void forceStop(CommandSender sender) {
        if(getBBArenaState() != BBArenaState.LOBBY) {
            switch (getBBArenaState()) {
                case THEME_VOTING:
                    themeVotingCountdown.cancel();
                    break;
                case INGAME:
                    gameCountdown.cancel();
                    break;
                case VOTING:
                    votingCountdown.cancel();
                    break;
                case ENDING:
                    endCountdown.cancel();
                    break;
            }
            stopArena(Message.FORCE_STOP.getChatMessage().replaceAll("%operator%", sender.getName()), true);
        } else {
            sender.sendMessage(Message.ARENA_NOT_RUNNING.getChatMessage());
        }
    }

    public void calculateResults() {
        Collections.sort(getVotingPlots());
        Collections.reverse(getVotingPlots());
        setWinner(getVotingPlots().get(0));
    }

    public void spawnWinnerFireworks() {
        if(getWinner() != null) {
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (times >= GameManager.getFireworkWaves()) {
                        cancel();
                    } else {
                        try {
                            for (Location l : LocationUtil.getAllCornersOfPlot(getWinner())) {
                                for (int i = 0; i < GameManager.getFireworkAmount(); i++) {
                                    FireworkUtil.spawnRandomFirework(LocationUtil.getCenter(l));
                                }
                            }
                        } catch (Exception e) {
                            cancel();
                        }
                    }
                    times++;
                }
            }.runTaskTimer(BuildBattle.getInstance(), 20L, 3 * 20L);
        }
    }

    public boolean isMinimumPlayersRequirementMet() {
        return getPlayers().size() >= getMinPlayers();
    }
    public boolean isInFirstThree(BBPlot plot) {
        if(getBuildPlots().indexOf(plot) == 0) {
            return true;
        } else if(getBuildPlots().indexOf(plot) == 1) {
            return true;
        } else if(getBuildPlots().indexOf(plot) == 2) {
            return true;
        } else {
            return false;
        }
    }

    public String getPosition(BBPlot plot) {
        return TimeUtil.ordinal(getVotingPlots().indexOf(plot)+1);
    }

    public void setVotingPlots() {
        List<BBPlot> returnList = new ArrayList<>();
        for (BBPlot plot : getBuildPlots()) {
            if ((plot.getTeam() != null) && (!plot.getTeam().isEmpty())) {
                returnList.add(plot);
            }
        }
        this.votingPlots = returnList;
    }

    public void forceStart(CommandSender sender, String theme) {
        if(getBBArenaState() == BBArenaState.LOBBY) {
            if(isMinimumPlayersRequirementMet()) {
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                }
                startGame(theme, true);
            } else {
                sender.sendMessage(Message.NOT_ENOUGH_PLAYERS.getChatMessage());
            }
        } else {
            sender.sendMessage(Message.ARENA_ALREADY_STARTED.getChatMessage());
        }
    }

    public void updateAllScoreboards(int timeleft) {
        for(BBBoard sb : getPlayerBoards()) {
            sb.updateScoreboard(timeleft);
        }
    }

    public BukkitTask getThemeVotingCountdown() {
        return themeVotingCountdown;
    }

    public BBThemeVoting getThemeVoting() {
        return themeVoting;
    }

    public List<BBSign> getArenaSigns() {
        return arenaSigns;
    }

    public void setArenaSigns(List<BBSign> arenaSigns) {
        this.arenaSigns = arenaSigns;
    }

    public void updateAllSigns() {
        for(BBSign sign : getArenaSigns()) {
            sign.update();
        }
    }

    public BBGameMode getGameType() {
        return gameMode;
    }

    public void setGameType(BBGameMode gameType) {
        this.gameMode = gameType;
    }

    public void setupTeams() {
        for(int i = 1;i <= getBuildPlots().size();i++) {
            BBTeam team = new BBTeam(this, i);
            teams.add(team);
        }
    }

    public List<BBTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<BBTeam> teams) {
        this.teams = teams;
    }

    public List<BBTeam> getValidTeams() {
        List<BBTeam> returnList = new ArrayList<>();
        for(BBTeam t : getTeams()) {
            if(t.getPlayers().size() > 0) {
                returnList.add(t);
            }
        }
        return returnList;
    }

    public Inventory getTeamsInventory() {
        return teamsInventory;
    }

    public void setTeamsInventory(Inventory teamsInventory) {
        this.teamsInventory = teamsInventory;
    }

    public void setupTeamInventory() {
        this.teamsInventory = Bukkit.createInventory(null, ItemCreator.getInventorySizeBasedOnList(getTeams()), Message.GUI_TEAMS_TITLE.getMessage());
        for(BBTeam team : getTeams()) {
            getTeamsInventory().addItem(team.getStatusItemStack());
        }
    }

    public BBTeam getTeamByItemStack(ItemStack currentItem) {
        for(BBTeam team : getTeams()) {
            if(team.getStatusItemStack().equals(currentItem)) {
                return team;
            }
        }
        return null;
    }

    public BBTeam getPlayerTeam(Player p) {
        for(BBTeam t : getTeams()) {
            if(t.getPlayers().contains(p)) {
                return t;
            }
        }
        return null;
    }

    public BBBoard getPlayerBoard(Player p) {
        for(BBBoard board : getPlayerBoards()) {
            if(board.getPlayer().equals(p)) {
                return board;
            }
        }
        return null;
    }
    public List<BBBoard> getPlayerBoards() {
        return playerBoards;
    }

    public String getArenaModeInString() {
        switch (getGameType()) {
            case SOLO:
                return Message.SCOREBOARD_SOLO_MODE.getMessage();
            case TEAM:
                return Message.SCOREBOARD_TEAM_MODE.getMessage();
        }
        return "&cNO MODE";
    }

    public String getTeamMateName(Player player) {
        if(getPlayerTeam(player) != null) {
            return "&a" + getPlayerTeam(player).getOtherPlayers(player);
        } else {
            return "&7&oNobody";
        }
    }
    public List<Player> getTeamMates(Player p) {
        if(getPlayerTeam(p) != null) {
            List<Player> returnList = new ArrayList<>(getPlayerTeam(p).getPlayers());
            returnList.remove(p);
            return returnList;
        }
        return null;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public Player getLastNonVipPlayer() {
        for(int i = getPlayers().size()-1; i >= 0;i--) {
            Player p = getPlayers().get(i);
            if(!p.hasPermission("buildbattlepro.joinfull")) {
                return p;
            }
        }
        return null;
    }
}
