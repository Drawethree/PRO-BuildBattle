package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloEndingScoreboard extends BBScoreboard {

    public BBSoloEndingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.ENDING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.ending.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.ending.lines"));
    }
}
