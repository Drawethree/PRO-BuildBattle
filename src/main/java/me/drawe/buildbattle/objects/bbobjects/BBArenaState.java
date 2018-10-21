package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Material;

public enum BBArenaState {

    LOBBY(Message.GAMESTATE_LOBBY.getMessage(), CompMaterial.LIME_TERRACOTTA),
    THEME_VOTING(Message.GAMESTATE_THEME_VOTING.getMessage(), CompMaterial.RED_TERRACOTTA),
    INGAME(Message.GAMESTATE_INGAME.getMessage(), CompMaterial.RED_TERRACOTTA),
    VOTING(Message.GAMESTATE_VOTING.getMessage(), CompMaterial.RED_TERRACOTTA),
    ENDING(Message.GAMESTATE_ENDING.getMessage(), CompMaterial.YELLOW_TERRACOTTA);

    private String prefix;
    private CompMaterial blockMaterial;

    BBArenaState(String s, CompMaterial data) {
        this.prefix = s;
        this.blockMaterial = data;
    }

    public String getPrefix() {
        return prefix;
    }

    public CompMaterial getBlockMaterial() {
        return blockMaterial;
    }
}
