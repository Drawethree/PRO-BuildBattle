package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import me.drawe.buildbattle.hooks.BBHook;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import me.drawe.buildbattle.utils.StringUtils;
import me.drawe.buildbattle.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BBScoreboard {

    private static Map<String, OfflinePlayer> cache = new HashMap<>();
    private BBGameMode gamemode;
    private BBArenaState gameState;
    private String title;
    private List<String> lines;
    private Scoreboard scoreboard;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;
    private int currentLine;

    protected BBScoreboard(BBGameMode gamemode, BBArenaState arenaState, String title, List<String> lines) {
        this.gamemode = gamemode;
        this.gameState = arenaState;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.lines = StringUtils.colorize(lines);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
    }

    private void add(String text) {
        this.currentLine -= 1;

        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(currentLine, text, false) || !scores.containsValue(currentLine)) {
            updated.add(text);
        }

        scores.put(text, currentLine);
    }

    public void updateScoreboard(Player p, BBArena arena, int timeLeft, int baseTime) {
        this.currentLine = 16;
        sendBoard(p, arena, timeLeft, baseTime);
        update();
        send(p);
    }

    public static BBScoreboard getActualScoreboard(BBArena arena) {
        switch (arena.getGameType()) {
            case SOLO:
                switch (arena.getBBArenaState()) {
                    case LOBBY:
                        return new BBSoloLobbyScoreboard();
                    case INGAME:
                        return new BBSoloIngameScoreboard();
                    case VOTING:
                        return new BBSoloVotingScoreboard();
                    case THEME_VOTING:
                        return new BBSoloThemeVotingScoreboard();
                    case ENDING:
                        return new BBSoloEndingScoreboard();
                }
            case TEAM:
                switch (arena.getBBArenaState()) {
                    case LOBBY:
                        return new BBTeamLobbyScoreboard();
                    case INGAME:
                        return new BBTeamIngameScoreboard();
                    case VOTING:
                        return new BBTeamVotingScoreboard();
                    case THEME_VOTING:
                        return new BBTeamThemeVotingScoreboard();
                    case ENDING:
                        return new BBTeamEndingScoreboard();
                }
        }
        return null;
    }


    public void sendBoard(Player player, BBArena arena, int timeLeft, int baseTime) {
        setTitle(replacePlaceholders(player, arena, title, timeLeft, baseTime));
        for (String line : lines) {
            add(replacePlaceholders(player, arena, line, timeLeft, baseTime));
        }
    }

    private String replacePlaceholders(Player player, BBArena arena, String line, int timeLeft, int baseTime) {
        line = line
                .replace("%timeleft%", Time.formatTimeMMSS(timeLeft, baseTime))
                .replace("%gamemode%", arena.getArenaModeInString())
                .replace("%time%", String.valueOf(timeLeft))
                .replace("%current_players%", String.valueOf(arena.getPlayers().size()))
                .replace("%theme%", arena.getTheme())
                .replace("%current_teams%", String.valueOf(arena.getValidTeams().size()))
                .replace("%player_teammates%", arena.getTeamMatesNames(player))
                .replace("%winners%", arena.getWinner() == null ? "" : arena.getWinner().getTeam().getPlayersInCommaSeparatedString())
                .replace("%builders%", arena.getCurrentVotingPlot() == null ? "" : arena.getCurrentVotingPlot().getTeam().getPlayersInCommaSeparatedString())
                .replace("%player_vote%", arena.getCurrentVotingPlot() == null ? "" : String.valueOf(arena.getCurrentVotingPlot().getPlayerVoteString(player)));

        if (BBHook.getHook("PlaceholderAPI")) {
            line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, line);
        }
        if (BBHook.getHook("MVdWPlaceholderAPI")) {
            line = PlaceholderAPI.replacePlaceholders(player, line);
        }
        return line;
    }


    private boolean remove(Integer score, String text) {
        return remove(score, text, true);
    }

    private boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if (b)
            removed.add(score);

        return true;
    }

    private String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score) &&
                    !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    private Map.Entry<Team, OfflinePlayer> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[pos];
        OfflinePlayer result;

        if (!cache.containsKey(color.toString()))
            cache.put(color.toString(), getOfflinePlayerSkipLookup(color.toString()));

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    private void applyText(Team team, String text, OfflinePlayer result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next();

        team.setPrefix(prefix);

        if (!team.hasPlayer(result))
            team.addPlayer(result);

        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(prefix);
            String suffix = iterator.next();

            if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                prefix = prefix.substring(0, prefix.length() - 1);
                team.setPrefix(prefix);
                prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                suffix = suffix.substring(1);
            }

            if (prefixColor == null)
                prefixColor = "";

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

    public void update() {
        if (updated.isEmpty()) {
            return;
        }

        if (obj == null) {
            obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            obj.setDisplayName(title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        removed.stream().forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = obj.getScore(s);

                if (score == null)
                    continue;

                if (score.getScore() != remove)
                    continue;

                scoreboard.resetScores(s);
            }
        });

        removed.clear();

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Team t = scoreboard.getTeam(ChatColor.values()[text.getValue()].toString());
            Map.Entry<Team, OfflinePlayer> team;

            if (!updated.contains(text.getKey())) {
                continue;
            }

            if (t != null) {
                String color = ChatColor.values()[text.getValue()].toString();

                if (!cache.containsKey(color)) {
                    cache.put(color, getOfflinePlayerSkipLookup(color));
                }

                team = new AbstractMap.SimpleEntry<>(t, cache.get(color));
                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            obj.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    private void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if (obj != null)
            obj.setDisplayName(this.title);
    }

    public void reset() {
        for (Team t : teams) {
            t.getEntries().stream().forEach(s -> scoreboard.resetScores(s));
            t.unregister();
        }
        teams.clear();
        scores.clear();
    }

    private void send(Player... players) {
        for (Player p : players)
            p.setScoreboard(scoreboard);
    }

    private final UUID invalidUserUUID = UUID.nameUUIDFromBytes("InvalidUsername".getBytes(Charsets.UTF_8));
    private Class<?> gameProfileClass;
    private Constructor<?> gameProfileConstructor;
    private Constructor<?> craftOfflinePlayerConstructor;

    @SuppressWarnings("deprecation")
    private OfflinePlayer getOfflinePlayerSkipLookup(String name) {
        try {
            if (gameProfileConstructor == null) {
                try { // 1.7
                    gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                } catch (ClassNotFoundException e) { // 1.8
                    gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                }
                gameProfileConstructor = gameProfileClass.getDeclaredConstructor(UUID.class, String.class);
                gameProfileConstructor.setAccessible(true);
            }
            if (craftOfflinePlayerConstructor == null) {
                Class<?> serverClass = Bukkit.getServer().getClass();
                Class<?> craftOfflinePlayerClass = Class.forName(serverClass.getName()
                        .replace("CraftServer", "CraftOfflinePlayer"));
                craftOfflinePlayerConstructor = craftOfflinePlayerClass.getDeclaredConstructor(
                        serverClass, gameProfileClass
                );
                craftOfflinePlayerConstructor.setAccessible(true);
            }
            Object gameProfile = gameProfileConstructor.newInstance(invalidUserUUID, name);
            Object craftOfflinePlayer = craftOfflinePlayerConstructor.newInstance(Bukkit.getServer(), gameProfile);
            return (OfflinePlayer) craftOfflinePlayer;
        } catch (Throwable t) { // Fallback if fail
            return Bukkit.getOfflinePlayer(name);
        }
    }

    private void send(List<Player> players) {
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }


    public BBGameMode getGamemode() {
        return gamemode;
    }

    public BBArenaState getGameState() {
        return gameState;
    }
}
