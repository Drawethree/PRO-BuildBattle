package me.drawe.buildbattle.objects.bbobjects;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
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

    public void add(String text, Integer score) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    public void updateScoreboard(int timeLeft) {
        switch (getArena().getBBArenaState()) {
            case LOBBY:
                add(getArena().getArenaModeInString(), 7);
                add("&a", 6);
                add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + getArena().getTotalPlayers(), 5);
                add("&b", 4);
                if (timeLeft == 0) {
                    add(Message.SCOREBOARD_STARTING_IN.getMessage().replaceAll("%time%", Message.SCOREBOARD_WAITING.getMessage()), 3);
                } else {
                    add(Message.SCOREBOARD_STARTING_IN.getMessage().replaceAll("%time%", String.valueOf(timeLeft)), 3);
                }
                add("&c", 2);
                add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                break;
            case THEME_VOTING:
                if(getArena().getGameType() == BBGameMode.SOLO) {
                    add(getArena().getArenaModeInString(), 11);
                    add("&a", 10);
                    add(Message.SCOREBOARD_TIME_LEFT.getMessage(), 9);
                    add(Time.formatTimeMMSS(timeLeft), 8);
                    add("", 7);
                    add(Message.SCOREBOARD_THEME.getMessage(), 6);
                    add("&a" + getArena().getTheme(), 5);
                    add("&b", 4);
                    add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + getArena().getPlayers().size(), 3);
                    add("&c", 2);
                    add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                } else if(getArena().getGameType() == BBGameMode.TEAM){
                    add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                    add("&d", 2);
                    int index = 2;
                    if(getArena().getTeamMates(getPlayer()) != null) {
                        for (Player p : getArena().getTeamMates(getPlayer())) {
                            index += 1;
                            add("&a" + p.getName(), index);
                        }
                    }
                    add(Message.SCOREBOARD_TEAMMATE.getMessage(), index + 1);
                    add("&c", index + 2);
                    add(Message.SCOREBOARD_TEAMS.getMessage() + " §a" + getArena().getValidTeams().size(), index +3);
                    add("&b", index + 4);
                    add("&a" + getArena().getTheme(), index + 5);
                    add(Message.SCOREBOARD_THEME.getMessage(), index + 6);
                    add("", index + 7);
                    add("&a" + Time.formatTimeMMSS(timeLeft), index + 8);
                    add(Message.SCOREBOARD_TIME_LEFT.getMessage(), index + 9);
                    add("&a", index + 10);
                    add(getArena().getArenaModeInString(), index + 11);
                }
            case INGAME:
                if(getArena().getGameType() == BBGameMode.SOLO) {
                    add(getArena().getArenaModeInString(), 11);
                    add("&a", 10);
                    add(Message.SCOREBOARD_TIME_LEFT.getMessage(), 9);
                    add("&a" + Time.formatTimeMMSS(timeLeft), 8);
                    add("", 7);
                    add(Message.SCOREBOARD_THEME.getMessage(), 6);
                    add("&a" + getArena().getTheme(), 5);
                    add("&b", 4);
                    add(Message.SCOREBOARD_PLAYERS.getMessage() + " §a" + getArena().getPlayers().size(), 3);
                    add("&c", 2);
                    add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                } else if(getArena().getGameType() == BBGameMode.TEAM){
                    add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                    add("&d", 2);
                    int index = 2;
                    if(getArena().getTeamMates(getPlayer()) != null) {
                        for (Player p : getArena().getTeamMates(getPlayer())) {
                            index += 1;
                            add("&a" + p.getName(), index);
                        }
                    }
                    add(Message.SCOREBOARD_TEAMMATE.getMessage(), index + 1);
                    add("&c", index + 2);
                    add(Message.SCOREBOARD_TEAMS.getMessage() + " §a" + getArena().getValidTeams().size(), index +3);
                    add("&b", index + 4);
                    add("&a" + getArena().getTheme(), index + 5);
                    add(Message.SCOREBOARD_THEME.getMessage(), index + 6);
                    add("", index + 7);
                    add("&a" + Time.formatTimeMMSS(timeLeft), index + 8);
                    add(Message.SCOREBOARD_TIME_LEFT.getMessage(), index + 9);
                    add("&a", index + 10);
                    add(getArena().getArenaModeInString(), index + 11);
                }
                break;
            case VOTING:
                add(Message.SCOREBOARD_SERVER.getMessage(), 1);
                add("&d", 2);
                add(getArena().getCurrentVotingPlot().getPlayerVoteString(getPlayer()), 3);
                add(Message.SCOREBOARD_YOUR_VOTE.getMessage(), 4);
                add("&c", 5);
                int index = 5;
                if(getArena().getTeamMates(getPlayer()) != null) {
                    for (Player p : getArena().getCurrentVotingPlot().getTeam().getPlayers()) {
                        index += 1;
                        add("&a" + p.getName(), index);
                    }
                }
                if(getArena().getGameType() == BBGameMode.SOLO) {
                    add(Message.SCOREBOARD_BUILDER.getMessage(), index + 1);
                } else if(getArena().getGameType() == BBGameMode.TEAM) {
                    add(Message.SCOREBOARD_BUILDERS.getMessage(), index + 1);
                }
                add("&b", index + 2);
                add("&a" + getArena().getTheme(), index + 3);
                add(Message.SCOREBOARD_THEME.getMessage(), index + 4);
                add("&a", index + 5);
                add(getArena().getArenaModeInString(), index + 6);
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