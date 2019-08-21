package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBTeamLobbyScoreboard extends BBScoreboard {

    public BBTeamLobbyScoreboard() {
        super(BBGameMode.TEAM, BBArenaState.LOBBY, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.team.lobby.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.team.lobby.lines"));

    }
}
