package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.MySQLManager;
import me.drawe.buildbattle.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BBPlayerStats {
    private String uuid;
    private int wins;
    private int played;
    private int mostPoints;
    private int blocksPlaced;
    private int particlesPlaced;
    private int superVotes;


    public BBPlayerStats(String uuid, int wins, int played, int mostPoints, int blocksPlaced, int particlesPlaced, int superVotes) {
        this.uuid = uuid;
        this.wins = wins;
        this.played = played;
        this.mostPoints = mostPoints;
        this.blocksPlaced = blocksPlaced;
        this.particlesPlaced = particlesPlaced;
        this.superVotes = superVotes;
    }

    public String getUuid() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
        if(GameManager.isAsyncSavePlayerData()) {
            MySQLManager.getInstance().savePlayerWins(this);
        }
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(getUuid()));
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
        if(GameManager.isAsyncSavePlayerData()) {
            switch(GameManager.getStatsType()) {
                case MYSQL:
                    MySQLManager.getInstance().savePlayerPlayed(this);
                    break;
                case FLATFILE:
                    PlayerManager.getInstance().savePlayerPlayed(this);
                    break;
            }
        }
    }

    public int getMostPoints() {
        return mostPoints;
    }

    public void setMostPoints(int mostPoints) {
        this.mostPoints = mostPoints;
        if(GameManager.isAsyncSavePlayerData()) {
            switch(GameManager.getStatsType()) {
                case MYSQL:
                    MySQLManager.getInstance().savePlayerMostPoints(this);
                    break;
                case FLATFILE:
                    PlayerManager.getInstance().savePlayerMostPoints(this);
                    break;
            }
        }
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public void setBlocksPlaced(int blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
        if(GameManager.isAsyncSavePlayerData()) {
            switch(GameManager.getStatsType()) {
                case MYSQL:
                    MySQLManager.getInstance().savePlayerBlocksPlaced(this);
                    break;
                case FLATFILE:
                    PlayerManager.getInstance().savePlayerBlocksPlaced(this);
                    break;
            }
        }
    }

    public int getParticlesPlaced() {
        return particlesPlaced;
    }

    public void setParticlesPlaced(int particlesPlaced) {
        this.particlesPlaced = particlesPlaced;
        if(GameManager.isAsyncSavePlayerData()) {
            switch(GameManager.getStatsType()) {
                case MYSQL:
                    MySQLManager.getInstance().savePlayerParticlesPlaced(this);
                    break;
                case FLATFILE:
                    PlayerManager.getInstance().savePlayerParticlesPlaced(this);
                    break;
            }
        }
    }

    public int getSuperVotes() {
        return superVotes;
    }

    public void setSuperVotes(int superVotes) {
        this.superVotes = superVotes;
        if(GameManager.isAsyncSavePlayerData()) {
            switch(GameManager.getStatsType()) {
                case MYSQL:
                    MySQLManager.getInstance().savePlayerSuperVotes(this);
                    break;
                case FLATFILE:
                    PlayerManager.getInstance().savePlayerSuperVotes(this);
                    break;
            }
        }
    }
}
