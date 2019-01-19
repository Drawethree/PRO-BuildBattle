package me.drawe.buildbattle.objects.bbobjects;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BBBoard {

    private static Map<String, OfflinePlayer> cache = new HashMap<>();

    private BBArena arena;
    private Player player;
    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;
    private int currentLine;

    public BBBoard(BBArena arena, Player p) {
        this.arena = arena;
        this.player = p;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', Message.SCOREBOARD_TITLE.getMessage());
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(String text) {
        this.currentLine -= 1;

        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(currentLine, text, false) || !scores.containsValue(currentLine)) {
            updated.add(text);
        }

        scores.put(text, currentLine);
    }

    /*public void updateScoreboard(int timeLeft) {
        this.currentLine = 16;

        switch (arena.getBBArenaState()) {

            case LOBBY:
                sendBoard(BBSettings.getLobbyScoreboardTitle(), BBSettings.getLobbyScoreboardLines(), timeLeft, BBSettings.getLobbyTime());
                break;
            case INGAME:
                sendBoard(BBSettings.getIngameScoreboardTitle(), BBSettings.getIngameScoreboardLines(), timeLeft, arena.getGameTime());
                break;
            case VOTING:
                sendBoard(BBSettings.getBuyingScoreboardTitle(), BBSettings.getBuyingScoreboardLines(), timeLeft, BBSettings.getVotingTime());
                break;
            case THEME_VOTING:
                sendBoard(BBSettings.getDeathmatchScoreboardTitle(), BBSettings.getDeathmatchScoreboardLines(), timeLeft, BBSettings.getThemeVotingTime());
                break;
            case ENDING:
                sendBoard(BBSettings.getDeathmatchScoreboardTitle(), BBSettings.getDeathmatchScoreboardLines(), timeLeft, BBSettings.getEndTime());
                break;
        }
        update();
        send(getPlayer());
    }*/


    /*private void sendBoard(String title, List<String> lines, int timeLeft, int baseTime) {
        setTitle(replacePlaceholders(title, timeLeft, baseTime));
        for (String line : lines) {
            if (line.contains("%players_locations%")) {
                for (int i = 0; i < GDSettings.getAmountOfPlayersInScoreboard(); i++) {
                    try {
                        final Player p = arena.getPlayers().get(i);

                        if (p.equals(player)) {
                            add(Message.SCOREBOARD_PLAYER_FORMAT.getMessage().replaceAll("%player%", ChatColor.UNDERLINE + p.getName() + ChatColor.RESET).replaceAll("%location%", String.valueOf(p.getLocation().getBlockY() - arena.getCurrentMap().getEndLine())));
                        } else {
                            add(Message.SCOREBOARD_PLAYER_FORMAT.getMessage().replaceAll("%player%", p.getName()).replaceAll("%location%", String.valueOf(p.getLocation().getBlockY() - arena.getCurrentMap().getEndLine())));
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                continue;
            }
            add(replacePlaceholders(line, timeLeft, baseTime));
        }
    }*/

    /*private String replacePlaceholders(String line, int timeLeft, int baseTime) {
        line = line
                .replaceAll("%timeleft%", Time.formatTimeMMSS(timeLeft, baseTime))
                .replaceAll("%players%", String.valueOf(arena.getPlayers().size()))
                .replaceAll("%player_coins%", String.valueOf(arena.getPlayerCoins().get(player)))
                .replaceAll("%map_name%", arena.getCurrentMapName())
                .replaceAll("%map_current%", String.valueOf(arena.getCurrentPlayedMap()))
                .replaceAll("%maps_played%", String.valueOf(arena.getPlayedMaps()))
                .replaceAll("%player_kills%", String.valueOf(arena.getDeathMatch().getPlayerKills(player)))
                .replaceAll("%remaining_players%", String.valueOf(arena.getDeathMatch().getAlivePlayers().size()));

        if (GetDownPro.isUsePlaceholderAPI()) {
            line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, line);
        }
        if (GetDownPro.isUseMVdWPlaceholderAPI()) {
            line = PlaceholderAPI.replacePlaceholders(player, line);
        }
        return line;
    }*/

    public void updateScoreboard(int timeLeft) {
        this.currentLine = 16;

        switch (arena.getBBArenaState()) {
            case LOBBY:
                add(arena.getArenaModeInString());
                add("&a");
                add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + arena.getTotalPlayers());
                add("&b");
                if (timeLeft == 0) {
                    add(Message.SCOREBOARD_STARTING_IN.getMessage().replaceAll("%time%", Message.SCOREBOARD_WAITING.getMessage()));
                } else {
                    add(Message.SCOREBOARD_STARTING_IN.getMessage().replaceAll("%time%", String.valueOf(timeLeft)));
                }
                add("&c");
                add(Message.SCOREBOARD_SERVER.getMessage());
                break;
            case THEME_VOTING:
                add(arena.getArenaModeInString());
                add("&a");
                add(Message.SCOREBOARD_TIME_LEFT.getMessage());
                add(Time.formatTimeMMSS(timeLeft, (int) BBSettings.getThemeVotingTime()));
                add("");
                add(Message.SCOREBOARD_THEME.getMessage());
                add("&a" + arena.getTheme());
                add("&b");
                if(arena.getGameType() == BBGameMode.SOLO) {
                    add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + arena.getPlayers().size());
                    add("&c");
                } else if(arena.getGameType() == BBGameMode.TEAM) {
                    add(Message.SCOREBOARD_TEAMS.getMessage() + " §a" + arena.getValidTeams().size());
                    add("&c");
                    add(Message.SCOREBOARD_TEAMMATE.getMessage());
                    if (arena.getTeamMates(getPlayer()) != null) {
                        for (Player p : arena.getTeamMates(getPlayer())) {
                            add("&a" + p.getName());
                        }
                    }
                    add("&d");
                }
                add(Message.SCOREBOARD_SERVER.getMessage());
                break;
            case INGAME:
                add(arena.getArenaModeInString());
                add("&a");
                add(Message.SCOREBOARD_TIME_LEFT.getMessage());
                add("&a" + Time.formatTimeMMSS(timeLeft, arena.getGameTime()));
                add("");
                add(Message.SCOREBOARD_THEME.getMessage());
                add("&a" + arena.getTheme());
                add("&b");
                if(arena.getGameType() == BBGameMode.SOLO) {
                    add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + arena.getPlayers().size());
                    add("&c");
                } else if(arena.getGameType() == BBGameMode.TEAM){
                    add(Message.SCOREBOARD_TEAMS.getMessage() + " §a" + arena.getValidTeams().size());
                    add("&c");
                    add(Message.SCOREBOARD_TEAMMATE.getMessage());
                    if(arena.getTeamMates(getPlayer()) != null) {
                        for (Player p : arena.getTeamMates(getPlayer())) {
                            add("&a" + p.getName());
                        }
                    }
                }
                add(Message.SCOREBOARD_SERVER.getMessage());
                break;
            case VOTING:
                add(arena.getArenaModeInString());
                add("&a");
                add(Message.SCOREBOARD_THEME.getMessage());
                add("&a" + arena.getTheme());
                add("&b");
                if(arena.getGameType() == BBGameMode.SOLO) {
                    add(Message.SCOREBOARD_BUILDER.getMessage());
                } else if(arena.getGameType() == BBGameMode.TEAM) {
                    add(Message.SCOREBOARD_BUILDERS.getMessage());
                }
                if(arena.getTeamMates(getPlayer()) != null) {
                    for (Player p : arena.getCurrentVotingPlot().getTeam().getPlayers()) {
                        add("&a" + p.getName());
                    }
                }
                add("&c");
                add(Message.SCOREBOARD_YOUR_VOTE.getMessage());
                add(arena.getCurrentVotingPlot().getPlayerVoteString(getPlayer()));
                add("&d");
                add(Message.SCOREBOARD_SERVER.getMessage());
                break;
        }
        update();
        send(getPlayer());
    }

    public boolean remove(Integer score, String text) {
        return remove(score, text, true);
}

    public boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if(b)
            removed.add(score);

        return true;
    }

    public String get(int score, String n) {
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

        if(!team.hasPlayer(result))
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

            if(!updated.contains(text.getKey())) {
                continue;
            }

            if(t != null) {
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

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if(obj != null)
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
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

    public void send(List<Player> players) {
        for(Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }


    public BBArena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }
}