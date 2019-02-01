package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamEndingScoreboard extends BBScoreboard {

    public BBTeamEndingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.ENDING, BuildBattle.getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.ending.title"), BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.ending.lines"));
    }
}
