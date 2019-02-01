package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamThemeVotingScoreboard extends BBScoreboard {

    public BBTeamThemeVotingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.THEME_VOTING, BuildBattle.getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.theme-voting.title"), BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.theme-voting.lines"));
    }
}
