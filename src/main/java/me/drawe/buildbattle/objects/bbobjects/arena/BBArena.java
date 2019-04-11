package me.drawe.buildbattle.objects.bbobjects.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.events.game.BBGameEndEvent;
import me.drawe.buildbattle.events.game.BBGameStartEvent;
import me.drawe.buildbattle.events.game.BBGameStateSwitchEvent;
import me.drawe.buildbattle.events.game.BBPlayerGameJoinEvent;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlotTime;
import me.drawe.buildbattle.objects.bbobjects.scoreboards.BBScoreboard;
import me.drawe.buildbattle.utils.*;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
    private int minPlayers;
    private int gameTime;
    private int teamSize;
    private Location lobbyLocation;
    private List<Player> players;
    private List<BBTeam> teams;
    private List<BBPlot> buildPlots;
    private List<BBPlot> votingPlots;
    private List<BBSign> arenaSigns;
    private HashMap<Player, BBScoreboard> playerBoards;
    private String theme;
    private BBPlot winner;
    private BBPlot currentVotingPlot;
    private BBArenaEdit arenaEdit;
    private BBGameMode gameMode;
    private BBThemeVoting themeVoting;
    private BBArenaState bbArenaState;

    private BukkitTask lobbyCountdown;
    private BukkitTask gameCountdown;
    private BukkitTask votingCountdown;
    private BukkitTask endCountdown;
    private BukkitTask themeVotingCountdown;

    private Inventory teamsInventory;

    //LOADING
    public BBArena(String name) {
        this.name = name;
        this.minPlayers = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(name + ".min_players");
        this.gameTime = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(name + ".gameTime");
        this.gameMode = BBGameMode.valueOf(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(name + ".mode"));
        this.teamSize = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(name + ".teamSize");
        this.lobbyLocation = LocationUtil.getLocationFromConfig("arenas.yml", name + ".lobbyLocation");
        this.players = new ArrayList<>();
        this.buildPlots = loadArenaPlots();
        this.arenaSigns = loadArenaSigns();
        this.theme = null;
        this.winner = null;
        this.currentVotingPlot = null;
        this.themeVoting = new BBThemeVoting(this);
        this.bbArenaState = BBArenaState.LOBBY;
        this.playerBoards = new HashMap<>();
        this.setupTeams();
        this.setupTeamInventory();
        this.arenaEdit = new BBArenaEdit(this);
    }

    //CREATING
    public BBArena(String name, BBGameMode gameMode) {
        this.name = name;
        this.minPlayers = 2;
        this.gameTime = BBSettings.getDefaultGameTime();
        this.gameMode = gameMode;
        this.players = new ArrayList<>();
        this.buildPlots = new ArrayList<>();
        this.arenaSigns = new ArrayList<>();
        this.playerBoards = new HashMap<>();
        this.theme = null;
        this.winner = null;
        this.currentVotingPlot = null;
        this.themeVoting = new BBThemeVoting(this);
        this.bbArenaState = BBArenaState.LOBBY;

        if (gameMode == BBGameMode.SOLO) {
            this.teamSize = 1;
        } else if (gameMode == BBGameMode.TEAM) {
            this.teamSize = 2;
        }

        this.setupTeams();
        this.setupTeamInventory();
        this.saveIntoConfig();
        this.arenaEdit = new BBArenaEdit(this);
    }

    private List<BBPlot> loadArenaPlots() {
        List<BBPlot> list = new ArrayList<>();
        try {
            for (String plot : BuildBattle.getFileManager().getConfig("arenas.yml").get().getConfigurationSection(name + ".plots").getKeys(false)) {
                final Location minPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".min"));
                final Location maxPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".max"));
                list.add(new BBPlot(this, minPoint, maxPoint));
                BuildBattle.info("§aPlot §e" + plot + " §afor arena §e" + name + " §aloaded !");
            }
        } catch (Exception e) {
            BuildBattle.warning("§cLooks like arena §e" + name + " §c have no plots ! Please set them.");
        }
        return list;
    }

    private List<BBSign> loadArenaSigns() {
        List<BBSign> list = new ArrayList<>();
        try {
            if (BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name) != null) {
                for (String sign : BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name).getKeys(false)) {
                    final Location signLoc = LocationUtil.getLocationFromString(sign);
                    final BBSign bbSign = new BBSign(this, signLoc);
                    if (bbSign.getLocation() != null) {
                        list.add(bbSign);
                    }
                }
            }
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading signs for arena §e" + name + "§c!");
            e.printStackTrace();
        }
        return list;
    }

    public List<BBPlot> getBuildPlots() {
        return buildPlots;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player p) {
        if (bbArenaState == BBArenaState.LOBBY) {
            if (players.size() < getMaxPlayers()) {

                if (callJoinEvent(p)) {
                    return;
                }

                try {
                    joinCommands(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(Message.UNABLE_TO_JOIN.getChatMessage());
                    return;
                }

                if (players.size() == minPlayers) {
                    startLobby();
                }
            } else {
                if (p.hasPermission("buildbattlepro.joinfull")) {
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

    private boolean callJoinEvent(Player p) {
        BBPlayerGameJoinEvent bbPlayerGameJoinEvent = new BBPlayerGameJoinEvent(this, p);
        Bukkit.getPluginManager().callEvent(bbPlayerGameJoinEvent);
        return bbPlayerGameJoinEvent.isCancelled();
    }

    private void leaveCommands(Player p) {

        BBTeam team = PlayerManager.getInstance().getPlayerTeam(this, p);
        if (team != null) {
            team.leaveTeam(p);
        }

        BBParty party = PartyManager.getInstance().getPlayerParty(p);
        if (party != null) {
            PartyManager.getInstance().leaveParty(p);
        }

        PlayerManager.getInstance().removeScoreboard(p);

        PlayerManager.getInstance().restorePlayerData(p);

        if (BBSettings.getMainLobbyLocation() != null) {
            PlayerManager.getInstance().teleportToMainLobby(p);
        }

        if (BBSettings.isUseBungeecord()) {
            BungeeUtils.connectPlayerToServer(p, BBSettings.getRandomFallbackServer());
        }

        players.remove(p);
        playerBoards.remove(p);

        updateAllSigns();
        ArenaManager.getInstance().refreshArenaItem(this);

        PlayerManager.getPlayersInArenas().remove(p);
        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.PLAYER_LEFT.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));

        if (bbArenaState != BBArenaState.LOBBY) {
            if (players.size() <= teamSize) {
                stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
            }
        }
    }

    private void joinCommands(Player p) {

        BBParty party = PartyManager.getInstance().getPlayerParty(p);

        if ((party != null) && (party.isCreator(p))) {
            party.joinGame(this);
        }

        PlayerManager.getInstance().createNewPlayerData(p);
        PlayerManager.getInstance().createPlayerStatsIfNotExists(p);

        p.teleport(lobbyLocation.clone().add(0, 0.5, 0));
        p.getInventory().clear();
        p.setExp(0F);
        p.setLevel(0);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);
        players.add(p);
        playerBoards.put(p, BBScoreboard.getActualScoreboard(this));

        if (gameMode == BBGameMode.TEAM) {
            p.getInventory().setItem(0, OptionsManager.getTeamsItem());
        }

        p.getInventory().setItem(8, OptionsManager.getLeaveItem());

        updateAllScoreboards(BBSettings.getLobbyTime(), BBSettings.getLobbyTime());

        PlayerManager.getInstance().broadcastToAllPlayersInArena(this, Message.PLAYER_JOINED.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));

        ArenaManager.getInstance().refreshArenaItem(this);
        updateAllSigns();

        PlayerManager.getPlayersInArenas().put(p, this);
    }

    public BBTeam getFreeBBTeamForParty(BBParty party) {
        int amountOfPlayers = party.getPlayers().size();
        for (BBTeam team : teams) {
            if (team.getLeftSlots() >= amountOfPlayers) {
                return team;
            }
        }
        return null;
    }

    public void removePlayer(Player p) {
        leaveCommands(p);
    }

    private void startLobby() {
        setBBArenaState(BBArenaState.LOBBY);
        lobbyCountdown = new BukkitRunnable() {
            int countdown = BBSettings.getLobbyTime();

            @Override
            public void run() {
                if (isMinimumPlayersRequirementMet()) {
                    if (countdown == 0) {
                        if (BBSettings.isVotingForThemes()) {
                            startThemeVoting();
                        } else {
                            switch (gameMode) {
                                case SOLO:
                                    startGame(BBSettings.getRandomSoloTheme(), true);
                                    break;
                                case TEAM:
                                    startGame(BBSettings.getRandomTeamTheme(), true);
                                    break;
                            }
                        }
                        cancel();
                        return;
                    } else if (countdown % 15 == 0 || countdown < 6) {
                        PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), CompSound.ORB_PICKUP);
                        PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.GAME_STARTS_IN.getChatMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                    }
                } else {
                    PlayerManager.getInstance().broadcastToAllPlayersInArena(getArenaInstance(), Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
                    updateAllScoreboards(BBSettings.getLobbyTime(), BBSettings.getLobbyTime());
                    lobbyCountdown.cancel();
                    return;
                }
                updateAllScoreboards(countdown, BBSettings.getLobbyTime());
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);
    }

    private void startThemeVoting() {
        setBBArenaState(BBArenaState.THEME_VOTING);
        setPlotsToTeams();
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(getArenaInstance());
        themeVotingCountdown = new BukkitRunnable() {
            int countdown = (int) BBSettings.getThemeVotingTime();

            @Override
            public void run() {
                updateAllScoreboards(countdown, (int) BBSettings.getThemeVotingTime());
                if (countdown == 0) {
                    themeVoting.setWinner();
                    startGame(themeVoting.getWinner().getName());
                    cancel();
                } else {
                    themeVoting.updateVoting(countdown);
                    countdown--;
                }
            }
        }.runTaskTimer(BuildBattle.getInstance(), 20L, 20L);
    }

    private void resetAllScoreboards() {
        for (Player p : playerBoards.keySet()) {
            playerBoards.put(p, BBScoreboard.getActualScoreboard(this));
        }
    }

    private void startGame(String theme) {
        setBBArenaState(BBArenaState.INGAME);
        this.theme = theme;
        setVotingPlots();
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        PlayerManager.getInstance().addPlayedToAllPlayers(this);
        OptionsManager.getInstance().giveAllPlayersItem(this, OptionsManager.getOptionsItem(), 8);
        PlayerManager.getInstance().sendStartMessageToAllPlayers(this);
        PlayerManager.getInstance().closeInventoryAllPlayersInArena(this);

        gameCountdown = new BukkitRunnable() {
            int countdown = gameTime;

            @Override
            public void run() {
                if (countdown == 0) {
                    updateAllScoreboards(countdown, getGameTime());
                    startVotingCountdown();
                    cancel();
                    return;
                } else if ((countdown % 60 == 0 && countdown >= 60 && countdown != gameTime) || (countdown % 30 == 0 && countdown < 60) || (countdown < 11)) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                }
                updateAllScoreboards(countdown, getGameTime());
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
        this.theme = theme;
        if (fromLobby) {
            setPlotsToTeams();
        }
        setVotingPlots();
        PlayerManager.getInstance().addPlayedToAllPlayers(this);
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        OptionsManager.getInstance().giveAllPlayersItem(this, OptionsManager.getOptionsItem(), 8);
        PlayerManager.getInstance().sendStartMessageToAllPlayers(this);
        PlayerManager.getInstance().closeInventoryAllPlayersInArena(this);

        gameCountdown = new BukkitRunnable() {
            int countdown = gameTime;

            @Override
            public void run() {
                if (countdown == 0) {
                    updateAllScoreboards(countdown, getGameTime());
                    startVotingCountdown();
                    cancel();
                    return;
                } else if ((countdown % 60 == 0 && countdown >= 60 && countdown != gameTime) || (countdown % 30 == 0 && countdown < 60) || (countdown < 11)) {
                    PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    PlayerManager.getInstance().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                }
                updateAllScoreboards(countdown, getGameTime());
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);

        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameStartEvent(this));
    }

    public List<BBPlot> getVotingPlots() {
        return votingPlots;
    }

    private void endGame() {
        setBBArenaState(BBArenaState.ENDING);
        calculateResults();
        this.winner = votingPlots.get(0);
        teleportAllPlayersToPlot(winner);
        updateAllScoreboards(0, BBSettings.getEndTime());

        PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
        PlayerManager.getInstance().addWinsToWinner(this);
        PlayerManager.getInstance().sendResultsToAllPlayers(this);
        PlayerManager.getInstance().setAllPlayersMostPoints(this);

        spawnWinnerFireworks();

        if (BBSettings.isEndCommandValid()) {
            runEndCommands();
        }
        if (!BBSettings.isGiveRewardsAfterGameEnds()) {
            RewardManager.getInstance().giveRewards(this);
        }

        ArenaManager.setTotalPlayedGames(ArenaManager.getTotalPlayedGames() + 1);
        endCountdown = new BukkitRunnable() {

            @Override
            public void run() {
                stopArena(Message.ARENA_ENDED.getChatMessage(), false);
            }
        }.runTaskLater(BuildBattle.getInstance(), BBSettings.getEndTime() * 20L);

        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameEndEvent(getArenaInstance(), getWinner().getTeam()));
    }

    public String getTotalPlayers() {
        return getPlayers().size() + "/" + getMaxPlayers();
    }


    private void startVotingCountdown() {
        setBBArenaState(BBArenaState.VOTING);

        PlayerManager.getInstance().sendTitleToAllPlayersInArena(this, Message.TITLE_VOTING.getMessage(), Message.SUBTITLE_VOTING.getMessage());
        PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
        PlayerManager.getInstance().setAllPlayersFlying(this);
        PlayerManager.getInstance().removeAllPotionEffects(this);

        setGamemodeToAllPlayers(GameMode.ADVENTURE);

        setVotingPlots();
        if (votingPlots == null) {
            stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
        } else {
            try {
                this.currentVotingPlot = votingPlots.get(0);
            } catch (Exception e) {
                endGame();
                return;
            }

            votingCountdown = new BukkitRunnable() {
                int timeLeft = BBSettings.getVotingTime();
                int index = 0;

                @Override
                public void run() {
                    if (timeLeft == BBSettings.getVotingTime()) {
                        teleportAllPlayersToPlot(currentVotingPlot);
                        updateAllScoreboards(timeLeft, BBSettings.getVotingTime());
                        PlayerManager.getInstance().giveVoteItemsAllPlayers(getArenaInstance());
                    }
                    if (timeLeft == 0) {
                        currentVotingPlot.setFinalPoints();
                        try {
                            index += 1;
                            currentVotingPlot = votingPlots.get(index);
                            timeLeft = BBSettings.getVotingTime();
                        } catch (Exception e) {
                            cancel();
                            endGame();
                        }
                        return;
                    } else if (timeLeft < 6) {
                        PlayerManager.getInstance().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    }
                    if (currentVotingPlot != null) {
                        PlayerManager.getInstance().setLevelsToAllPlayers(getArenaInstance(), timeLeft);
                        updateAllScoreboards(timeLeft, BBSettings.getVotingTime());
                        if (timeLeft >= 1)
                            PlayerManager.getInstance().sendActionBarToAllPlayers(getArenaInstance(), Message.VOTE_TIME.getMessage().replaceAll("%time%", new Time(timeLeft, TimeUnit.SECONDS).toString()));
                    }
                    timeLeft -= 1;
                }
            }.runTaskTimer(BuildBattle.getInstance(), 100L, 20L);
        }
    }

    private void teleportAllPlayersToPlot(BBPlot plot) {
        for (Player p : players) {
            p.setAllowFlight(true);
            p.setFlying(true);
            p.teleport(plot.getRandomLocationInPlot());
            p.setPlayerTime(plot.getOptions().getCurrentTime().getTime(), false);
            p.setPlayerWeather(plot.getOptions().getCurrentWeather());
        }
        if (plot.getTeam().getPlayers().size() == 1) {
            PlayerManager.getInstance().sendTitleToAllPlayersInArena(this, Message.VOTING_BUILDER.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        } else {
            PlayerManager.getInstance().sendTitleToAllPlayersInArena(this, Message.VOTING_BUILDERS.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        }
    }

    public boolean isFull() {
        return players.size() == getMaxPlayers();
    }

    public void stopArena(String message, boolean forced) {

        PlayerManager.getInstance().broadcastToAllPlayersInArena(this, message);

        switch (bbArenaState) {
            case LOBBY:
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                    updateAllScoreboards(BBSettings.getLobbyTime(), BBSettings.getLobbyTime());
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

        if (forced || BBSettings.isRemovePlayersAfterGame()) {
            kickAllPlayers();
            if (BBSettings.isGiveRewardsAfterGameEnds()) {
                RewardManager.getInstance().giveRewards(this);
            }
        } else {
            updateAllScoreboards(BBSettings.getLobbyTime(), BBSettings.getLobbyTime());
            PlayerManager.getInstance().clearInventoryAllPlayersInArena(this);
            PlayerManager.getInstance().teleportAllPlayersToLobby(getArenaInstance());
            setGamemodeToAllPlayers(GameMode.ADVENTURE);
            if (gameMode == BBGameMode.TEAM) {
                PlayerManager.getInstance().giveAllPlayersTeamsItem(getArenaInstance());
            }
            PlayerManager.getInstance().giveAllPlayersLeaveItem(getArenaInstance());
            if (players.size() >= minPlayers) {
                startLobby();
            }
        }

        this.winner = null;
        this.currentVotingPlot = null;
        this.theme = null;
        this.resetAllPlots();
        this.resetAllTeams();

        if (BBSettings.isVotingForThemes()) {
            themeVoting.reset();
        }

        setBBArenaState(BBArenaState.LOBBY);
    }

    private void resetAllTeams() {
        for (BBTeam t : getTeams()) {
            t.resetTeam();
        }
    }

    private void resetAllPlots() {
        for (BBPlot plot : buildPlots) {
            plot.restoreBBPlot();
        }
    }

    private void setPlotsToTeams() {
        for (Player p : players) {
            if (getPlayerTeam(p) == null) {
                autoAssignTeamToPlayer(p);
            }
        }
        List<BBTeam> teams = new ArrayList<>(getValidTeams());
        for (BBPlot plot : buildPlots) {
            if (!teams.isEmpty()) {
                plot.setTeam(teams.get(0));
                plot.teleportTeamToPlot();
                plot.getOptions().setCurrentTime(BBPlotTime.NOON, false);
            } else {
                break;
            }
            teams.remove(0);
        }
    }

    private void autoAssignTeamToPlayer(Player p) {
        for (BBTeam team : teams) {
            if (!team.isFull()) {
                team.getPlayers().add(p);
                break;
            }
        }
    }

    private void joinVIP(Player vip) {
        Player lastNonVIPPlayer = getLastNonVipPlayer();
        if (lastNonVIPPlayer != null) {
            removePlayer(lastNonVIPPlayer);
            lastNonVIPPlayer.sendMessage(Message.KICKED_DUE_TO_VIP_JOIN.getChatMessage().replaceAll("%player%", vip.getDisplayName()));
            joinCommands(vip);
            if (players.size() == minPlayers) {
                startLobby();
            }
        } else {
            vip.sendMessage(Message.NO_VIP_SLOT_FREE.getChatMessage());
        }
    }

    public void kickAllPlayers() {
        Iterator it = players.iterator();
        while (it.hasNext()) {
            Player p = (Player) it.next();

            if (BBSettings.isScoreboardEnabled()) {
                playerBoards.remove(p);
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            PlayerManager.getInstance().restorePlayerData(p);

            if (BBSettings.getMainLobbyLocation() != null) {
                PlayerManager.getInstance().teleportToMainLobby(p);
            }
            PlayerManager.getPlayersInArenas().remove(p);

            if (BBSettings.isUseBungeecord()) {
                BungeeUtils.connectPlayerToServer(p, BBSettings.getRandomFallbackServer());
            }
            it.remove();
        }
    }

    private void setGamemodeToAllPlayers(GameMode gm) {
        for (Player p : players) {
            p.setGameMode(gm);
        }
    }

    public void saveIntoConfig() {
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".gameTime", gameTime);
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".min_players", minPlayers);
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".mode", gameMode.name());
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".teamSize", teamSize);

        for (BBPlot plot : buildPlots) {
            BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".plots." + buildPlots.indexOf(plot) + ".min", LocationUtil.getStringFromLocation(plot.getMinPoint()));
            BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".plots." + buildPlots.indexOf(plot) + ".max", LocationUtil.getStringFromLocation(plot.getMaxPoint()));
        }

        BuildBattle.info("§aArena §e" + name + " §asuccessfully saved into config !");
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
    }

    public String getTheme() {
        if (theme == null) {
            return Message.SCOREBOARD_WAITING.getMessage();
        } else {
            return theme;
        }
    }

    public BBPlot getWinner() {
        return winner;
    }

    public BBPlot getCurrentVotingPlot() {
        return currentVotingPlot;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public BBArenaState getBBArenaState() {
        return bbArenaState;
    }

    public void setBBArenaState(BBArenaState newState) {
        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameStateSwitchEvent(this, bbArenaState, newState));
        this.bbArenaState = newState;
        this.updateAllSigns();
        this.resetAllScoreboards();
        ArenaManager.getInstance().refreshArenaItem(this);
    }

    public BBArena getArenaInstance() {
        return this;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
        BuildBattle.getFileManager().getConfig("arenas.yml").set(name + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));
        BuildBattle.getFileManager().getConfig("arenas.yml").save();
    }

    public void delete(CommandSender sender) {
        stopArena(Message.ARENA_REMOVED.getChatMessage(), true);

        BuildBattle.getFileManager().getConfig("arenas.yml").get().set(name, null);
        BuildBattle.getFileManager().getConfig("arenas.yml").save();

        sender.sendMessage(Message.ARENA_REMOVED.getChatMessage());
    }

    public int getMaxPlayers() {
        return buildPlots.size() * teamSize;
    }

    public void forceStart(CommandSender sender, boolean ignoreMinPlayers) {
        if (bbArenaState == BBArenaState.LOBBY) {
            if (isMinimumPlayersRequirementMet() || ignoreMinPlayers) {
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                }
                if (BBSettings.isVotingForThemes()) {
                    startThemeVoting();
                } else {
                    switch (gameMode) {
                        case TEAM:
                            startGame(BBSettings.getRandomTeamTheme(), true);
                            break;
                        case SOLO:
                            startGame(BBSettings.getRandomSoloTheme(), true);
                            break;
                    }
                }
            } else {
                sender.sendMessage(Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
            }
        } else {
            sender.sendMessage(Message.ARENA_ALREADY_STARTED.getChatMessage());
        }
    }

    public void forceStop(CommandSender sender) {
        if (bbArenaState != BBArenaState.LOBBY) {
            stopArena(Message.FORCE_STOP.getChatMessage().replaceAll("%operator%", sender.getName()), true);
        } else {
            sender.sendMessage(Message.ARENA_NOT_RUNNING.getChatMessage());
        }
    }

    private void calculateResults() {
        if (BBSettings.isFairVote())
            VotingManager.getInstance().checkVotes(this);
        Collections.sort(votingPlots);
        Collections.reverse(votingPlots);
    }

    private void spawnWinnerFireworks() {
        if (!BBSettings.isWinFireworksEnabled()) {
            return;
        }
        if (winner != null) {
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (times >= BBSettings.getFireworkWaves()) {
                        cancel();
                        return;
                    } else {
                        try {
                            for (Location l : winner.getPlotCorners()) {
                                for (int i = 0; i < BBSettings.getFireworkAmount(); i++) {
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
        return players.size() >= minPlayers;
    }


    public String getPosition(BBPlot plot) {
        return Time.ordinal(votingPlots.indexOf(plot) + 1);
    }

    private void setVotingPlots() {
        List<BBPlot> returnList = new ArrayList<>();
        for (BBPlot plot : buildPlots) {
            if ((plot.getTeam() != null) && (!plot.getTeam().isEmpty())) {
                returnList.add(plot);
            }
        }
        this.votingPlots = returnList;
    }

    public void forceStart(CommandSender sender, String theme, boolean ignoreMinPlayers) {
        if (bbArenaState == BBArenaState.LOBBY) {
            if (isMinimumPlayersRequirementMet() || ignoreMinPlayers) {
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                }
                startGame(theme, true);
            } else {
                sender.sendMessage(Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
            }
        } else {
            sender.sendMessage(Message.ARENA_ALREADY_STARTED.getChatMessage());
        }
    }

    private void updateAllScoreboards(int timeleft, int baseTime) {
        if (BBSettings.isScoreboardEnabled()) {
            for (Player p : playerBoards.keySet()) {
                playerBoards.get(p).updateScoreboard(p, this, timeleft, baseTime);
            }
        }
    }

    public BBThemeVoting getThemeVoting() {
        return themeVoting;
    }

    public List<BBSign> getArenaSigns() {
        return arenaSigns;
    }

    private void updateAllSigns() {
        for (BBSign sign : getArenaSigns()) {
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
        this.teams = new ArrayList<>();
        for (int i = 1; i <= buildPlots.size(); i++) {
            teams.add(new BBTeam(this, i));
        }
    }

    public List<BBTeam> getTeams() {
        return teams;
    }

    public List<BBTeam> getValidTeams() {
        List<BBTeam> returnList = new ArrayList<>();
        for (BBTeam t : teams) {
            if (t.getPlayers().size() > 0) {
                returnList.add(t);
            }
        }
        return returnList;
    }

    public Inventory getTeamsInventory() {
        return teamsInventory;
    }

    public void setupTeamInventory() {
        this.teamsInventory = Bukkit.createInventory(null, ItemUtil.getInventorySizeBasedOnList(teams), Message.GUI_TEAMS_TITLE.getMessage());
        for (BBTeam team : teams) {
            teamsInventory.addItem(team.getStatusItemStack());
        }
    }

    public BBTeam getTeamByItemStack(ItemStack currentItem) {
        for (BBTeam team : teams) {
            if (team.getStatusItemStack().equals(currentItem)) {
                return team;
            }
        }
        return null;
    }

    public ItemStack getArenaStatusItem() {
        return ItemUtil.create(bbArenaState.getBlockMaterial(), 1, name, ItemUtil.makeLore(
                " ",
                Message.ARENA_LIST_MODE.getMessage().replaceAll("%mode%", gameMode.getName()),
                Message.ARENA_LIST_PLAYERS.getMessage().replaceAll("%total_players%", getTotalPlayers()),
                Message.ARENA_LIST_STATUS.getMessage().replaceAll("%status%", bbArenaState.getPrefix()),
                " ",
                Message.ARENA_LIST_CLICK_TO_JOIN.getMessage()), null, null);
    }

    public BBTeam getPlayerTeam(Player p) {
        for (BBTeam t : teams) {
            if (t.getPlayers().contains(p)) {
                return t;
            }
        }
        return null;
    }

    private void runEndCommands() {
        for (String cmd : BBSettings.getEndCommands()) {
            for (Player p : players) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", p.getName()));
            }
        }
    }

    public BBScoreboard getPlayerBoard(Player p) {
        return playerBoards.get(p);
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

    public String getTeamMatesNames(Player player) {
        if (getPlayerTeam(player) != null) {
            return getPlayerTeam(player).getOtherPlayers(player);
        } else {
            return Message.SCOREBOARD_NO_TEAMMATES.getMessage();
        }
    }

    public List<Player> getTeamMates(Player p) {
        if (getPlayerTeam(p) != null) {
            List<Player> returnList = new ArrayList<>(getPlayerTeam(p).getPlayers());
            returnList.remove(p);
            return returnList;
        }
        return new ArrayList<>();
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

    private Player getLastNonVipPlayer() {
        for (int i = players.size() - 1; i >= 0; i--) {
            Player p = players.get(i);
            if (!p.hasPermission("buildbattlepro.joinfull")) {
                return p;
            }
        }
        return null;
    }

    public BBArenaEdit getArenaEdit() {
        return arenaEdit;
    }
}
