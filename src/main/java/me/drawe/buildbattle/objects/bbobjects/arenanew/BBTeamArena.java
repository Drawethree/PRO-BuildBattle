package me.drawe.buildbattle.objects.bbobjects.arenanew;

import org.bukkit.entity.Player;

public class BBTeamArena extends BBArena {

    public BBTeamArena(String name) {
        super(name);
        this.gamemode = BBGameMode.TEAM;
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
}
