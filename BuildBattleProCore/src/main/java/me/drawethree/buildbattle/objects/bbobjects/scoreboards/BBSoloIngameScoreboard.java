package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloIngameScoreboard extends BBScoreboard {

    public BBSoloIngameScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.INGAME, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.solo.ingame.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.solo.ingame.lines"));
    }
}
