package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {
    //CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int, Wins int, MostPoints int)
    private static MySQLManager ourInstance = new MySQLManager();

    public static MySQLManager getInstance() {
        return ourInstance;
    }

    private MySQLManager() {
    }

    public boolean isPlayerInTable(Player p) {
        try {
            return MySQL.getResult("SELECT UUID FROM BuildBattlePro_PlayerData WHERE UUID='" + p.getUniqueId().toString() + "'").next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasReportedPlayer(Player reporter, Player player) {
        try {
            return MySQL.getResult("SELECT * FROM BuildBattlePro_Reports WHERE Player='" + player.getName() + "' AND ReportedBy='" + reporter.getName() + "'").next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reportBuild(BBPlot plot, Player reporter) {
        try {
            for(Player player : plot.getTeam().getPlayers()) {
                    if (player.equals(reporter)) {
                        reporter.sendMessage(Message.CANNOT_REPORT_YOURSELF.getChatMessage());
                        return;
                    }
                    if (!hasReportedPlayer(reporter, player)) {
                        PreparedStatement statement = MySQL.getConnection().prepareStatement("INSERT INTO BuildBattlePro_Reports(Player,ReportedBy,Date) VALUES(?,?,?)");
                        statement.setString(1, player.getName());
                        statement.setString(2, reporter.getName());
                        statement.setString(3, TimeUtil.getCurrentDateTime());
                        statement.execute();
                        statement.close();
                        reporter.sendMessage(Message.REPORT_SUCCESS.getChatMessage().replaceAll("%player%", player.getName()));
                    } else {
                        reporter.sendMessage(Message.ALREADY_REPOTED.getChatMessage());
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reporter.sendMessage(Message.REPORT_FAILED.getChatMessage());
        }
    }

    public void loadPlayerFromTable(Player p) {
        ResultSet set = MySQL.getResult("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID='" + p.getUniqueId().toString() + "'");
        try {
            String uuid = set.getString("UUID");
            int played = set.getInt("Played");
            int wins = set.getInt("Wins");
            int mostPoints = set.getInt("MostPoints");
            int blocksPlaced = set.getInt("BlocksPlaced");
            int particlesPlaced = set.getInt("ParticlesPlaced");
            BBPlayerStats stats = new BBPlayerStats(uuid,wins,played,mostPoints,blocksPlaced, particlesPlaced);
            PlayerManager.getPlayerStats().add(stats);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUUIDInTable(String uuid) {
        try {
            ResultSet set = MySQL.getResult("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID='" + uuid + "'");
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayerToTable(BBPlayerStats ps) {
        if(!isUUIDInTable(ps.getUuid())) {
            MySQL.insert("INSERT INTO BuildBattlePro_PlayerData(UUID,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced) VALUES('" + ps.getUuid().toString() + "','" + ps.getPlayed() + "','" + ps.getWins() + "','" + ps.getMostPoints() + "','" + ps.getBlocksPlaced() + "','" + ps.getParticlesPlaced() + "')");
        } else {
            MySQL.update("UPDATE BuildBattlePro_PlayerData SET Played='" + ps.getPlayed() + "', Wins='" + ps.getWins() + "', MostPoints='" + ps.getMostPoints() + "', BlocksPlaced='" + ps.getBlocksPlaced() + "', ParticlesPlaced='" + ps.getParticlesPlaced() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
        }
    }
    public void saveAllPlayerStats() {
        for(BBPlayerStats ps : PlayerManager.getInstance().getPlayerStats()) {
            if(isUUIDInTable(ps.getUuid())) {
                MySQL.update("UPDATE BuildBattlePro_PlayerData SET Played='" + ps.getPlayed() + "', Wins='" + ps.getWins() + "', MostPoints='" + ps.getMostPoints() + "', BlocksPlaced='" + ps.getBlocksPlaced() + "', ParticlesPlaced='" + ps.getParticlesPlaced() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
            } else {
                MySQL.insert("INSERT INTO BuildBattlePro_PlayerData(UUID,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced) VALUES('" + ps.getUuid().toString() + "','" + ps.getPlayed() + "','" + ps.getWins() + "','" + ps.getMostPoints() + "','" + ps.getBlocksPlaced() + "','" + ps.getParticlesPlaced() + "')");
            }
        }
        BuildBattle.info("§aBuildBattle player stats saved!");
    }

    public void loadAllPlayerStats() {
        ResultSet set = MySQL.getResult("SELECT * FROM BuildBattlePro_PlayerData");
        try {
            while(set.next()) {
                String uuid = set.getString("UUID");
                int played = set.getInt("Played");
                int wins = set.getInt("Wins");
                int mostPoints = set.getInt("MostPoints");
                int blocksPlaced = set.getInt("BlocksPlaced");
                int particlesPlaced = set.getInt("ParticlesPlaced");
                BBPlayerStats stats = new BBPlayerStats(uuid, wins, played, mostPoints, blocksPlaced, particlesPlaced);
                PlayerManager.getPlayerStats().add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BuildBattle.info("§aBuildBattle player stats loaded!");
    }

    public void savePlayerWins(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET Wins='" + ps.getWins() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }
    public void savePlayerPlayed(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET Played='" + ps.getPlayed() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }
    public void savePlayerParticlesPlaced(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET ParticlesPlaced='" + ps.getParticlesPlaced() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }
    public void savePlayerBlocksPlaced(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET BlocksPlaced='" + ps.getBlocksPlaced() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }
    public void savePlayerMostPoints(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET MostPoints='" + ps.getMostPoints() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }
}
