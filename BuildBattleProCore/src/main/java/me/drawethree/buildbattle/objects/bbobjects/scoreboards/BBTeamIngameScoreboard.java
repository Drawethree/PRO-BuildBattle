package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamIngameScoreboard extends BBScoreboard {

    public BBTeamIngameScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.INGAME, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.team.ingame.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.team.ingame.lines"));

    }
}
