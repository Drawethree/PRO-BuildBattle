package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.objects.Message;
import org.bukkit.Material;

public enum BBArenaState {

    LOBBY(Message.GAMESTATE_LOBBY.getMessage(), Material.LIME_TERRACOTTA),
    THEME_VOTING(Message.GAMESTATE_THEME_VOTING.getMessage(), Material.RED_TERRACOTTA),
    INGAME(Message.GAMESTATE_INGAME.getMessage(), Material.RED_TERRACOTTA),
    VOTING(Message.GAMESTATE_VOTING.getMessage(), Material.RED_TERRACOTTA),
    ENDING(Message.GAMESTATE_ENDING.getMessage(), Material.YELLOW_TERRACOTTA);

    private String prefix;
    private Material blockMaterial;

    BBArenaState(String s, Material data) {
        this.prefix = s;
        this.blockMaterial = data;
    }

    public String getPrefix() {
        return prefix;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }
}
