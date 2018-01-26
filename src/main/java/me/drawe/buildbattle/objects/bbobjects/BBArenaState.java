package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;

public enum BBArenaState {

    LOBBY(Message.GAMESTATE_LOBBY.getMessage(), (byte) 5),
    THEME_VOTING(Message.GAMESTATE_THEME_VOTING.getMessage(), (byte) 14),
    INGAME(Message.GAMESTATE_INGAME.getMessage(), (byte) 14),
    VOTING(Message.GAMESTATE_VOTING.getMessage(), (byte) 14),
    ENDING(Message.GAMESTATE_ENDING.getMessage(), (byte) 4);

    private String prefix;
    private byte dataValue;

    BBArenaState(String s, byte data) {
        this.prefix = s;
        this.dataValue = data;
    }

    public String getPrefix() {
        return prefix;
    }

    public byte getDataValue() {
        return dataValue;
    }
}
