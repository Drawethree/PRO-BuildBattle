package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloVotingScoreboard extends BBScoreboard {

    public BBSoloVotingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.VOTING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.voting.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.voting.lines"));
    }
}
