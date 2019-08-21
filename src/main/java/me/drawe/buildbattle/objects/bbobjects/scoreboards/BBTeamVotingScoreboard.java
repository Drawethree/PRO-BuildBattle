package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;


public class BBTeamVotingScoreboard extends BBScoreboard {

    public BBTeamVotingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.VOTING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.voting.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.voting.lines"));

    }
}
