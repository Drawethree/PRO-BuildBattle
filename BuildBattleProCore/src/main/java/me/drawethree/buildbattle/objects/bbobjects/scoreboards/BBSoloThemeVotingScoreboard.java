package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloThemeVotingScoreboard extends BBScoreboard {

    public BBSoloThemeVotingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.THEME_VOTING, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.solo.theme-voting.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.solo.theme-voting.lines"));

    }
}
