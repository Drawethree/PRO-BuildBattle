package me.drawe.buildbattle.objects.bbobjects.arena;

import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;

public enum BBArenaState {

    LOBBY(Message.GAMESTATE_LOBBY.getMessage(), XMaterial.LIME_TERRACOTTA),
    THEME_VOTING(Message.GAMESTATE_THEME_VOTING.getMessage(), XMaterial.RED_TERRACOTTA),
    INGAME(Message.GAMESTATE_INGAME.getMessage(), XMaterial.RED_TERRACOTTA),
    VOTING(Message.GAMESTATE_VOTING.getMessage(), XMaterial.RED_TERRACOTTA),
    ENDING(Message.GAMESTATE_ENDING.getMessage(), XMaterial.YELLOW_TERRACOTTA);

    private String prefix;
    private XMaterial blockMaterial;

    BBArenaState(String s, XMaterial data) {
        this.prefix = s;
        this.blockMaterial = data;
    }

    public String getPrefix() {
        return prefix;
    }

    public XMaterial getBlockMaterial() {
        return blockMaterial;
    }
}
