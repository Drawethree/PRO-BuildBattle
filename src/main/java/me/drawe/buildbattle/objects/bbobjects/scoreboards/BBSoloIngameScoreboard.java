package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloIngameScoreboard extends BBScoreboard {

    public BBSoloIngameScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.INGAME, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.ingame.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.ingame.lines"));
    }
}
