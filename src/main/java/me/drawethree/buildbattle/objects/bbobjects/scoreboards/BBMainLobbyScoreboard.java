package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BBMainLobbyScoreboard {

    private static Map<String, OfflinePlayer> cache = new HashMap<>();

    private Player player;
    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;

    public BBMainLobbyScoreboard(Player p, BBPlayerStats stats) {
        this.player = p;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', Message.SCOREBOARD_MAIN_LOBBY_TITLE.getMessage());
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
        add("&a", 14);
        add("&b", 12);
        add("&c", 10);
        add("&d", 8);
        add("&e", 6);
        add("&f", 4);
        add("&1", 2);
        add(Message.SCOREBOARD_MAIN_LOBBY_SERVER.getMessage(), 1);
        if(stats != null) {
            add(Message.SCOREBOARD_MAIN_LOBBY_PLAYED.getMessage().replace("%played%", String.valueOf(stats.getStat(BBStat.PLAYED))), 13);
            add(Message.SCOREBOARD_MAIN_LOBBY_WINS.getMessage().replace("%wins%", String.valueOf(stats.getStat(BBStat.WINS))), 11);
            add(Message.SCOREBOARD_MAIN_LOBBY_MOST_POINTS.getMessage().replace("%most_points%", String.valueOf(stats.getStat(BBStat.MOST_POINTS))), 9);
            add(Message.SCOREBOARD_MAIN_LOBBY_BLOCKS_PLACED.getMessage().replace("%blocks%", String.valueOf(stats.getStat(BBStat.BLOCKS_PLACED))), 7);
            add(Message.SCOREBOARD_MAIN_LOBBY_PARTICLES_PLACED.getMessage().replace("%particles%", String.valueOf(stats.getStat(BBStat.PARTICLES_PLACED))), 5);
            add(Message.SCOREBOARD_MAIN_LOBBY_SUPER_VOTES.getMessage().replace("%super_votes%", String.valueOf(stats.getStat(BBStat.SUPER_VOTES))), 3);
        } else {
            add(Message.SCOREBOARD_MAIN_LOBBY_PLAYED.getMessage().replace("%played%", String.valueOf(0)), 13);
            add(Message.SCOREBOARD_MAIN_LOBBY_WINS.getMessage().replace("%wins%", String.valueOf(0)), 11);
            add(Message.SCOREBOARD_MAIN_LOBBY_MOST_POINTS.getMessage().replace("%most_points%", String.valueOf(0)), 9);
            add(Message.SCOREBOARD_MAIN_LOBBY_BLOCKS_PLACED.getMessage().replace("%blocks%", String.valueOf(0)), 7);
            add(Message.SCOREBOARD_MAIN_LOBBY_PARTICLES_PLACED.getMessage().replace("%particles%", String.valueOf(0)), 5);
            add(Message.SCOREBOARD_MAIN_LOBBY_SUPER_VOTES.getMessage().replace("%super_votes%", String.valueOf(0)), 3);
        }
        update();
    }

    public void add(String text, Integer score) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
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

    public void removeBoard() {
        getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public Player getPlayer() {
        return player;
    }
}