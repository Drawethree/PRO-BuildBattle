package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamThemeVotingScoreboard extends BBScoreboard {

    public BBTeamThemeVotingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.THEME_VOTING, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.team.theme-voting.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.team.theme-voting.lines"));
    }
}
