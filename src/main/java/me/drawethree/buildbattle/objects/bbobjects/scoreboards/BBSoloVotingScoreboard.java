package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloVotingScoreboard extends BBScoreboard {

    public BBSoloVotingScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.VOTING, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.voting.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.voting.lines"));
    }
}
