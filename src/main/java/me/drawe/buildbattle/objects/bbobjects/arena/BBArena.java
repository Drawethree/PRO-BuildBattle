package me.drawe.buildbattle.objects.bbobjects.arena;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.api.events.game.BBGameEndEvent;
import me.drawe.buildbattle.api.events.game.BBGameStartEvent;
import me.drawe.buildbattle.api.events.game.BBGameStateSwitchEvent;
import me.drawe.buildbattle.api.events.game.BBPlayerGameJoinEvent;
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

public class BBArena implements Spectetable<Player> {

    private BuildBattle plugin;
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
    private List<Player> spectators;
    private Inventory spectateInventory;

    //LOADING
    public BBArena(BuildBattle plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.minPlayers = plugin.getFileManager().getConfig("arenas.yml").get().getInt(name + ".min_players");
        this.gameTime = plugin.getFileManager().getConfig("arenas.yml").get().getInt(name + ".gameTime");
        this.gameMode = BBGameMode.valueOf(plugin.getFileManager().getConfig("arenas.yml").get().getString(name + ".mode"));
        this.teamSize = plugin.getFileManager().getConfig("arenas.yml").get().getInt(name + ".teamSize");
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
        this.arenaEdit = new BBArenaEdit(plugin,this);
    }

    //CREATING
    public BBArena(BuildBattle plugin, String name, BBGameMode gameMode) {
        this.plugin = plugin;
        this.name = name;
        this.minPlayers = 2;
        this.gameTime = plugin.getSettings().getDefaultGameTime();
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
        this.arenaEdit = new BBArenaEdit(plugin, this);
    }

