package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.TimeUtil;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class MySQLManager {
    //CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int, Wins int, MostPoints int)
    //CREATE TABLE IF NOT EXISTS BuildBattlePro_ReportedBuilds(ID varchar(100) NOT NULL, ReportedPlayers text NOT NULL, ReportedBy varchar(36) NOT NULL, Date date NOT NULL, SchematicName text NOT NULL, Status varchar(30) NOT NULL)
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
            return MySQL.getResult("SELECT * FROM BuildBattlePro_ReportedBuilds WHERE Player='" + player.getName() + "' AND ReportedBy='" + reporter.getName() + "'").next();
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
                        PreparedStatement statement = MySQL.getConnection().prepareStatement("INSERT INTO BuildBattlePro_ReportedBuilds(Player,ReportedBy,Date) VALUES(?,?,?)");
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
            int superVotes = set.getInt("SuperVotes");
            BBPlayerStats stats = new BBPlayerStats(uuid,wins,played,mostPoints,blocksPlaced, particlesPlaced, superVotes);
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
            MySQL.insert("INSERT INTO BuildBattlePro_PlayerData(UUID,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced,SuperVotes) VALUES('" + ps.getUuid().toString() + "','" + ps.getPlayed() + "','" + ps.getWins() + "','" + ps.getMostPoints() + "','" + ps.getBlocksPlaced() + "','" + ps.getParticlesPlaced() + "','" + ps.getSuperVotes() + "')");
        } else {
            MySQL.update("UPDATE BuildBattlePro_PlayerData SET Played='" + ps.getPlayed() + "', Wins='" + ps.getWins() + "', MostPoints='" + ps.getMostPoints() + "', BlocksPlaced='" + ps.getBlocksPlaced() + "', ParticlesPlaced='" + ps.getParticlesPlaced() + "', SuperVotes='" + ps.getSuperVotes() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
        }
    }
    public void saveAllPlayerStats() {
        for(BBPlayerStats ps : PlayerManager.getInstance().getPlayerStats()) {
            if(isUUIDInTable(ps.getUuid())) {
                MySQL.update("UPDATE BuildBattlePro_PlayerData SET Played='" + ps.getPlayed() + "', Wins='" + ps.getWins() + "', MostPoints='" + ps.getMostPoints() + "', BlocksPlaced='" + ps.getBlocksPlaced() + "', ParticlesPlaced='" + ps.getParticlesPlaced() + "', SuperVotes='" + ps.getSuperVotes() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
            } else {
                MySQL.insert("INSERT INTO BuildBattlePro_PlayerData(UUID,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced,SuperVotes) VALUES('" + ps.getUuid().toString() + "','" + ps.getPlayed() + "','" + ps.getWins() + "','" + ps.getMostPoints() + "','" + ps.getBlocksPlaced() + "','" + ps.getParticlesPlaced() + "','" + ps.getSuperVotes() + "')");
            }
        }
        BuildBattle.info("§aBuildBattle player stats saved!");
    }

    public void loadAllPlayerStats() {
        PlayerManager.playerStats = new ArrayList<>();
        ResultSet set = MySQL.getResult("SELECT * FROM BuildBattlePro_PlayerData");
        try {
            while(set.next()) {
                String uuid = set.getString("UUID");
                int played = set.getInt("Played");
                int wins = set.getInt("Wins");
                int mostPoints = set.getInt("MostPoints");
                int blocksPlaced = set.getInt("BlocksPlaced");
                int particlesPlaced = set.getInt("ParticlesPlaced");
                int superVotes = set.getInt("SuperVotes");
                BBPlayerStats stats = new BBPlayerStats(uuid, wins, played, mostPoints, blocksPlaced, particlesPlaced, superVotes);
                PlayerManager.getPlayerStats().add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BuildBattle.info("§aBuildBattle player stats loaded!");
    }

    public void loadAllReports() {
        ReportManager.buildReports = new ArrayList<>();
        ResultSet set = MySQL.getResult("SELECT * FROM BuildBattlePro_ReportedBuilds");
        try {
            while (set.next()) {
                String id = set.getString("ID");
                List<UUID> reportedPlayers = new ArrayList<>();
                String reportedPlayersString = set.getString("ReportedPlayers");
                Arrays.asList(reportedPlayersString.split(",")).forEach(uuid-> reportedPlayers.add(UUID.fromString(uuid)));
                UUID reportedBy = UUID.fromString(set.getString("ReportedBy"));
                Date date = set.getTimestamp("Date");
                File schematic = new File(new File(BuildBattle.getInstance().getDataFolder(), ReportManager.reportsDirectoryName), set.getString("SchematicName"));
                BBReportStatus reportStatus = BBReportStatus.valueOf(set.getString("Status").toUpperCase());
                BBBuildReport report = new BBBuildReport(id,reportedPlayers,reportedBy,schematic,date,reportStatus);
                ReportManager.getBuildReports().add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BuildBattle.info("§aBuildBattle build reports loaded !");
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

    public void savePlayerSuperVotes(BBPlayerStats ps) {
        MySQL.update("UPDATE BuildBattlePro_PlayerData SET SuperVotes='" + ps.getSuperVotes() + "' WHERE UUID='" + ps.getUuid().toString() + "'");
    }

    public boolean saveReport(BBBuildReport report) {
        PreparedStatement statement = MySQL.getStatement("INSERT INTO BuildBattlePro_ReportedBuilds(ID,ReportedPlayers,ReportedBy,Date,SchematicName,Status) VALUES(?,?,?,?,?,?)");
        try {
            statement.setString(1, report.getReportID());
            statement.setString(2, report.getReportedPlayersInCommaSeparatedString());
            statement.setString(3, report.getReportedBy().toString());
            statement.setTimestamp(4, new Timestamp(report.getReportDate().getTime()));
            statement.setString(5, report.getSchematicFile().getName());
            statement.setString(6, report.getReportStatus().name().toUpperCase());
            statement.execute();
            return true;
        } catch (SQLException e) {
            BuildBattle.severe("An error occurred while saving build report " + report.getReportID() + " into MySQL database !");
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReport(BBBuildReport report) {
        return MySQL.update("DELETE FROM BuildBattlePro_ReportedBuilds WHERE ID='" + report.getReportID() + "'");
    }
}
