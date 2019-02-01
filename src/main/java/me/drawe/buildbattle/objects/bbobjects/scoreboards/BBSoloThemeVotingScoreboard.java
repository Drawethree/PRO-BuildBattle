package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloThemeVotingScoreboard extends BBScoreboard {

    public BBSoloThemeVotingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.THEME_VOTING, BuildBattle.getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.theme-voting.title"), BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.theme-voting.lines"));

    }
}
