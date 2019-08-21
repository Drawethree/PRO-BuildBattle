package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamIngameScoreboard extends BBScoreboard {

    public BBTeamIngameScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.INGAME, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.ingame.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.ingame.lines"));

    }
}
