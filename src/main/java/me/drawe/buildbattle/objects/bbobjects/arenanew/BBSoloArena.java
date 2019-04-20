package me.drawe.buildbattle.objects.bbobjects.arenanew;

import org.bukkit.entity.Player;

public class BBSoloArena extends BBArena {


    public BBSoloArena(String name) {
        super(name);
        this.gamemode = BBGameMode.SOLO;
    }

    @Override
    public void addPlayer(Player p) {

    }

    @Override
    public void removePlayer(Player p) {

    }

    @Override
    public void startLobby() {

    }

    @Override
    public void startGame() {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void loadArena() {
        super.loadArena();

    }

    @Override
    public void saveArena() {
        super.saveArena();

    }
}
