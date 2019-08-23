package me.drawe.buildbattle.objects.bbobjects.exceptions;

import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;

public class BBLobbyNotFoundException extends Exception {

    public BBLobbyNotFoundException(BBArena arena) {
        super("No lobby found for arena " + arena.getName() + "!");
    }
}