    private List<BBPlot> loadArenaPlots() {
        List<BBPlot> list = new ArrayList<>();
        try {
            for (String plot : plugin.getFileManager().getConfig("arenas.yml").get().getConfigurationSection(name + ".plots").getKeys(false)) {
                final Location minPoint = LocationUtil.getLocationFromString(plugin.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".min"));
                final Location maxPoint = LocationUtil.getLocationFromString(plugin.getFileManager().getConfig("arenas.yml").get().getString(name + ".plots." + plot + ".max"));
                list.add(new BBPlot(this, minPoint, maxPoint));
                plugin.info("§aPlot §e" + plot + " §afor arena §e" + name + " §aloaded !");
            }
        } catch (Exception e) {
            plugin.warning("§cLooks like arena §e" + name + " §c have no plots ! Please set them.");
        }
        return list;
    }

    private List<BBSign> loadArenaSigns() {
        List<BBSign> list = new ArrayList<>();
        try {
            if (plugin.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name) != null) {
                for (String sign : plugin.getFileManager().getConfig("signs.yml").get().getConfigurationSection(name).getKeys(false)) {
                    final Location signLoc = LocationUtil.getLocationFromString(sign);
                    final BBSign bbSign = new BBSign(this, signLoc);
                    if (bbSign.getLocation() != null) {
                        list.add(bbSign);
                    }
                }
            }
        } catch (Exception e) {
            plugin.severe("§cAn exception occurred while trying loading signs for arena §e" + name + "§c!");
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

        BBTeam team = plugin.getPlayerManager().getPlayerTeam(this, p);
        if (team != null) {
            team.leaveTeam(p);
        }

        BBParty party = plugin.getPartyManager().getPlayerParty(p);
        if (party != null) {
            plugin.getPartyManager().leaveParty(p);
        }

        plugin.getPlayerManager().removeScoreboard(p);

        plugin.getPlayerManager().restorePlayerData(p);

        if (this.plugin.getSettings().getMainLobbyLocation() != null) {
            plugin.getPlayerManager().teleportToMainLobby(p);
        }

        if (this.plugin.getSettings().isUseBungeecord()) {
            BungeeUtils.connectPlayerToServer(p, this.plugin.getSettings().getRandomFallbackServer());
        }

        players.remove(p);
        playerBoards.remove(p);

        updateAllSigns();
        this.refreshSpectateInventory();
        plugin.getArenaManager().refreshArenaItem(this);

        plugin.getPlayerManager().getPlayersInArenas().remove(p);
        plugin.getPlayerManager().broadcastToAllPlayersInArena(getArenaInstance(), Message.PLAYER_LEFT.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));

        if (bbArenaState != BBArenaState.LOBBY) {
            if (players.size() <= teamSize) {
                stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
            }
        }
    }

    private void refreshSpectateInventory() {
        if (spectateInventory == null) {
            return;
        }
        spectateInventory.clear();
        for (Player p : this.players) {
            spectateInventory.addItem(ItemUtil.createPlayerHead(p));
        }
    }

    private void joinCommands(Player p) {

        BBParty party = plugin.getPartyManager().getPlayerParty(p);

        if ((party != null) && (party.isCreator(p))) {
            party.joinGame(this);
        }

        plugin.getPlayerManager().createNewPlayerData(p);
        plugin.getPlayerManager().createPlayerStatsIfNotExists(p);

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
            p.getInventory().setItem(0, plugin.getOptionsManager().getTeamsItem());
        }

        p.getInventory().setItem(8, plugin.getOptionsManager().getLeaveItem());

        updateAllScoreboards(this.plugin.getSettings().getLobbyTime(), this.plugin.getSettings().getLobbyTime());

        plugin.getPlayerManager().broadcastToAllPlayersInArena(this, Message.PLAYER_JOINED.getChatMessage().replaceAll("%player%", p.getDisplayName()).replaceAll("%players%", getTotalPlayers()));

        this.refreshSpectateInventory();
        plugin.getArenaManager().refreshArenaItem(this);
        updateAllSigns();

        plugin.getPlayerManager().getPlayersInArenas().put(p, this);
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
            int countdown = plugin.getSettings().getLobbyTime();

            @Override
            public void run() {
                if (isMinimumPlayersRequirementMet()) {
                    if (countdown == 0) {
                        if (plugin.getSettings().isVotingForThemes()) {
                            startThemeVoting();
                        } else {
                            switch (gameMode) {
                                case SOLO:
                                    startGame(plugin.getSettings().getRandomSoloTheme(), true);
                                    break;
                                case TEAM:
                                    startGame(plugin.getSettings().getRandomTeamTheme(), true);
                                    break;
                            }
                        }
                        cancel();
                        return;
                    } else if (countdown % 15 == 0 || countdown < 6) {
                        plugin.getPlayerManager().playSoundToAllPlayers(getArenaInstance(), CompSound.ORB_PICKUP);
                        plugin.getPlayerManager().broadcastToAllPlayersInArena(getArenaInstance(), Message.GAME_STARTS_IN.getChatMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
                    }
                } else {
                    plugin.getPlayerManager().broadcastToAllPlayersInArena(getArenaInstance(), Message.NOT_ENOUGH_PLAYERS_TO_START.getChatMessage());
                    updateAllScoreboards(plugin.getSettings().getLobbyTime(), plugin.getSettings().getLobbyTime());
                    lobbyCountdown.cancel();
                    return;
                }
                updateAllScoreboards(countdown, plugin.getSettings().getLobbyTime());
                countdown--;
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, 20L);
    }

    private void startThemeVoting() {
        setBBArenaState(BBArenaState.THEME_VOTING);
        setPlotsToTeams();
        plugin.getPlayerManager().clearInventoryAllPlayersInArena(getArenaInstance());
        themeVotingCountdown = new BukkitRunnable() {
            int countdown = (int) plugin.getSettings().getThemeVotingTime();

            @Override
            public void run() {
                updateAllScoreboards(countdown, (int) plugin.getSettings().getThemeVotingTime());
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
        plugin.getPlayerManager().clearInventoryAllPlayersInArena(this);
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        plugin.getPlayerManager().addPlayedToAllPlayers(this);
        plugin.getOptionsManager().giveAllPlayersItem(this, plugin.getOptionsManager().getOptionsItem(), 8);
        plugin.getPlayerManager().sendStartMessageToAllPlayers(this);
        plugin.getPlayerManager().closeInventoryAllPlayersInArena(this);

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
                    plugin.getPlayerManager().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    plugin.getPlayerManager().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
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
        plugin.getPlayerManager().addPlayedToAllPlayers(this);
        plugin.getPlayerManager().clearInventoryAllPlayersInArena(this);
        setGamemodeToAllPlayers(GameMode.CREATIVE);
        plugin.getOptionsManager().giveAllPlayersItem(this, plugin.getOptionsManager().getOptionsItem(), 8);
        plugin.getPlayerManager().sendStartMessageToAllPlayers(this);
        plugin.getPlayerManager().closeInventoryAllPlayersInArena(this);

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
                    plugin.getPlayerManager().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    plugin.getPlayerManager().sendTitleToAllPlayersInArena(getArenaInstance(), "", Message.GAME_ENDS_IN.getMessage().replaceAll("%time%", new Time(countdown, TimeUnit.SECONDS).toString()));
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
        updateAllScoreboards(0, plugin.getSettings().getEndTime());

        plugin.getPlayerManager().clearInventoryAllPlayersInArena(this);
        plugin.getPlayerManager().addWinsToWinner(this);
        plugin.getPlayerManager().sendResultsToAllPlayers(this);
        plugin.getPlayerManager().setAllPlayersMostPoints(this);

        spawnWinnerFireworks();

        if (plugin.getSettings().isEndCommandValid()) {
            runEndCommands();
        }
        if (!plugin.getSettings().isGiveRewardsAfterGameEnds()) {
            plugin.getRewardManager().giveRewards(this);
        }

        plugin.getArenaManager().setTotalPlayedGames(plugin.getArenaManager().getTotalPlayedGames() + 1);
        endCountdown = new BukkitRunnable() {

            @Override
            public void run() {
                stopArena(Message.ARENA_ENDED.getChatMessage(), false);
            }
        }.runTaskLater(BuildBattle.getInstance(), plugin.getSettings().getEndTime() * 20L);

        BuildBattle.getInstance().getServer().getPluginManager().callEvent(new BBGameEndEvent(getArenaInstance(), getWinner().getTeam()));
    }

    public String getTotalPlayers() {
        return getPlayers().size() + "/" + getMaxPlayers();
    }


    private void startVotingCountdown() {
        setBBArenaState(BBArenaState.VOTING);

        plugin.getPlayerManager().sendTitleToAllPlayersInArena(this, Message.TITLE_VOTING.getMessage(), Message.SUBTITLE_VOTING.getMessage());
        plugin.getPlayerManager().clearInventoryAllPlayersInArena(this);
        plugin.getPlayerManager().setAllPlayersFlying(this);
        plugin.getPlayerManager().removeAllPotionEffects(this);

        setGamemodeToAllPlayers(GameMode.ADVENTURE);

        setVotingPlots();
        if (votingPlots == null) {
            stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
        } else {
            try {
                this.currentVotingPlot = votingPlots.get(0);
            } catch (Exception e) {
                stopArena(Message.NOT_ENOUGH_PLAYERS.getChatMessage(), false);
                return;
            }

            votingCountdown = new BukkitRunnable() {
                int timeLeft = plugin.getSettings().getVotingTime();
                int index = 0;

                @Override
                public void run() {
                    if (timeLeft == plugin.getSettings().getVotingTime()) {
                        teleportAllPlayersToPlot(currentVotingPlot);
                        updateAllScoreboards(timeLeft, plugin.getSettings().getVotingTime());
                        plugin.getPlayerManager().giveVoteItemsAllPlayers(getArenaInstance());
                    }
                    if (timeLeft == 0) {
                        currentVotingPlot.setFinalPoints();
                        try {
                            index += 1;
                            currentVotingPlot = votingPlots.get(index);
                            timeLeft = plugin.getSettings().getVotingTime();
                        } catch (Exception e) {
                            cancel();
                            endGame();
                        }
                        return;
                    } else if (timeLeft < 6) {
                        plugin.getPlayerManager().playSoundToAllPlayers(getArenaInstance(), CompSound.CLICK);
                    }
                    if (currentVotingPlot != null) {
                        plugin.getPlayerManager().setLevelsToAllPlayers(getArenaInstance(), timeLeft);
                        updateAllScoreboards(timeLeft, plugin.getSettings().getVotingTime());
                        if (timeLeft >= 1)
                            plugin.getPlayerManager().sendActionBarToAllPlayers(getArenaInstance(), Message.VOTE_TIME.getMessage().replaceAll("%time%", new Time(timeLeft, TimeUnit.SECONDS).toString()));
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
            plugin.getPlayerManager().sendTitleToAllPlayersInArena(this, Message.VOTING_BUILDER.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        } else {
            plugin.getPlayerManager().sendTitleToAllPlayersInArena(this, Message.VOTING_BUILDERS.getMessage(), plot.getTeam().getPlayersInCommaSeparatedString());
        }
    }

    public boolean isFull() {
        return players.size() == getMaxPlayers();
    }

    public void stopArena(String message, boolean removePlayers) {

        plugin.getPlayerManager().broadcastToAllPlayersInArena(this, message);

        switch (bbArenaState) {
            case LOBBY:
                if (lobbyCountdown != null) {
                    lobbyCountdown.cancel();
                    updateAllScoreboards(plugin.getSettings().getLobbyTime(), plugin.getSettings().getLobbyTime());
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

        if (removePlayers || plugin.getSettings().isRemovePlayersAfterGame()) {
            kickAllPlayers();
            if (plugin.getSettings().isGiveRewardsAfterGameEnds()) {
                plugin.getRewardManager().giveRewards(this);
            }
        } else {
            updateAllScoreboards(plugin.getSettings().getLobbyTime(), plugin.getSettings().getLobbyTime());
            plugin.getPlayerManager().clearInventoryAllPlayersInArena(this);
            plugin.getPlayerManager().teleportAllPlayersToLobby(getArenaInstance());
            setGamemodeToAllPlayers(GameMode.ADVENTURE);
            if (gameMode == BBGameMode.TEAM) {
                plugin.getPlayerManager().giveAllPlayersTeamsItem(getArenaInstance());
            }
            plugin.getPlayerManager().giveAllPlayersLeaveItem(getArenaInstance());
            if (players.size() >= minPlayers) {
                startLobby();
            }
        }

        this.winner = null;
        this.currentVotingPlot = null;
        this.theme = null;
        this.resetAllPlots();
        this.resetAllTeams();

        if (plugin.getSettings().isVotingForThemes()) {
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
                plot.getOptions().setCurrentTime(null, BBPlotTime.NOON, false);
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

            if (plugin.getSettings().isScoreboardEnabled()) {
                playerBoards.remove(p);
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            plugin.getPlayerManager().restorePlayerData(p);

            if (plugin.getSettings().getMainLobbyLocation() != null) {
                plugin.getPlayerManager().teleportToMainLobby(p);
            }

            plugin.getPlayerManager().getPlayersInArenas().remove(p);

            if (plugin.getSettings().isUseBungeecord()) {
                BungeeUtils.connectPlayerToServer(p, plugin.getSettings().getRandomFallbackServer());
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
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".gameTime", gameTime);
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".min_players", minPlayers);
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".mode", gameMode.name());
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".teamSize", teamSize);

        for (BBPlot plot : buildPlots) {
            plugin.getFileManager().getConfig("arenas.yml").set(name + ".plots." + buildPlots.indexOf(plot) + ".min", LocationUtil.getStringFromLocation(plot.getMinPoint()));
            plugin.getFileManager().getConfig("arenas.yml").set(name + ".plots." + buildPlots.indexOf(plot) + ".max", LocationUtil.getStringFromLocation(plot.getMaxPoint()));
        }

        plugin.info("§aArena §e" + name + " §asuccessfully saved into config !");
        plugin.getFileManager().getConfig("arenas.yml").save();
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
        plugin.getArenaManager().refreshArenaItem(this);
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
        plugin.getFileManager().getConfig("arenas.yml").set(name + ".lobbyLocation", LocationUtil.getStringFromLocation(lobbyLocation));
        plugin.getFileManager().getConfig("arenas.yml").save();
    }

    public void delete(CommandSender sender) {
        stopArena(Message.ARENA_REMOVED.getChatMessage(), true);

        plugin.getFileManager().getConfig("arenas.yml").get().set(name, null);
        plugin.getFileManager().getConfig("arenas.yml").save();

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
                if (plugin.getSettings().isVotingForThemes()) {
                    startThemeVoting();
                } else {
                    switch (gameMode) {
                        case TEAM:
                            startGame(plugin.getSettings().getRandomTeamTheme(), true);
                            break;
                        case SOLO:
                            startGame(plugin.getSettings().getRandomSoloTheme(), true);
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
        if (plugin.getSettings().isFairVote())
            plugin.getVotingManager().checkVotes(this);
        Collections.sort(votingPlots);
        Collections.reverse(votingPlots);
    }

    private void spawnWinnerFireworks() {
        if (!plugin.getSettings().isWinFireworksEnabled()) {
            return;
        }
        if (winner != null) {
            new BukkitRunnable() {
                int times = 0;

                @Override
                public void run() {
                    if (times >= plugin.getSettings().getFireworkWaves()) {
                        cancel();
                        return;
                    } else {
                        try {
                            for (Location l : winner.getPlotCorners()) {
                                for (int i = 0; i < plugin.getSettings().getFireworkAmount(); i++) {
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
        if (plugin.getSettings().isScoreboardEnabled()) {
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
        for (String cmd : plugin.getSettings().getEndCommands()) {
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

    @Override
    public List<Player> getSpectators() {
        return spectators;
    }

    @Override
    public void spectate(Player player) {
        if (spectators == null) {
            spectateInventory = Bukkit.createInventory(null, ItemUtil.getInventorySizeBasedOnList(this.players), "Spectating Arena: " + this.name);
            for (Player p : this.players) {
                spectateInventory.addItem(ItemUtil.createPlayerHead(p));
            }
            spectators = new ArrayList<>();
        }

        if (spectators.contains(player)) {
            player.sendMessage(Message.ALREADY_SPECTATING.getChatMessage());
            return;
        }

        player.getInventory().setItem(0, SPECTATE_ITEM);
        player.getInventory().setItem(8, QUIT_SPECTATE_ITEM);
        player.updateInventory();

        player.sendMessage(Message.SPECTATING_ARENA.getChatMessage().replaceAll("%arena%", this.name));
        spectators.add(player);
    }

    @Override
    public void unspectate(Player player) {
        if (spectators == null) {
            return;
        }

        if (!spectators.contains(player)) {
            player.sendMessage(Message.NOT_SPECTATING.getChatMessage());
            return;
        }

        player.getInventory().remove(Spectetable.SPECTATE_ITEM);
        player.getInventory().remove(Spectetable.QUIT_SPECTATE_ITEM);
        player.updateInventory();

        player.sendMessage(Message.NO_LONGER_SPECTATING_ARENA.getChatMessage().replaceAll("%arena%", this.name));
        spectators.remove(player);
    }

    @Override
    public Inventory getSpectateInventory() {
        return spectateInventory;
    }
}
