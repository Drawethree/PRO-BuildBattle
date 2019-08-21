package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloEndingScoreboard extends BBScoreboard {

    public BBSoloEndingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.ENDING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.ending.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.ending.lines"));
    }
}
