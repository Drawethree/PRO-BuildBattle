package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.utils.LocationUtil;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static GameManager ourInstance = new GameManager();

    public static GameManager getInstance() {
        return ourInstance;
    }

    private GameManager() {
    }
    private static List<String> soloThemes = new ArrayList<>();
    private static List<String> teamThemes = new ArrayList<>();
    private static List<String> restricedThemes = new ArrayList<>();
    private static List<String> restricedBlocks = new ArrayList<>();
    private static List<String> fallbackServers = new ArrayList<>();
    private static List<String> allowedCommands = new ArrayList<>();
    private static CompMaterial defaultFloorMaterial = CompMaterial.BIRCH_PLANKS;
    private static String prefix = "&8[&eBuildBattlePro&8]&r";
    private static int lobbyTime = 30;
    private static int defaultGameTime = 300;
    private static int themeVotingTime = 15;
    private static int votingTime = 13;
    private static int endTime = 10;
    private static int fireworkWaves = 3;
    private static int fireworkAmount = 5;
    private static int themesToVote = 5;
    private static int maxParticlesPerPlayer = 20;
    private static int partyMaxPlayers = 2;
    private static double particleOffset = 0.5;
    private static int amountParticleToSpawn = 5;
    private static double particleRefreshTime = 1;
    private static List<String> startMessage = new ArrayList<>();
    private static List<String> endMessage = new ArrayList<>();
    private static List<String> themeVotingLore = new ArrayList<>();
    private static List<String> weatherLore = new ArrayList<>();
    private static List<String> finalBannerLore = new ArrayList<>();
    private static List<String> endCommands = new ArrayList<>();
    private static List<String> superVoteLore = new ArrayList<>();
    private static boolean asyncSavePlayerData = false;
    private static StatsType statsType = StatsType.FLATFILE;
    private static boolean scoreboardEnabled = true;
    private static boolean mainLobbyScoreboardEnabled = true;
    private static boolean partiesEnabled = true;
    private static boolean reportsEnabled = true;
    private static boolean removePlayersAfterGame = true;
    private static boolean votingForThemes = true;
    private static boolean changeMOTD = false;
    private static boolean pointsApiRewards = false;
    private static boolean vaultRewards = false;
    private static boolean commandRewards = false;
    private static boolean arenaChat = true;
    private static boolean teamChat = true;
    private static boolean announceNewMostPoints = true;
    private static boolean automaticGrow = true;
    private static boolean showVoteInSubtitle = true;
    private static boolean restrictPlayerMovement = true;
    private static boolean restrictOnlyPlayerYMovement = false;
    private static boolean lockServerOnGameStart = false;
    private static boolean replaceBlockBehindSigns = true;
    private static boolean fairVote = true;
    private static boolean giveRewardsAfterGameEnds = true;
    //PLOT OPTIONS
    private static boolean enableClearPlotOption = true;
    private static boolean enableBannerCreatorOption = true;
    private static boolean enableHeadsOption = true;
    private static boolean enableParticleOption = true;
    private static boolean enableBiomeOption = true;
    private static boolean enabledWeatherOption = true;
    private static boolean enableChangeFloorOption = true;
    private static boolean enableTimeOption = true;
    //
    private static boolean autoRestarting = false;
    private static int autoRestartGamesRequired = -1;
    private static String autoRestartCommand = "NONE";
    private static EntityType floorChangeNPCtype = EntityType.VILLAGER;
    private static Location mainLobbyLocation = null;
    private static boolean teleportToMainLobbyOnJoin = false;

    public static void setLobbyTime(int lobbyTime) {
        if (lobbyTime > 0) {
            GameManager.lobbyTime = lobbyTime;
        } else {
            BuildBattle.warning("§cVariable lobbyTime must be higher than 0 ! Setting it to default (" + getLobbyTime() + ")");
        }
    }

    public static void setThemeVotingTime(int time) {
        if (time > 0) {
            GameManager.themeVotingTime = time;
        } else {
            BuildBattle.warning("§cVariable themeVotingTime must be higher than 0 ! Setting it to default (" + getThemeVotingTime() + ")");
        }
    }

    public static int getLobbyTime() {
        return lobbyTime;
    }

    public static int getDefaultGameTime() {
        return defaultGameTime;
    }

    public static void setDefaultFloorMaterial(CompMaterial defaultFloorMaterial) {
        if (defaultFloorMaterial != null) {
            GameManager.defaultFloorMaterial = defaultFloorMaterial;
        } else {
            BuildBattle.warning("§cVariable default_floor cannot be loaded (maybe it's invalid ?) ! Setting it to default (" + getDefaultFloorMaterial() + ")");
        }
    }

    public static void setDefaultGameTime(int defaultGameTime) {
        if (defaultGameTime > 0) {
            GameManager.defaultGameTime = defaultGameTime;
        } else {
            BuildBattle.warning("§cVariable defaultGameTime must be higher than 0 ! Setting it to default (" + getDefaultGameTime() + ")");
        }
    }

    public static int getVotingTime() {
        return votingTime;
    }

    public static void setVotingTime(int votingTime) {
        if (votingTime > 0) {
            GameManager.votingTime = votingTime;
        } else {
            BuildBattle.warning("§cVariable votingTime must be higher than 0 ! Setting it to default (" + getVotingTime() + ")");
        }
    }

    public static int getEndTime() {
        return endTime;
    }

    public static void setEndTime(int endTime) {
        if (endTime > 0) {
            GameManager.endTime = endTime;
        } else {
            BuildBattle.warning("§cVariable endTime must be higher than 0 ! Setting it to default (" + getEndTime() + ")");
        }
    }

    public static List<String> getRestricedBlocks() {
        return restricedBlocks;
    }

    public static CompMaterial getDefaultFloorMaterial() {
        return defaultFloorMaterial;
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public static void setPrefix(String prefix) {
        if (prefix != null) {
            GameManager.prefix = prefix;
        } else {
            BuildBattle.warning("§cVariable prefix could not be loaded ! Setting it to default (" + getPrefix() + ")");
        }
    }

    public static int getMaxParticlesPerPlayer() {
        return maxParticlesPerPlayer;
    }

    public static void setMaxParticlesPerPlayer(int maxParticlesPerPlayer) {
        if (maxParticlesPerPlayer < 0) {
            BuildBattle.warning("§cVariable particles.max_particles_per_player must be higher or equal 0 ! Setting it to default (" + getMaxParticlesPerPlayer() + ")");
        } else {
            GameManager.maxParticlesPerPlayer = maxParticlesPerPlayer;
        }
    }

    public static double getParticleOffset() {
        return particleOffset;
    }

    public static void setParticleOffset(double particleOffset) {
        GameManager.particleOffset = particleOffset;
    }

    public static int getAmountParticleToSpawn() {
        return amountParticleToSpawn;
    }

    public static void setAmountParticleToSpawn(int amountParticleToSpawn) {
        if (amountParticleToSpawn > 0) {
            GameManager.amountParticleToSpawn = amountParticleToSpawn;
        } else {
            BuildBattle.warning("§cVariable particles.amount_to_spawn must be higher than 0 ! Setting it to default (" + getAmountParticleToSpawn() + ")");
        }
    }

    public static double getParticleRefreshTime() {
        return particleRefreshTime;
    }

    public static void setParticleRefreshTime(double particleRefreshTime) {
        if (particleRefreshTime > 0) {
            GameManager.particleRefreshTime = particleRefreshTime;
        } else {
            BuildBattle.warning("§cVariable particles.refresh_time must be higher than 0 ! Setting it to default (" + getAmountParticleToSpawn() + ")");
        }
    }

    public static List<String> getFallbackServers() {
        return fallbackServers;
    }

    public static void setFallbackServers(List<String> fallbackServers) {
        GameManager.fallbackServers = fallbackServers;
    }

    public static List<String> getStartMessage() {
        return startMessage;
    }

    public static void setStartMessage(List<String> startMessage) {
        if (startMessage != null) {
            GameManager.startMessage = startMessage;
        } else {
            BuildBattle.severe("§cVariable start message could not be loaded !");
        }
    }

    public static boolean isAsyncSavePlayerData() {
        return asyncSavePlayerData;
    }

    public static void setAsyncSavePlayerData(boolean asyncSavePlayerData) {
        GameManager.asyncSavePlayerData = asyncSavePlayerData;
    }

    public static int getFireworkWaves() {
        return fireworkWaves;
    }

    public static void setFireworkWaves(int fireworkWaves) {
        if (fireworkWaves >= 0) {
            GameManager.fireworkWaves = fireworkWaves;
        } else {
            BuildBattle.warning("§cVariable firework_waves must be higher or equal 0 ! Setting it to default (" + getFireworkWaves() + ")");
        }
    }

    public static int getFireworkAmount() {
        return fireworkAmount;
    }

    public static void setFireworkAmount(int fireworkAmount) {
        if (fireworkAmount >= 0) {
            GameManager.fireworkAmount = fireworkAmount;
        } else {
            BuildBattle.warning("§cVariable firework_amount must be higher or equal 0 ! Setting it to default (" + getFireworkAmount() + ")");
        }
    }

    public static boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public static void setScoreboardEnabled(boolean scoreboardEnabled) {
        GameManager.scoreboardEnabled = scoreboardEnabled;
    }

    public static boolean isChangeMOTD() {
        return changeMOTD;
    }

    public static void setChangeMOTD(boolean changeMOTD) {
        GameManager.changeMOTD = changeMOTD;
    }

    public static double getThemeVotingTime() {
        return themeVotingTime;
    }

    public static int getThemesToVote() {
        return themesToVote;
    }

    public static void setThemesToVote(int themesToVote) {
        if (themesToVote > 0 && themesToVote <= 6) {
            GameManager.themesToVote = themesToVote;
        } else {
            BuildBattle.warning("§cVariable themesToVote must be higher than 0 and lower or equal 6 ! Setting it to default (" + getThemesToVote() + ")");
        }
        GameManager.themesToVote = themesToVote;
    }

    public static boolean isVotingForThemes() {
        return votingForThemes;
    }

    public static void setVotingForThemes(boolean votingForThemes) {
        GameManager.votingForThemes = votingForThemes;
    }

    public static StatsType getStatsType() {
        return statsType;
    }

    public static void setStatsType(StatsType statsType) {
        try {
            GameManager.statsType = statsType;
        } catch (Exception e) {
            BuildBattle.warning("§cVariable stats.Type is invalid ! Setting it to default (" + getStatsType() + ")");
        }
    }

    public static boolean isEndCommandValid() {
        if ((getEndCommands() != null) && (!getEndCommands().isEmpty())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPointsApiRewards() {
        return pointsApiRewards;
    }

    public static void setPointsApiRewards(boolean pointsApiRewards) {
        GameManager.pointsApiRewards = pointsApiRewards;
        if (pointsApiRewards) {
            if (BuildBattle.getInstance().getServer().getPluginManager().getPlugin("PointsAPI") == null) {
                BuildBattle.warning("§cYou enabled PointsAPI rewards, but PointsAPI plugin cannot be found ! Disabling PointsAPI rewards...");
                GameManager.pointsApiRewards = false;
            } else {
                BuildBattle.info("§ePointsAPI §arewards enabled!");
            }
        }
    }

    public static boolean isVaultRewards() {
        return vaultRewards;
    }

    public static void setVaultRewards(boolean vaultRewards) {
        GameManager.vaultRewards = vaultRewards;
        if (vaultRewards) {
            if (BuildBattle.getInstance().setupEconomy() == false) {
                BuildBattle.warning("§cYou enabled Vault rewards, but Vault plugin cannot be found ! Disabling Vault rewards...");
                GameManager.vaultRewards = false;
            } else {
                BuildBattle.info("§eVault §arewards enabled!");
            }
        }
    }

    public static List<String> getThemeVotingLore() {
        return themeVotingLore;
    }

    public static void setThemeVotingLore(List<String> themeVotingLore) {
        if (themeVotingLore != null) {
            GameManager.themeVotingLore = themeVotingLore;
        } else {
            BuildBattle.severe("§cVariable gui.theme_voting.themes.lore in messages.yml could not be loaded !");
        }
    }

    public static boolean isReportsEnabled() {
        return reportsEnabled;
    }

    public static void setReportsEnabled(boolean reportsEnabled) {
        GameManager.reportsEnabled = reportsEnabled;
    }

    public static List<String> getWeatherLore() {
        return weatherLore;
    }

    public static void setWeatherLore(List<String> weatherLore) {
        if (weatherLore != null) {
            GameManager.weatherLore = weatherLore;
        } else {
            BuildBattle.severe("§cVariable gui.options.items.change_weather_item.lore in messages.yml could not be loaded !");
        }
    }

    public static List<String> getAllowedCommands() {
        return allowedCommands;
    }

    public static void setAllowedCommands(List<String> allowedCommands) {
        if (allowedCommands != null) {
            GameManager.allowedCommands = allowedCommands;
        } else {
            BuildBattle.severe("§cVariable allowed_commands in config.yml could not be loaded !");
        }
    }

    public static boolean isShowVoteInSubtitle() {
        return showVoteInSubtitle;
    }

    public static void setShowVoteInSubtitle(boolean showVoteInSubtitle) {
        GameManager.showVoteInSubtitle = showVoteInSubtitle;
    }

    public static EntityType getFloorChangeNPCtype() {
        return floorChangeNPCtype;
    }

    public static void setFloorChangeNPCtype(EntityType floorChangeNPCtype) {
        GameManager.floorChangeNPCtype = floorChangeNPCtype;
    }

    public static int getPartyMaxPlayers() {
        return partyMaxPlayers;
    }

    public static void setPartyMaxPlayers(int partyMaxPlayers) {
        if (partyMaxPlayers > 0) {
            if(partyMaxPlayers <= 100) {
                GameManager.partyMaxPlayers = partyMaxPlayers;
            } else {
                BuildBattle.warning("§cVariable parties.max_players can not exceed 100 ! Setting it to default (" + getPartyMaxPlayers() + ")");
            }
        } else {
            BuildBattle.warning("§cVariable parties.max_players must be higher than 0 ! Setting it to default (" + getPartyMaxPlayers() + ")");
        }
    }

    public static boolean isPartiesEnabled() {
        return partiesEnabled;
    }

    public static void setPartiesEnabled(boolean partiesEnabled) {
        GameManager.partiesEnabled = partiesEnabled;
    }

    public static boolean isRestrictPlayerMovement() {
        return restrictPlayerMovement;
    }

    public static void setRestrictPlayerMovement(boolean restrictPlayerMovement) {
        GameManager.restrictPlayerMovement = restrictPlayerMovement;
    }

    public static boolean isRemovePlayersAfterGame() {
        return removePlayersAfterGame;
    }

    public static void setRemovePlayersAfterGame(boolean removePlayersAfterGame) {
        GameManager.removePlayersAfterGame = removePlayersAfterGame;
    }

    public static List<String> getEndMessage() {
        return endMessage;
    }

    public static void setEndMessage(List<String> endMessage) {
        GameManager.endMessage = endMessage;
    }

    public static boolean isAutomaticGrow() {
        return automaticGrow;
    }

    public static void setAutomaticGrow(boolean automaticGrow) {
        GameManager.automaticGrow = automaticGrow;
    }

    public static boolean isLockServerOnGameStart() {
        return lockServerOnGameStart;
    }

    public static void setLockServerOnGameStart(boolean lockServerOnGameStart) {
        GameManager.lockServerOnGameStart = lockServerOnGameStart;
    }

    public static boolean isArenaChat() {
        return arenaChat;
    }

    public static void setArenaChat(boolean arenaChat) {
        GameManager.arenaChat = arenaChat;
    }

    public static boolean isAnnounceNewMostPoints() {
        return announceNewMostPoints;
    }

    public static void setAnnounceNewMostPoints(boolean announceNewMostPoints) {
        GameManager.announceNewMostPoints = announceNewMostPoints;
    }

    public static boolean isReplaceBlockBehindSigns() {
        return replaceBlockBehindSigns;
    }

    public static void setReplaceBlockBehindSigns(boolean replaceBlockBehindSigns) {
        GameManager.replaceBlockBehindSigns = replaceBlockBehindSigns;
    }

    public static boolean isAutoRestarting() {
        return autoRestarting;
    }

    public static void setAutoRestarting(boolean autoRestarting) {
        GameManager.autoRestarting = autoRestarting;
        if (autoRestarting) {
            BuildBattle.info("§aAuto-Restarting >> §eEnabled !");
        }
    }

    public static int getAutoRestartGamesRequired() {
        return autoRestartGamesRequired;
    }

    public static void setAutoRestartGamesRequired(int autoRestartGamesRequired) {
        if (autoRestartGamesRequired > 0) {
            GameManager.autoRestartGamesRequired = autoRestartGamesRequired;
            BuildBattle.info("§aAuto-Restarting >> Games Needed to restart : §e" + autoRestartGamesRequired);
        } else {
            BuildBattle.warning("§cVariable auto-restart.games-needed must be higher than 0 ! Setting it to default (" + getAutoRestartGamesRequired() + ")");
        }
    }

    public static String getAutoRestartCommand() {
        return autoRestartCommand;
    }

    public static void setAutoRestartCommand(String autoRestartCommand) {
        if (autoRestartCommand != null) {
            GameManager.autoRestartCommand = autoRestartCommand;
            BuildBattle.info("§aAuto-Restarting >> Restart command : §e" + autoRestartCommand);
        } else {
            BuildBattle.warning("§cVariable auto-restart.restart-command in config.yml is not set ! Setting it to default (" + getAutoRestartCommand() + ")");
        }
    }

    public static List<String> getFinalBannerLore() {
        return finalBannerLore;
    }

    public static void setFinalBannerLore(List<String> finalBannerLore) {
        if (finalBannerLore != null) {
            GameManager.finalBannerLore = finalBannerLore;
        } else {
            BuildBattle.warning("§cFinal banner lore in messages.yml is empty !");
        }
    }

    public static List<String> getRestricedThemes() {
        return restricedThemes;
    }

    public static void setRestricedThemes(List<String> restricedThemes) {
        if (restricedThemes != null) {
            GameManager.restricedThemes = restricedThemes;
        } else {
            BuildBattle.warning("§cBlacklisted themes in config.yml are empty !");
        }
    }

    public static Location getMainLobbyLocation() {
        return mainLobbyLocation;
    }


    public static void setMainLobbyLocation() {
        try {
            World w = Bukkit.getWorld(BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_lobby.world"));
            double x = BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.x");
            double y = BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.y");
            double z = BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.z");
            float pitch = (float) BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.pitch");
            float yaw = (float) BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.yaw");
            GameManager.mainLobbyLocation = new Location(w,x,y,z,yaw,pitch);
            return;
        } catch (Exception e) {
            try {
                GameManager.mainLobbyLocation = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("config.yml").get().getString("main_lobby"));
                return;
            } catch (Exception e2) {
            }
        }
        BuildBattle.warning("§cMain Lobby Location in config.yml is not set ! If you don't want to use main lobby feature, just ignore this warning :)");
    }

    public static boolean isMainLobbyScoreboardEnabled() {
        return mainLobbyScoreboardEnabled;
    }

    public static void setMainLobbyScoreboardEnabled(boolean mainLobbyScoreboardEnabled) {
        GameManager.mainLobbyScoreboardEnabled = mainLobbyScoreboardEnabled;
    }

    public static List<String> getSoloThemes() {
        return soloThemes;
    }

    public static List<String> getTeamThemes() {
        return teamThemes;
    }

    public static boolean isRestrictOnlyPlayerYMovement() {
        return restrictOnlyPlayerYMovement;
    }

    public static void setRestrictOnlyPlayerYMovement(boolean restrictOnlyPlayerYMovement) {
        GameManager.restrictOnlyPlayerYMovement = restrictOnlyPlayerYMovement;
    }

    public static boolean isEnableClearPlotOption() {
        return enableClearPlotOption;
    }

    public static void setEnableClearPlotOption(boolean enableClearPlotOption) {
        GameManager.enableClearPlotOption = enableClearPlotOption;
    }

    public static boolean isTeamChat() {
        return teamChat;
    }

    public static void setTeamChat(boolean teamChat) {
        GameManager.teamChat = teamChat;
    }

    public static List<String> getSuperVoteLore() {
        return superVoteLore;
    }

    public static void setSuperVoteLore(List<String> superVoteLore) {
        if (superVoteLore != null) {
            GameManager.superVoteLore = superVoteLore;
        } else {
            BuildBattle.severe("§cVariable gui.theme_voting.supervote_item.lore in messages.yml could not be loaded !");
        }
    }

    public static boolean isEnableBannerCreatorOption() {
        return enableBannerCreatorOption;
    }

    public static void setEnableBannerCreatorOption(boolean enableBannerCreatorOption) {
        GameManager.enableBannerCreatorOption = enableBannerCreatorOption;
    }

    public static boolean isEnableHeadsOption() {
        return enableHeadsOption;
    }

    public static void setEnableHeadsOption(boolean enableHeadsOption) {
        GameManager.enableHeadsOption = enableHeadsOption;
    }

    public static boolean isEnableParticleOption() {
        return enableParticleOption;
    }

    public static void setEnableParticleOption(boolean enableParticleOption) {
        GameManager.enableParticleOption = enableParticleOption;
    }

    public static boolean isEnabledWeatherOption() {
        return enabledWeatherOption;
    }

    public static void setEnabledWeatherOption(boolean enabledWeatherOption) {
        GameManager.enabledWeatherOption = enabledWeatherOption;
    }

    public static boolean isEnableBiomeOption() {
        return enableBiomeOption;
    }

    public static void setEnableBiomeOption(boolean enableBiomeOption) {
        GameManager.enableBiomeOption = enableBiomeOption;
    }

    public static boolean isEnableChangeFloorOption() {
        return enableChangeFloorOption;
    }

    public static void setEnableChangeFloorOption(boolean enableChangeFloorOption) {
        GameManager.enableChangeFloorOption = enableChangeFloorOption;
    }

    public static boolean isEnableTimeOption() {
        return enableTimeOption;
    }

    public static void setEnableTimeOption(boolean enableTimeOption) {
        GameManager.enableTimeOption = enableTimeOption;
    }

    public static boolean isCommandRewards() {
        return commandRewards;
    }

    public static void setCommandRewards(boolean commandRewards) {
        GameManager.commandRewards = commandRewards;
    }

    public static List<String> getEndCommands() {
        return endCommands;
    }

    public static void setEndCommands(List<String> endCommands) {
        if(endCommands != null) {
            GameManager.endCommands = endCommands;
        } else {
            BuildBattle.severe("§cEnd-Commands list in config.yml could not be loaded !");
        }
    }

    public static boolean isFairVote() {
        return fairVote;
    }

    public static void setFairVote(boolean fairVote) {
        GameManager.fairVote = fairVote;
    }

    public static boolean isTeleportToMainLobbyOnJoin() {
        return teleportToMainLobbyOnJoin;
    }

    public static void setTeleportToMainLobbyOnJoin(boolean teleportToMainLobbyOnJoin) {
        GameManager.teleportToMainLobbyOnJoin = teleportToMainLobbyOnJoin;
    }

    public static boolean isGiveRewardsAfterGameEnds() {
        return giveRewardsAfterGameEnds;
    }

    public static void setGiveRewardsAfterGameEnds(boolean giveRewardsAfterGameEnds) {
        GameManager.giveRewardsAfterGameEnds = giveRewardsAfterGameEnds;
    }

    public void loadDefaultFloorMaterial() {
        setDefaultFloorMaterial(CompMaterial.fromString(BuildBattle.getFileManager().getConfig("config.yml").get().getString("arena.default_floor")));
    }

    public void loadThemes() {
        soloThemes = new ArrayList<>();
        teamThemes = new ArrayList<>();
        restricedThemes = new ArrayList<>();
        try {
            for (String s : BuildBattle.getFileManager().getConfig("themes.yml").get().getStringList("solo-themes")) {
                soloThemes.add(s);
            }
            for (String s : BuildBattle.getFileManager().getConfig("themes.yml").get().getStringList("team-themes")) {
                teamThemes.add(s);
            }
            for (String s : BuildBattle.getFileManager().getConfig("themes.yml").get().getStringList("blacklisted-themes")) {
                restricedThemes.add(s);
            }
            BuildBattle.info("§aLoaded §e" + soloThemes.size() + " Solo Themes !");
            BuildBattle.info("§aLoaded §e" + teamThemes.size() + " Team Themes !");
            BuildBattle.info("§aLoaded §e" + restricedThemes.size() + " Restriced Themes !");
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading themes from themes.yml !");
            e.printStackTrace();
        }
    }

    public void loadFallbackServers() {
        try {
            List<String> allServers = BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("bungeecord.fallback_servers");
            for (String server : allServers) {
                fallbackServers.add(server);
                BuildBattle.info("§aFallback server §e" + server + " §aloaded !");
            }
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading fallback servers from config!");
            e.printStackTrace();
        }
    }

    public void loadRestrictedBlocks() {
        try {
            for (String s : BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.restriced_blocks")) {
                getRestricedBlocks().add(s);
                BuildBattle.info("§aRestricted block with ID §e" + s + " §aloaded !");
            }
        } catch (Exception e) {
            BuildBattle.severe("§cAn exception occurred while trying loading restriced blocks from config !");
            e.printStackTrace();
        }
    }

    public boolean isThemeOK(String theme) {
        for (String s : restricedThemes) {
            if (s.equalsIgnoreCase(theme)) {
                return false;
            }
        }
        return true;
    }

    public static String getRandomSoloTheme() {
        Random ran = new Random();
        int index = ran.nextInt(soloThemes.size());
        return soloThemes.get(index);
    }
    public static String getRandomTeamTheme() {
        Random ran = new Random();
        int index = ran.nextInt(teamThemes.size());
        return teamThemes.get(index);
    }

    public static String getRandomFallbackServer() {
        Random ran = new Random();
        int index = ran.nextInt(fallbackServers.size());
        return fallbackServers.get(index);
    }

    public void loadArenaPreferences() {
        try {
            setLobbyTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.lobbyTime"));
            setDefaultGameTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.defaultGameTime"));
            setVotingTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.votingTime"));
            setThemeVotingTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themeVotingTime"));
            setEndTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.endTime"));
            setAutomaticGrow(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.automatic_grow"));
            setThemesToVote(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themesToVote"));
            setVotingForThemes(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.voting_for_themes.enabled"));
            setPrefix(BuildBattle.getFileManager().getConfig("config.yml").get().getString("prefix"));
            setEndCommands(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.end_command"));
            setArenaChat(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.arena_chat"));
            setTeamChat(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.team_chat"));
            setFairVote(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.fair_vote.enabled"));
            setRestrictPlayerMovement(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.restrict_player_movement"));
            setRestrictOnlyPlayerYMovement(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.restrict_only_player_Y_movement"));
            setMaxParticlesPerPlayer(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.particles.max_particles_per_player"));
            setParticleOffset(BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.offset"));
            setFireworkAmount(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.amount_per_corner"));
            setFireworkWaves(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.firework_waves"));
            setAmountParticleToSpawn(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.particles.amount_to_spawn"));
            setPartyMaxPlayers(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("parties.max_players"));
            setAnnounceNewMostPoints(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.announce_new_most_points"));
            setEnableClearPlotOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.clear_plot"));
            setEnableBannerCreatorOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.banner_creator"));
            setEnableBiomeOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.biome_selector"));
            setEnableChangeFloorOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.change_floor"));
            setEnabledWeatherOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.weather"));
            setEnableHeadsOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.heads"));
            setEnableTimeOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.time"));
            setEnableParticleOption(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.particles"));
            //setLockServerOnGameStart(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.lock_server_on_game_start"));
            setPartiesEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("parties.enabled"));
            setParticleRefreshTime(BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.refresh_time"));
            setStartMessage(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("messages.start_message"));
            setAllowedCommands(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.allowed_commands"));
            setRemovePlayersAfterGame(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.remove_players_after_game"));
            setEndMessage(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("messages.end_message"));
            setThemeVotingLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.theme_voting.themes.lore"));
            setFinalBannerLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.banner_creator.items.final_banner.lore"));
            setWeatherLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.change_weather_item.lore"));
            setSuperVoteLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.theme_voting.supervote_item.lore"));
            setPointsApiRewards(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.PointsAPI.enabled"));
            setVaultRewards(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.Vault.enabled"));
            setCommandRewards(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.Command.enabled"));
            setGiveRewardsAfterGameEnds(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.give_after_game_ends"));
            setAsyncSavePlayerData(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("stats.async_save_player_data"));
            setStatsType(StatsType.valueOf(BuildBattle.getFileManager().getConfig("config.yml").get().getString("stats.type").toUpperCase()));
            setReportsEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.enable_reports"));
            setShowVoteInSubtitle(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_vote_in_subtitle"));
            setFloorChangeNPCtype(EntityType.valueOf(BuildBattle.getFileManager().getConfig("config.yml").get().getString("change_floor_npc.type")));
            setScoreboardEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_game_scoreboard"));
            setMainLobbyScoreboardEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_main_lobby_scoreboard"));
            setChangeMOTD(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.change_motd"));
            setReplaceBlockBehindSigns(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.replace_block_behind_signs"));
            setAutoRestarting(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("auto-restart.enabled"));

            setMainLobbyLocation();
            if (isAutoRestarting()) {
                setAutoRestartGamesRequired(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("auto-restart.games-needed"));
                setAutoRestartCommand(BuildBattle.getFileManager().getConfig("config.yml").get().getString("auto-restart.restart-command"));
            }
            if(mainLobbyLocation != null) {
                setTeleportToMainLobbyOnJoin(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("teleport_to_main_lobby_on_join"));
            }
        } catch (NullPointerException e) {
            BuildBattle.severe("§cAn exception occurred while loading arena preferences ! Check your config.yml");
            e.printStackTrace();
        }
    }

    public void setMainLobbyLocation(Player p) {
        try {
            Location pLoc = p.getLocation();
            String locString = LocationUtil.getStringFromLocation(pLoc);
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby", null);
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.world", pLoc.getWorld().getName());
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.x", pLoc.getX());
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.y", pLoc.getY());
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.z", pLoc.getZ());
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.pitch", pLoc.getPitch());
            BuildBattle.getFileManager().getConfig("config.yml").set("main_lobby.yaw", pLoc.getYaw()).save();
            p.sendMessage("§e§lBuildBattle Setup §8| §aMain lobby location set to §e" + locString);
            mainLobbyLocation = pLoc;
        } catch (Exception e) {
            p.sendMessage("§e§lBuildBattle Setup §8| §cOops ! Something went wrong while setting main lobby ! Check console please.");
            BuildBattle.severe("§cAn exception occurred while setting main lobby !");
            e.printStackTrace();
        }
    }

    public static void runEndCommands(BBArena bbArena) {
        for(String cmd : endCommands) {
            for (Player p : bbArena.getPlayers()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", p.getName()));
            }
        }
    }
}
