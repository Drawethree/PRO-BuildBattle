package me.drawe.buildbattle.objects.bbobjects.arenanew;

import me.drawe.buildbattle.objects.Message;

public enum BBGameMode {

    SOLO(2, Message.GAMEMODE_SOLO.getMessage()),
    TEAM(4, Message.GAMEMODE_TEAMS.getMessage());

    private int minPlayers;
    private String name;

    BBGameMode(int minPlayers, String message) {
        this.minPlayers = minPlayers;
        this.name = message;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public String getName() {
        return name;
    }
}
