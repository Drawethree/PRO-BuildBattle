package me.drawethree.buildbattle.objects.bbobjects.scoreboards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamLobbyScoreboard extends BBScoreboard {

    public BBTeamLobbyScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.LOBBY, BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getString("scoreboard.team.lobby.title"), BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("scoreboard.team.lobby.lines"));

    }
}
