package me.drawe.buildbattle.objects.bbobjects.scoreboards;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;

public class BBSoloLobbyScoreboard extends BBScoreboard {

    public BBSoloLobbyScoreboard() {
        super(BBGameMode.SOLO, BBArenaState.LOBBY, BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getString("scoreboard.solo.lobby.title"), BuildBattle.getInstance().getFileManager().getConfig("translates.yml").get().getStringList("scoreboard.solo.lobby.lines"));
    }
}
