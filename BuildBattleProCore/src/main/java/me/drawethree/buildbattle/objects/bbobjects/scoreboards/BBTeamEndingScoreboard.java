package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamEndingScoreboard extends BBScoreboard {

    public BBTeamEndingScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.ENDING, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.team.ending.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.team.ending.lines"));
    }
}
