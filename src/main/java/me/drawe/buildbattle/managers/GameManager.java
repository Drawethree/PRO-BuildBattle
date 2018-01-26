package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.BBSign;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.*;
import org.bukkit.entity.EntityType;

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

    private static List<String> themes = new ArrayList<>();
    private static List<String> restricedBlocks = new ArrayList<>();
    private static List<String> fallbackServers = new ArrayList<>();
    private static List<String> allowedCommands = new ArrayList<>();
    private static List<BBArena> arenas = new ArrayList<>();
    private static String defaultFloorMaterial = "2:0";
    private static String prefix = "&8[&eBuildBattlePro&8]&r";

    private static int lobbyTime = 30;
    private static int gameTime = 300;
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
    private static boolean asyncSavePlayerData = false;
    private static StatsType statsType = StatsType.FLATFILE;
    private static boolean scoreboardEnabled = true;
    private static boolean partiesEnabled = true;
    private static boolean reportsEnabled = true;
    private static boolean removePlayersAfterGame = true;
    private static boolean votingForThemes = true;
    private static boolean changeMOTD = false;
    private static boolean pointsApiRewards = false;
    private static boolean vaultRewards = false;
    private static boolean automaticGrow = true;
    private static boolean showVoteInSubtitle = true;
    private static boolean restrictPlayerMovement = true;
    private static String endCommand = "NONE";
    private static EntityType floorChangeNPCtype = EntityType.VILLAGER;

    public static void setLobbyTime(int lobbyTime) {
        if(lobbyTime > 0) {
            GameManager.lobbyTime = lobbyTime;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable lobbyTime must be higher than 0 ! Setting it to default (" + getLobbyTime() + ")");
        }
    }

    public static void setThemeVotingTime(int time) {
        if(time > 0) {
            GameManager.themeVotingTime = time;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable themeVotingTime must be higher than 0 ! Setting it to default (" + getThemeVotingTime() + ")");
        }
    }

    public static List<String> getThemes() {
        return themes;
    }

    public static int getLobbyTime() {
        return lobbyTime;
    }

    public static int getGameTime() {
        return gameTime;
    }

    public static void setDefaultFloorMaterial(String defaultFloorMaterial) {
        if(defaultFloorMaterial != null) {
            GameManager.defaultFloorMaterial = defaultFloorMaterial;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable default_floor cannot be loaded ! Setting it to default (" + getDefaultFloorMaterial() + ")");
        }
    }

    public static void setGameTime(int gameTime) {
        if(gameTime > 0) {
            GameManager.gameTime = gameTime;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable gameTime must be higher than 0 ! Setting it to default (" + getGameTime() + ")");
        }
    }

    public static int getVotingTime() {
        return votingTime;
    }

    public static void setVotingTime(int votingTime) {
        if(votingTime > 0) {
            GameManager.votingTime = votingTime;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable votingTime must be higher than 0 ! Setting it to default (" + getVotingTime() + ")");
        }
    }

    public static int getEndTime() {
        return endTime;
    }

    public static void setEndTime(int endTime) {
        if(endTime > 0) {
            GameManager.endTime = endTime;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable endTime must be higher than 0 ! Setting it to default (" + getEndTime() + ")");
        }
    }

    public static List<BBArena> getArenas() {
        return arenas;
    }

    public static List<String> getRestricedBlocks() {
        return restricedBlocks;
    }

    public static String getDefaultFloorMaterial() {
        return defaultFloorMaterial;
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public static void setPrefix(String prefix) {
        if(prefix != null) {
            GameManager.prefix = prefix;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable prefix could not be loaded ! Setting it to default (" + getPrefix() + ")");
        }
    }

    public static int getMaxParticlesPerPlayer() {
        return maxParticlesPerPlayer;
    }

    public static void setMaxParticlesPerPlayer(int maxParticlesPerPlayer) {
        if(maxParticlesPerPlayer < 0) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable particles.max_particles_per_player must be higher or equal 0 ! Setting it to default (" + getMaxParticlesPerPlayer() + ")");
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
        if(amountParticleToSpawn > 0) {
            GameManager.amountParticleToSpawn = amountParticleToSpawn;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable particles.amount_to_spawn must be higher than 0 ! Setting it to default (" + getAmountParticleToSpawn() + ")");
        }
    }

    public static double getParticleRefreshTime() {
        return particleRefreshTime;
    }

    public static void setParticleRefreshTime(double particleRefreshTime) {
        if(particleRefreshTime > 0) {
            GameManager.particleRefreshTime = particleRefreshTime;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable particles.refresh_time must be higher than 0 ! Setting it to default (" + getAmountParticleToSpawn() + ")");
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
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable start message could not be loaded !");
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
        if(fireworkWaves >= 0) {
            GameManager.fireworkWaves = fireworkWaves;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable firework_waves must be higher or equal 0 ! Setting it to default (" + getFireworkWaves() + ")");
        }
    }

    public static int getFireworkAmount() {
        return fireworkAmount;
    }

    public static void setFireworkAmount(int fireworkAmount) {
        if(fireworkAmount >= 0) {
            GameManager.fireworkAmount = fireworkAmount;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable firework_amount must be higher or equal 0 ! Setting it to default (" + getFireworkAmount() + ")");
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
        if(themesToVote > 0 && themesToVote <= 6) {
            GameManager.themesToVote = themesToVote;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable themesToVote must be higher than 0 and lower or equal 6 ! Setting it to default (" + getThemesToVote() + ")");
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
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable stats.Type is invalid ! Setting it to default (" + getStatsType() + ")");
        }
    }

    public static String getEndCommand() {
        return endCommand;
    }

    public static void setEndCommand(String endCommand) {
        if(endCommand != null) {
            GameManager.endCommand = endCommand;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable endCommand in config.yml is not set ! Setting it to default (" + getEndCommand() + ")");
        }
    }

    public static boolean isEndCommandValid() {
        if((getEndCommand() != null) && (!getEndCommand().equalsIgnoreCase("none"))) {
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
        if(pointsApiRewards) {
            if(BuildBattle.getInstance().getServer().getPluginManager().getPlugin("PointsAPI") == null) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cYou enabled PointsAPI rewards, but PointsAPI plugin cannot be found ! Disabling PointsAPI rewards...");
                GameManager.pointsApiRewards = false;
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §ePointsAPI §arewards enabled!");
            }
        }
    }

    public static boolean isVaultRewards() {
        return vaultRewards;
    }

    public static void setVaultRewards(boolean vaultRewards) {
        GameManager.vaultRewards = vaultRewards;
        if(vaultRewards) {
            if(BuildBattle.getInstance().setupEconomy() == false) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cYou enabled Vault rewards, but Vault plugin cannot be found ! Disabling Vault rewards...");
                GameManager.vaultRewards = false;
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §eVault §arewards enabled!");
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
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable gui.theme_voting.themes.lore in messages.yml could not be loaded !");
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
        if(weatherLore != null) {
            GameManager.weatherLore = weatherLore;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable gui.options.items.change_weather_item.lore in messages.yml could not be loaded !");
        }
    }

    public static List<String> getAllowedCommands() {
        return allowedCommands;
    }

    public static void setAllowedCommands(List<String> allowedCommands) {
        if(allowedCommands != null) {
            GameManager.allowedCommands = allowedCommands;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable allowed_commands in config.yml could not be loaded !");
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
        if(partyMaxPlayers > 0) {
            GameManager.partyMaxPlayers = partyMaxPlayers;
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cVariable party.maxPlayers must be higher than 0 ! Setting it to default (" + getPartyMaxPlayers() + ")");
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


    public void loadDefaultFloorMaterial() {
        setDefaultFloorMaterial(BuildBattle.getFileManager().getConfig("config.yml").get().getString("arena.default_floor"));
    }
    public void loadBBPlots(BBArena a) {
        try {
            for (String plot : BuildBattle.getFileManager().getConfig("arenas.yml").get().getConfigurationSection(a.getName() + ".plots").getKeys(false)) {
                Location minPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(a.getName() + ".plots." + plot + ".min"));
                Location maxPoint = LocationUtil.getLocationFromString(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(a.getName() + ".plots." + plot + ".max"));
                BBPlot bbPlot = new BBPlot(a,minPoint,maxPoint);
                bbPlot.addIntoArenaPlots();
                bbPlot.restoreBBPlot();
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §aPlot §e" + plot + " §afor arena §e" + a.getName() + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cLooks like arena §e" + a.getName() + " §c have no plots ! Please set them.");
        }
    }

    public void loadArenas() {
        try {
            arenas = new ArrayList<>();
            for (String arena : BuildBattle.getFileManager().getConfig("arenas.yml").get().getKeys(false)) {
                String name = arena;
                int minPlayers = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(arena + ".min_players");
                if(!BuildBattle.getFileManager().getConfig("arenas.yml").get().isSet(arena + ".mode")) {
                    BuildBattle.getFileManager().getConfig("arenas.yml").get().set(arena + ".mode", BBGameMode.SOLO.name());
                    BuildBattle.getFileManager().getConfig("arenas.yml").save();
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set mode ! Automatically set to SOLO");
                }
                BBGameMode gameMode = BBGameMode.valueOf(BuildBattle.getFileManager().getConfig("arenas.yml").get().getString(arena + ".mode"));
                if(!BuildBattle.getFileManager().getConfig("arenas.yml").get().isSet(arena + ".teamSize")) {
                    BuildBattle.getFileManager().getConfig("arenas.yml").get().set(arena + ".teamSize", gameMode.getDefaultTeamSize());
                    BuildBattle.getFileManager().getConfig("arenas.yml").save();
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set teamSize ! Automatically set to " + gameMode.getDefaultTeamSize());
                }
                int teamSize = BuildBattle.getFileManager().getConfig("arenas.yml").get().getInt(arena + ".teamSize");
                Location lobbyLoc = LocationUtil.getLocationFromConfig("arenas.yml", arena + ".lobbyLocation");
                if(lobbyLoc == null) {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " §4[Warning] §cArena §e" + arena + " §chave not set lobby location !");
                }
                BBArena bbArena = new BBArena(name, minPlayers, gameMode, teamSize, lobbyLoc, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                loadBBPlots(bbArena);
                loadBBSigns(bbArena);
                bbArena.setupTeams();
                bbArena.setupTeamInventory();
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §aArena §e" + arena + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while trying loading arenas !");
            e.printStackTrace();
        }

    }

    private void loadBBSigns(BBArena a) {
        try {
            if (BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(a.getName()) != null) {
                for (String sign : BuildBattle.getFileManager().getConfig("signs.yml").get().getConfigurationSection(a.getName()).getKeys(false)) {
                    Location signLoc = LocationUtil.getLocationFromString(sign);
                    //BBSign constructor already adds this sign into BBArena signs and update it.
                    BBSign bbSign = new BBSign(a, signLoc);
                }
            }
        } catch(Exception e){
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while trying loading signs for arena §e" + a.getName() + "§c!");
            e.printStackTrace();
        }
    }

    public void loadThemes() {
        try {
            for (String s : BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.themes")) {
                themes.add(s);
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §aTheme §e" + s + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while trying loading themes from config !");
            e.printStackTrace();
        }
    }

    public void loadFallbackServers() {
        try {
            List<String> allServers = BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("bungeecord.fallback_servers");
            for(String server : allServers) {
                fallbackServers.add(server);
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §aFallback server §e" + server + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while trying loading fallback servers from config!");
            e.printStackTrace();
        }
    }

    public void loadRestrictedBlocks() {
        try {
            for (String s : BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.restriced_blocks")) {
                getRestricedBlocks().add(s);
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " §aRestricted block with ID §e" + s + " §aloaded !");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while trying loading restriced blocks from config !");
            e.printStackTrace();
        }
    }

    public String getRandomTheme() {
        Random ran = new Random();
        int index = ran.nextInt(themes.size());
        return themes.get(index);
    }

    public String getRandomFallbackServer() {
        Random ran = new Random();
        int index = ran.nextInt(fallbackServers.size());
        return fallbackServers.get(index);
    }

    public void loadArenaPreferences() {
        try {
            setLobbyTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.lobbyTime"));
            setGameTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.gameTime"));
            setVotingTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.votingTime"));
            setThemeVotingTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themeVotingTime"));
            setEndTime(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.endTime"));
            setAutomaticGrow(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.automatic_grow"));
            setThemesToVote(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.voting_for_themes.themesToVote"));
            setVotingForThemes(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.voting_for_themes.enabled"));
            setPrefix(BuildBattle.getFileManager().getConfig("config.yml").get().getString("prefix"));
            setEndCommand(BuildBattle.getFileManager().getConfig("config.yml").get().getString("arena.end_command"));
            setRestrictPlayerMovement(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.restrict_player_movement"));
            setMaxParticlesPerPlayer(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.particles.max_particles_per_player"));
            setParticleOffset(BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.offset"));
            setFireworkAmount(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.amount_per_corner"));
            setFireworkWaves(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.win_fireworks.firework_waves"));
            setAmountParticleToSpawn(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("arena.particles.amount_to_spawn"));
            setPartyMaxPlayers(BuildBattle.getFileManager().getConfig("config.yml").get().getInt("parties.max_players"));
            setPartiesEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("parties.enabled"));
            setParticleRefreshTime(BuildBattle.getFileManager().getConfig("config.yml").get().getDouble("arena.particles.refresh_time"));
            setStartMessage(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("messages.start_message"));
            setAllowedCommands(BuildBattle.getFileManager().getConfig("config.yml").get().getStringList("arena.allowed_commands"));
            setRemovePlayersAfterGame(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.remove_players_after_game"));
            setEndMessage(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("messages.end_message"));
            setThemeVotingLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.theme_voting.themes.lore"));
            setWeatherLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.change_weather_item.lore"));
            setPointsApiRewards(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.PointsAPI.enabled"));
            setVaultRewards(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("rewards.Vault.enabled"));
            setAsyncSavePlayerData(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("stats.async_save_player_data"));
            setStatsType(StatsType.valueOf(BuildBattle.getFileManager().getConfig("config.yml").get().getString("stats.type")));
            setReportsEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.enable_reports"));
            setShowVoteInSubtitle(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_vote_in_subtitle"));
            setFloorChangeNPCtype(EntityType.valueOf(BuildBattle.getFileManager().getConfig("config.yml").get().getString("change_floor_npc.type")));
            setScoreboardEnabled(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("arena.show_scoreboard"));
            setChangeMOTD(BuildBattle.getFileManager().getConfig("config.yml").get().getBoolean("bungeecord.change_motd"));
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " §cAn exception occurred while loading arena preferences ! Check your config.yml");
            e.printStackTrace();
        }
    }
}
