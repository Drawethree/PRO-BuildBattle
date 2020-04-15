package me.drawethree.buildbattle.objects.bbobjects;

import me.drawethree.buildbattle.objects.Message;

public enum BBGameMode {

    SOLO(1, Message.GAMEMODE_SOLO.getMessage()),
    TEAM(2, Message.GAMEMODE_TEAMS.getMessage());

    int defaultTeamSize;
    String name;

    BBGameMode(int i, String message) {
        this.defaultTeamSize = i;
        this.name = message;
    }

    public int getDefaultTeamSize() {
        return defaultTeamSize;
    }

    public String getName() {
        return name;
    }
}
