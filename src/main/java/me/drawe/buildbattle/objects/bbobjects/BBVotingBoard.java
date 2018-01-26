package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;
import org.bukkit.entity.Player;

public class BBVotingBoard extends BBBoard {

    private Player owner;

    public BBVotingBoard(BBArena arena, Player p) {
        super(arena,p);
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public void updateVotingBoard() {
        add("&a", 11);
        add(Message.SCOREBOARD_THEME.getMessage(), 10);
        add("&a" + getArena().getTheme(), 9);
        add("&b", 8);
        add(Message.SCOREBOARD_BUILDER.getMessage(), 7);
        add("&a" + getArena().getCurrentVotingPlot().getTeam().getPlayersInCommaSeparatedString(), 6);
        add("&c", 5);
        add(Message.SCOREBOARD_YOUR_VOTE.getMessage(), 4);
        add(getArena().getCurrentVotingPlot().getPlayerVoteString(getOwner()), 3);
        add("&d", 2);
        add(Message.SCOREBOARD_SERVER.getMessage(), 1);
        update();
        send(getOwner());
    }
}
