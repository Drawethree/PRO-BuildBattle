package me.drawe.buildbattle.managers;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.utils.LocationUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class BBSettings {

    private List<String> soloThemes;
    private List<String> teamThemes;
    private List<String> restricedThemes;
    private List<Material> restricedBlocks;
    private List<String> fallbackServers;
    private List<String> allowedCommands;
    private CompMaterial defaultFloorMaterial;
    private String prefix;
    private int lobbyTime;
    private int defaultGameTime;
    private int themeVotingTime;
    private int votingTime;
    private int endTime;
    private int fireworkWaves;
    private int fireworkAmount;
    private int themesToVote;
    private int maxParticlesPerPlayer;
    private int partyMaxPlayers;
    private int leaderBoardsRefreshTime;
    private double particleOffset;
    private int amountParticleToSpawn;
    private double particleRefreshTime;
    private List<String> startMessage;
    private List<String> endMessage;
    private List<String> themeVotingLore;
    private List<String> weatherLore;
    private List<String> finalBannerLore;
    private List<String> endCommands;
    private List<String> superVoteLore;
    private boolean asyncSavePlayerData;
    private StatsType statsType;
    private boolean scoreboardEnabled;
    private boolean mainLobbyScoreboardEnabled;
    private boolean partiesEnabled;
    private boolean reportsEnabled;
    private boolean removePlayersAfterGame;
    private boolean votingForThemes;
    private boolean superVotesEnabled;
    private boolean changeMOTD;
    private boolean pointsApiRewards;
    private boolean vaultRewards;
    private boolean commandRewards;
    private boolean arenaChat;
    private boolean teamChat;
    private boolean announceNewMostPoints;
    private boolean automaticGrow;
    private boolean showVoteInSubtitle;
    private boolean restrictPlayerMovement;
    private boolean restrictOnlyPlayerYMovement;
    private boolean lockServerOnGameStart;
    private boolean replaceBlockBehindSigns;
    private boolean fairVote;
    private boolean giveRewardsAfterGameEnds;
    private boolean winFireworksEnabled;
    //PLOT OPTIONS
    private boolean enableClearPlotOption;
    private boolean enableBannerCreatorOption;
    private boolean enableHeadsOption;
    private boolean enableParticleOption;
    private boolean enableBiomeOption;
    private boolean enabledWeatherOption;
    private boolean enableChangeFloorOption;
    private boolean enableTimeOption;
    //
    private boolean autoRestarting;
    private int autoRestartGamesRequired;
    private String autoRestartCommand;
    private EntityType floorChangeNPCtype;
    private Location mainLobbyLocation;
    private boolean teleportToMainLobbyOnJoin;

    private boolean useBungeecord;
    private boolean autoJoinPlayers;
    private boolean loadPluginLater;
    private int loadAfter;

    private BuildBattle plugin;


    public BBSettings(BuildBattle plugin) {
        this.plugin = plugin;
        soloThemes = new ArrayList<>();
        teamThemes = new ArrayList<>();
        restricedThemes = new ArrayList<>();
        restricedBlocks = new ArrayList<>();
        fallbackServers = new ArrayList<>();
        allowedCommands = new ArrayList<>();
        defaultFloorMaterial = CompMaterial.BIRCH_PLANKS;
        prefix = "§8[§eBuildBattlePro§8]§r ";
        lobbyTime = 30;
        defaultGameTime = 300;
        themeVotingTime = 15;
        votingTime = 13;
        endTime = 10;
        fireworkWaves = 3;
        fireworkAmount = 5;
        themesToVote = 5;
        maxParticlesPerPlayer = 20;
        partyMaxPlayers = 2;
        particleOffset = 0.5;
        amountParticleToSpawn = 5;
        particleRefreshTime = 1;
        startMessage = new ArrayList<>();
        endMessage = new ArrayList<>();
        themeVotingLore = new ArrayList<>();
        weatherLore = new ArrayList<>();
        finalBannerLore = new ArrayList<>();
        endCommands = new ArrayList<>();
        superVoteLore = new ArrayList<>();
        asyncSavePlayerData = false;
        statsType = StatsType.FLATFILE;
        scoreboardEnabled = true;
        mainLobbyScoreboardEnabled = true;
        partiesEnabled = true;
        reportsEnabled = true;
        removePlayersAfterGame = true;
        votingForThemes = true;
        superVotesEnabled = true;
        changeMOTD = false;
        pointsApiRewards = false;
        vaultRewards = false;
        commandRewards = false;
        arenaChat = true;
        teamChat = true;
        announceNewMostPoints = true;
        automaticGrow = true;
        showVoteInSubtitle = true;
        restrictPlayerMovement = true;
        restrictOnlyPlayerYMovement = false;
        lockServerOnGameStart = false;
        replaceBlockBehindSigns = true;
        fairVote = true;
        giveRewardsAfterGameEnds = true;
        winFireworksEnabled = true;
        enableClearPlotOption = true;
        enableBannerCreatorOption = true;
        enableHeadsOption = true;
        enableParticleOption = true;
        enableBiomeOption = true;
        enabledWeatherOption = true;
        enableChangeFloorOption = true;
        enableTimeOption = true;
        autoRestarting = false;
        autoRestartGamesRequired = -1;
        autoRestartCommand = "NONE";
        floorChangeNPCtype = EntityType.VILLAGER;
        mainLobbyLocation = null;
        teleportToMainLobbyOnJoin = false;
        useBungeecord = false;
        autoJoinPlayers = false;
        loadPluginLater = false;
        loadAfter = 0;
    }

    private void setLobbyTime(int lobbyTime) {
        if (lobbyTime > 0) {
            this.lobbyTime = lobbyTime;
        } else {
            this.plugin.warning("§cVariable lobbyTime must be higher than 0 ! Setting it to default (" + this.lobbyTime + ")");
        }
    }

    private void setThemeVotingTime(int time) {
        if (time > 0) {
            this.themeVotingTime = time;
        } else {
            this.plugin.warning("§cVariable themeVotingTime must be higher than 0 ! Setting it to default (" + this.themeVotingTime + ")");
        }
    }

    private void setDefaultFloorMaterial(CompMaterial defaultFloorMaterial) {
        if (defaultFloorMaterial != null) {
            this.defaultFloorMaterial = defaultFloorMaterial;
        } else {
            this.plugin.warning("§cVariable default_floor cannot be loaded (maybe it's invalid ?) ! Setting it to default (" + this.defaultFloorMaterial + ")");
        }
    }

    private void setDefaultGameTime(int defaultGameTime) {
        if (defaultGameTime > 0) {
            this.defaultGameTime = defaultGameTime;
        } else {
            this.plugin.warning("§cVariable defaultGameTime must be higher than 0 ! Setting it to default (" + this.defaultGameTime + ")");
        }
    }


    private void setVotingTime(int votingTime) {
        if (votingTime > 0) {
            this.votingTime = votingTime;
        } else {
            this.plugin.warning("§cVariable votingTime must be higher than 0 ! Setting it to default (" + this.votingTime + ")");
        }
    }

    private void setEndTime(int endTime) {
        if (endTime > 0) {
            this.endTime = endTime;
        } else {
            this.plugin.warning("§cVariable endTime must be higher than 0 ! Setting it to default (" + this.endTime + ")");
        }
    }

    private void setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        } else {
            this.plugin.warning("§cVariable prefix could not be loaded ! Setting it to default (" + this.prefix + ")");
        }
    }


    private void setMaxParticlesPerPlayer(int maxParticlesPerPlayer) {
        if (maxParticlesPerPlayer < 0) {
            this.plugin.warning("§cVariable particles.max_particles_per_player must be higher or equal 0 ! Setting it to default (" + this.maxParticlesPerPlayer + ")");
        } else {
            this.maxParticlesPerPlayer = maxParticlesPerPlayer;
        }
    }


    private void setParticleOffset(double particleOffset) {
        this.particleOffset = particleOffset;
    }


    private void setAmountParticleToSpawn(int amountParticleToSpawn) {
        if (amountParticleToSpawn > 0) {
            this.amountParticleToSpawn = amountParticleToSpawn;
        } else {
            this.plugin.warning("§cVariable particles.amount_to_spawn must be higher than 0 ! Setting it to default (" + this.amountParticleToSpawn + ")");
        }
    }

    private void setParticleRefreshTime(double particleRefreshTime) {
        if (particleRefreshTime > 0) {
            this.particleRefreshTime = particleRefreshTime;
        } else {
            this.plugin.warning("§cVariable particles.refresh_time must be higher than 0 ! Setting it to default (" + this.particleRefreshTime + ")");
        }
    }

    private void setStartMessage(List<String> startMessage) {
        if (startMessage != null) {
            this.startMessage = startMessage;
        } else {
            this.plugin.severe("§cVariable start message could not be loaded !");
        }
    }

    private void setAsyncSavePlayerData(boolean asyncSavePlayerData) {
        this.asyncSavePlayerData = asyncSavePlayerData;
    }

    private void setFireworkWaves(int fireworkWaves) {
        if (fireworkWaves >= 0) {
            this.fireworkWaves = fireworkWaves;
        } else {
            this.plugin.warning("§cVariable firework_waves must be higher or equal 0 ! Setting it to default (" + this.fireworkWaves + ")");
        }
    }

    private void setFireworkAmount(int fireworkAmount) {
        if (fireworkAmount >= 0) {
            this.fireworkAmount = fireworkAmount;
        } else {
            this.plugin.warning("§cVariable firework_amount must be higher or equal 0 ! Setting it to default (" + this.fireworkAmount + ")");
        }
    }


    private void setScoreboardEnabled(boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }


    private void setChangeMOTD(boolean changeMOTD) {
        this.changeMOTD = changeMOTD;
    }

    private void setThemesToVote(int themesToVote) {
        if (themesToVote > 0 && themesToVote <= 6) {
            this.themesToVote = themesToVote;
        } else {
            this.plugin.warning("§cVariable themesToVote must be higher than 0 and lower or equal 6 ! Setting it to default (" + this.themesToVote + ")");
        }
    }

    private void setVotingForThemes(boolean votingForThemes) {
        this.votingForThemes = votingForThemes;
    }


    private void setStatsType(StatsType statsType) {
        try {
            this.statsType = statsType;
        } catch (Exception e) {
            this.plugin.warning("§cVariable stats.Type is invalid ! Setting it to default (" + this.statsType.name() + ")");
        }
    }

    public boolean isEndCommandValid() {
        if ((endCommands != null) && (!endCommands.isEmpty())) {
            return true;
        } else {
            return false;
        }
    }


    private void setPointsApiRewards(boolean pointsApiRewards) {
        this.pointsApiRewards = pointsApiRewards;
        if (pointsApiRewards) {
            if (this.plugin.getServer().getPluginManager().getPlugin("PointsAPI") == null) {
                this.plugin.warning("§cYou enabled PointsAPI rewards, but PointsAPI plugin cannot be found ! Disabling PointsAPI rewards...");
                this.pointsApiRewards = false;
            } else {
                this.plugin.info("§ePointsAPI §arewards enabled!");
            }
        }
    }

    private void setVaultRewards(boolean vaultRewards) {
        this.vaultRewards = vaultRewards;
        if (vaultRewards) {
            if (this.plugin.setupEconomy() == false) {
                this.plugin.warning("§cYou enabled Vault rewards, but Vault plugin cannot be found ! Disabling Vault rewards...");
                this.vaultRewards = false;
            } else {
                this.plugin.info("§eVault §arewards enabled!");
            }
        }
    }

    private void setThemeVotingLore(List<String> themeVotingLore) {
        if (themeVotingLore != null) {
            this.themeVotingLore = themeVotingLore;
        } else {
            this.plugin.severe("§cVariable gui.theme_voting.themes.lore in translates.yml could not be loaded !");
        }
    }


    private void setReportsEnabled(boolean reportsEnabled) {
        this.reportsEnabled = reportsEnabled;
    }

    private void setWeatherLore(List<String> weatherLore) {
        if (weatherLore != null) {
            this.weatherLore = weatherLore;
        } else {
            this.plugin.severe("§cVariable gui.options.items.change_weather_item.lore in translates.yml could not be loaded !");
        }
    }


    private void setAllowedCommands(List<String> allowedCommands) {
        if (allowedCommands != null) {
            this.allowedCommands = allowedCommands;
        } else {
            this.plugin.severe("§cVariable allowed_commands in config.yml could not be loaded !");
        }
    }


    private void setShowVoteInSubtitle(boolean showVoteInSubtitle) {
        this.showVoteInSubtitle = showVoteInSubtitle;
    }

    private void setFloorChangeNPCtype(EntityType floorChangeNPCtype) {
        this.floorChangeNPCtype = floorChangeNPCtype;
    }

    private void setPartyMaxPlayers(int partyMaxPlayers) {
        if (partyMaxPlayers > 0) {
            if (partyMaxPlayers <= 100) {
                this.partyMaxPlayers = partyMaxPlayers;
            } else {
                this.plugin.warning("§cVariable parties.max_players can not exceed 100 ! Setting it to default (" + this.partyMaxPlayers + ")");
            }
        } else {
            this.plugin.warning("§cVariable parties.max_players must be higher than 0 ! Setting it to default (" + this.partyMaxPlayers + ")");
        }
    }


    private void setPartiesEnabled(boolean partiesEnabled) {
        this.partiesEnabled = partiesEnabled;
    }

    private void setRestrictPlayerMovement(boolean restrictPlayerMovement) {
        this.restrictPlayerMovement = restrictPlayerMovement;
    }


    private void setRemovePlayersAfterGame(boolean removePlayersAfterGame) {
        this.removePlayersAfterGame = removePlayersAfterGame;
    }


    private void setEndMessage(List<String> endMessage) {
        this.endMessage = endMessage;
    }


    private void setAutomaticGrow(boolean automaticGrow) {
        this.automaticGrow = automaticGrow;
    }

    private void setLockServerOnGameStart(boolean lockServerOnGameStart) {
        this.lockServerOnGameStart = lockServerOnGameStart;
    }

    private void setArenaChat(boolean arenaChat) {
        this.arenaChat = arenaChat;
    }

    private void setAnnounceNewMostPoints(boolean announceNewMostPoints) {
        this.announceNewMostPoints = announceNewMostPoints;
    }


    private void setReplaceBlockBehindSigns(boolean replaceBlockBehindSigns) {
        this.replaceBlockBehindSigns = replaceBlockBehindSigns;
    }

    private void setAutoRestarting(boolean autoRestarting) {
        this.autoRestarting = autoRestarting;
        if (autoRestarting) {
            this.plugin.info("§aAuto-Restarting >> §eEnabled !");
        }
    }


    private void setAutoRestartGamesRequired(int autoRestartGamesRequired) {
        if (autoRestartGamesRequired > 0) {
            this.autoRestartGamesRequired = autoRestartGamesRequired;
            this.plugin.info("§aAuto-Restarting >> Games Needed to restart : §e" + autoRestartGamesRequired);
        } else {
            this.plugin.warning("§cVariable auto-restart.games-needed must be higher than 0 ! Setting it to default (" + this.autoRestartGamesRequired + ")");
        }
    }


    private void setAutoRestartCommand(String autoRestartCommand) {
        if (autoRestartCommand != null) {
            this.autoRestartCommand = autoRestartCommand;
            this.plugin.info("§aAuto-Restarting >> Restart command : §e" + autoRestartCommand);
        } else {
            this.plugin.warning("§cVariable auto-restart.restart-command in config.yml is not set ! Setting it to default (" + this.autoRestartCommand + ")");
        }
    }


    private void setFinalBannerLore(List<String> finalBannerLore) {
        if (finalBannerLore != null) {
            this.finalBannerLore = finalBannerLore;
        } else {
            this.plugin.warning("§cFinal banner lore in translates.yml is empty !");
        }
    }


    private void setRestricedThemes(List<String> restricedThemes) {
        if (restricedThemes != null) {
            this.restricedThemes = restricedThemes;
        } else {
            this.plugin.warning("§cBlacklisted themes in config.yml are empty !");
        }
    }


    private void setMainLobbyLocation() {
        try {
            World w = Bukkit.getWorld(this.plugin.getFileManager().getConfig("config.yml").get().getString("main_lobby.world"));
            double x = this.plugin.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.x");
            double y = this.plugin.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.y");
            double z = this.plugin.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.z");
            float pitch = (float) this.plugin.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.pitch");
            float yaw = (float) this.plugin.getFileManager().getConfig("config.yml").get().getDouble("main_lobby.yaw");
            this.mainLobbyLocation = new Location(w, x, y, z, yaw, pitch);
            return;
        } catch (Exception e) {
            try {
                this.mainLobbyLocation = LocationUtil.getLocationFromString(this.plugin.getFileManager().getConfig("config.yml").get().getString("main_lobby"));
                return;
            } catch (Exception e2) {
            }
        }
        this.plugin.warning("§cMain Lobby Location in config.yml is not set ! If you don't want to use main lobby feature, just ignore this warning :)");
    }


    private void setMainLobbyScoreboardEnabled(boolean mainLobbyScoreboardEnabled) {
        this.mainLobbyScoreboardEnabled = mainLobbyScoreboardEnabled;
    }


    private void setRestrictOnlyPlayerYMovement(boolean restrictOnlyPlayerYMovement) {
        this.restrictOnlyPlayerYMovement = restrictOnlyPlayerYMovement;
    }

    private void setEnableClearPlotOption(boolean enableClearPlotOption) {
        this.enableClearPlotOption = enableClearPlotOption;
    }


    private void setTeamChat(boolean teamChat) {
        this.teamChat = teamChat;
    }

    private void setSuperVoteLore(List<String> superVoteLore) {
        if (superVoteLore != null) {
            this.superVoteLore = superVoteLore;
        } else {
            this.plugin.severe("§cVariable gui.theme_voting.supervote_item.lore in translates.yml could not be loaded !");
        }
    }


    private void setEnableBannerCreatorOption(boolean enableBannerCreatorOption) {
        this.enableBannerCreatorOption = enableBannerCreatorOption;
    }


    private void setEnableHeadsOption(boolean enableHeadsOption) {
        this.enableHeadsOption = enableHeadsOption;
    }


    private void setEnableParticleOption(boolean enableParticleOption) {
        this.enableParticleOption = enableParticleOption;
    }


    private void setEnabledWeatherOption(boolean enabledWeatherOption) {
        this.enabledWeatherOption = enabledWeatherOption;
    }


    private void setEnableBiomeOption(boolean enableBiomeOption) {
        this.enableBiomeOption = enableBiomeOption;
    }


    private void setEnableChangeFloorOption(boolean enableChangeFloorOption) {
        this.enableChangeFloorOption = enableChangeFloorOption;
    }

    private void setEnableTimeOption(boolean enableTimeOption) {
        this.enableTimeOption = enableTimeOption;
    }

    private void setCommandRewards(boolean commandRewards) {
        this.commandRewards = commandRewards;
    }


    private void setEndCommands(List<String> endCommands) {
        if (endCommands != null) {
            this.endCommands = endCommands;
        } else {
            this.plugin.severe("§cEnd-Commands list in config.yml could not be loaded !");
        }
    }


    private void setFairVote(boolean fairVote) {
        this.fairVote = fairVote;
    }

    private void setTeleportToMainLobbyOnJoin(boolean teleportToMainLobbyOnJoin) {
        this.teleportToMainLobbyOnJoin = teleportToMainLobbyOnJoin;
    }

    private void setGiveRewardsAfterGameEnds(boolean giveRewardsAfterGameEnds) {
        this.giveRewardsAfterGameEnds = giveRewardsAfterGameEnds;
    }

    private void loadThemes() {
        this.soloThemes = new ArrayList<>();
        this.teamThemes = new ArrayList<>();
        this.restricedThemes = new ArrayList<>();

        try {
            for (String s : this.plugin.getFileManager().getConfig("themes.yml").get().getStringList("solo-themes")) {
                this.soloThemes.add(s);
            }
            for (String s : this.plugin.getFileManager().getConfig("themes.yml").get().getStringList("team-themes")) {
                this.teamThemes.add(s);
            }
            for (String s : this.plugin.getFileManager().getConfig("themes.yml").get().getStringList("blacklisted-themes")) {
                this.restricedThemes.add(s);
            }
            this.plugin.info("§aLoaded §e" + soloThemes.size() + " Solo Themes !");
            this.plugin.info("§aLoaded §e" + teamThemes.size() + " Team Themes !");
            this.plugin.info("§aLoaded §e" + restricedThemes.size() + " Restriced Themes !");
        } catch (Exception e) {
            this.plugin.severe("§cAn exception occurred while trying loading themes from themes.yml !");
            e.printStackTrace();
        }
    }

    private void loadFallbackServers() {
        try {
            this.fallbackServers = this.plugin.getFileManager().getConfig("config.yml").get().getStringList("bungeecord.fallback_servers");
            this.plugin.info("§aFallback servers loaded !");
        } catch (Exception e) {
            this.plugin.severe("§cAn exception occurred while trying loading fallback servers from config!");
            e.printStackTrace();
        }
    }

    private void loadRestrictedBlocks() {
        try {
            this.restricedBlocks = new ArrayList<>();
            List<String> strings = this.plugin.getFileManager().getConfig("config.yml").get().getStringList("arena.restriced_blocks");

            for(String s : strings) {
                CompMaterial mat = CompMaterial.fromString(s);

                if(mat == null)  {
                    this.plugin.warning("§cMaterial " + s + " is not a valid material for your server version ! Use materials from here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                    continue;
                }

                this.restricedBlocks.add(mat.toMaterial());
            }

            this.plugin.info("§aRestricted blocks loaded !");
        } catch (Exception e) {
            this.plugin.severe("§cAn exception occurred while trying loading restriced blocks from config !");
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

    public String getRandomSoloTheme() {
        return soloThemes.get(new Random().nextInt(soloThemes.size()));
    }

    public String getRandomTeamTheme() {
        return teamThemes.get(new Random().nextInt(teamThemes.size()));
    }

    public String getRandomFallbackServer() {
        return fallbackServers.get(new Random().nextInt(fallbackServers.size()));
    }

    public void loadSettings() {
        try {
            setUseBungeecord(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.use_bungee"));
            setAutoJoinPlayers(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.auto_join_players"));
            setLoadPluginLater(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("plugin_loading.load_plugin_later"));
            setLoadAfter(this.plugin.getFileManager().getConfig("config.yml").get().getInt("plugin_loading.load_after"));
            setLobbyTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.lobbyTime"));
            setDefaultGameTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.defaultGameTime"));
            setDefaultFloorMaterial(CompMaterial.fromStringStrict(this.plugin.getFileManager().getConfig("config.yml").get().getString("arena.default_floor")));
            setVotingTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.votingTime"));
            setThemeVotingTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themeVotingTime"));
            setEndTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.endTime"));
            setAutomaticGrow(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.automatic_grow"));
            setThemesToVote(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themesToVote"));
            setVotingForThemes(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.voting_for_themes.enabled"));
            setSuperVotesEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.voting_for_themes.super_votes_enabled"));
            setPrefix(this.plugin.getFileManager().getConfig("config.yml").get().getString("prefix"));
            setEndCommands(this.plugin.getFileManager().getConfig("config.yml").get().getStringList("arena.end_command"));
            setArenaChat(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.arena_chat"));
            setTeamChat(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.team_chat"));
            setFairVote(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.fair_vote.enabled"));
            setRestrictPlayerMovement(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.restrict_player_movement"));
            setRestrictOnlyPlayerYMovement(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.restrict_only_player_Y_movement"));
            setMaxParticlesPerPlayer(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.particles.max_particles_per_player"));
            setParticleOffset(this.plugin.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.offset"));
            setFireworkAmount(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.amount_per_corner"));
            setFireworkWaves(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.firework_waves"));
            setWinFireworksEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.win_fireworks.enabled"));
            setAmountParticleToSpawn(this.plugin.getFileManager().getConfig("config.yml").get().getInt("arena.particles.amount_to_spawn"));
            setPartyMaxPlayers(this.plugin.getFileManager().getConfig("config.yml").get().getInt("parties.max_players"));
            setAnnounceNewMostPoints(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.announce_new_most_points"));
            setEnableClearPlotOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.clear_plot"));
            setEnableBannerCreatorOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.banner_creator"));
            setEnableBiomeOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.biome_selector"));
            setEnableChangeFloorOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.change_floor"));
            setEnabledWeatherOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.weather"));
            setEnableHeadsOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.heads"));
            setEnableTimeOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.time"));
            setEnableParticleOption(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.plot_options.particles"));
            setPartiesEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("parties.enabled"));
            setParticleRefreshTime(this.plugin.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.refresh_time"));
            setStartMessage(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("messages.start_message"));
            setAllowedCommands(this.plugin.getFileManager().getConfig("config.yml").get().getStringList("arena.allowed_commands"));
            setRemovePlayersAfterGame(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.remove_players_after_game"));
            setEndMessage(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("messages.end_message"));
            setThemeVotingLore(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.theme_voting.themes.lore"));
            setFinalBannerLore(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.banner_creator.items.final_banner.lore"));
            setWeatherLore(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.change_weather_item.lore"));
            setSuperVoteLore(this.plugin.getFileManager().getConfig("translates.yml").get().getStringList("gui.theme_voting.supervote_item.lore"));
            setPointsApiRewards(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("rewards.PointsAPI.enabled"));
            setVaultRewards(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("rewards.Vault.enabled"));
            setCommandRewards(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("rewards.Command.enabled"));
            setGiveRewardsAfterGameEnds(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("rewards.give_after_game_ends"));
            setAsyncSavePlayerData(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("stats.async_save_player_data"));
            setStatsType(StatsType.valueOf(this.plugin.getFileManager().getConfig("config.yml").get().getString("stats.type").toUpperCase()));
            setReportsEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.enable_reports"));
            setShowVoteInSubtitle(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_vote_in_subtitle"));
            setFloorChangeNPCtype(EntityType.valueOf(this.plugin.getFileManager().getConfig("config.yml").get().getString("change_floor_npc.type")));
            setScoreboardEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_game_scoreboard"));
            setMainLobbyScoreboardEnabled(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_main_lobby_scoreboard"));
            setChangeMOTD(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.change_motd"));
            setReplaceBlockBehindSigns(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("arena.replace_block_behind_signs"));
            setAutoRestarting(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("auto-restart.enabled"));
            setLeaderBoardsRefreshTime(this.plugin.getFileManager().getConfig("config.yml").get().getInt("leaderboards_refresh_time"));
            setMainLobbyLocation();
            loadFallbackServers();
            loadRestrictedBlocks();
            loadThemes();
            if (isAutoRestarting()) {
                setAutoRestartGamesRequired(this.plugin.getFileManager().getConfig("config.yml").get().getInt("auto-restart.games-needed"));
                setAutoRestartCommand(this.plugin.getFileManager().getConfig("config.yml").get().getString("auto-restart.restart-command"));
            }
            if (mainLobbyLocation != null) {
                setTeleportToMainLobbyOnJoin(this.plugin.getFileManager().getConfig("config.yml").get().getBoolean("teleport_to_main_lobby_on_join"));
            }

        } catch (NullPointerException e) {
            this.plugin.severe("§cAn exception occurred while loading arena preferences ! Check your config.yml");
            e.printStackTrace();
        }
    }

    public void setMainLobbyLocation(Player p) {
        try {
            Location pLoc = p.getLocation();
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby", null);
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.world", pLoc.getWorld().getName());
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.x", pLoc.getX());
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.y", pLoc.getY());
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.z", pLoc.getZ());
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.pitch", pLoc.getPitch());
            this.plugin.getFileManager().getConfig("config.yml").set("main_lobby.yaw", pLoc.getYaw()).save();
            p.sendMessage("§e§lBuildBattle Setup §8| §aMain lobby location set to §e" + LocationUtil.getStringFromLocationXYZ(pLoc));
            mainLobbyLocation = pLoc;
        } catch (Exception e) {
            p.sendMessage("§e§lBuildBattle Setup §8| §cOops ! Something went wrong while setting main lobby ! Check console please.");
            this.plugin.severe("§cAn exception occurred while setting main lobby !");
            e.printStackTrace();
        }
    }

    private void setUseBungeecord(boolean useBungeecord) {
        this.useBungeecord = useBungeecord;
        if (useBungeecord) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§aBungeeCord system for BuildBattle loaded !");
            this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin);
            this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(this.plugin);
            this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
            this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "BungeeCord", this.plugin);
        }
    }

    private void setAutoJoinPlayers(boolean autoJoinPlayers) {
        this.autoJoinPlayers = autoJoinPlayers;
    }

    private void setLoadPluginLater(boolean loadPluginLater) {
        this.loadPluginLater = loadPluginLater;
    }

    private void setLoadAfter(int loadAfter) {
        this.loadAfter = loadAfter;
    }

    private void setSuperVotesEnabled(boolean superVotesEnabled) {
        this.superVotesEnabled = superVotesEnabled;
    }

    private void setWinFireworksEnabled(boolean winFireworksEnabled) {
        this.winFireworksEnabled = winFireworksEnabled;
    }

    public void setLeaderBoardsRefreshTime(int leaderBoardsRefreshTime) {
        this.leaderBoardsRefreshTime = leaderBoardsRefreshTime;
    }
}
