package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;


public class BBTeamVotingScoreboard extends BBScoreboard {

    public BBTeamVotingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.VOTING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.voting.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.voting.lines"));

    }
}
