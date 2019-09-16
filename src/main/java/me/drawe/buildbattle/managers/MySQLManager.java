package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.mysql.MySQLDatabase;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawe.buildbattle.objects.bbobjects.BBStat;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.utils.Time;
import me.drawe.buildbattle.utils.compatbridge.MinecraftVersion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MySQLManager {

    private MySQLDatabase database;

    public MySQLManager(MySQLDatabase database) {
        this.database = database;
    }

    //CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int, Wins int, MostPoints int)
    //CREATE TABLE IF NOT EXISTS BuildBattlePro_ReportedBuilds(ID varchar(100) NOT NULL, ReportedPlayers text NOT NULL, ReportedBy varchar(36) NOT NULL, Date date NOT NULL, SchematicName text NOT NULL, Status varchar(30) NOT NULL)

    public boolean hasReportedPlayer(Player reporter, Player player) {
        try {
            return database.query(database.getStatement("SELECT * FROM BuildBattlePro_ReportedBuilds WHERE Player='" + player.getName() + "' AND ReportedBy='" + reporter.getName() + "'")).next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reportBuild(BBPlot plot, Player reporter) {
        try {
            for (Player player : plot.getTeam().getPlayers()) {
                if (player.equals(reporter)) {
                    reporter.sendMessage(Message.CANNOT_REPORT_YOURSELF.getChatMessage());
                    return;
                }
                if (!hasReportedPlayer(reporter, player)) {
                    PreparedStatement statement = database.getStatement("INSERT INTO BuildBattlePro_ReportedBuilds(Player,ReportedBy,Date) VALUES(?,?,?)");
                    statement.setString(1, player.getName());
                    statement.setString(2, reporter.getName());
                    statement.setString(3, Time.getCurrentDateTime());
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

    public boolean isUUIDInTable(UUID uuid) {
        try {
            ResultSet set = database.query(database.getStatement("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID='" + uuid.toString() + "'"));
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayerToTable(BBPlayerStats ps) {
        if (!isUUIDInTable(ps.getUuid())) {
            database.update("INSERT INTO BuildBattlePro_PlayerData(UUID,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced,SuperVotes) VALUES('" + ps.getUuid().toString() + "','" + ps.getStat(BBStat.PLAYED) + "','" + ps.getStat(BBStat.WINS) + "','" + ps.getStat(BBStat.MOST_POINTS) + "','" + ps.getStat(BBStat.BLOCKS_PLACED) + "','" + ps.getStat(BBStat.PARTICLES_PLACED) + "','" + ps.getStat(BBStat.SUPER_VOTES) + "')");
        } else {
            this.savePlayerStats(ps);
        }
    }

    public synchronized void loadPlayer(Player p) {
        new BukkitRunnable() {

            @Override
            public void run() {
                PreparedStatement statement = database.getStatement("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID=?");
                try {
                    statement.setString(1, p.getUniqueId().toString());
                    ResultSet set = database.query(statement);
                    if (set.next()) {
                        BBPlayerStats stats = new BBPlayerStats(UUID.fromString(set.getString("UUID")));
                        for (BBStat stat : BBStat.values()) {
                            stats.setStat(stat, set.getObject(stat.getSQLKey()));
                        }
                        database.getParent().getPlayerManager().getPlayerStats().put(stats.getUuid(), stats);
                        BuildBattle.getInstance().debug("Data for player " + p.getName() + " loaded from MySQLDatabase!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(database.getParent());
    }

    public void loadAllReports() {
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_13)) {
            return;
        }
        database.getParent().getReportManager().getBuildReports().clear();

        ResultSet set = database.query(database.getStatement("SELECT * FROM BuildBattlePro_ReportedBuilds"));
        try {
            while (set.next()) {
                String id = set.getString("ID");
                List<UUID> reportedPlayers = new ArrayList<>();
                String reportedPlayersString = set.getString("ReportedPlayers");
                Arrays.asList(reportedPlayersString.split(",")).forEach(uuid -> reportedPlayers.add(UUID.fromString(uuid)));
                UUID reportedBy = UUID.fromString(set.getString("ReportedBy"));
                Date date = set.getTimestamp("Date");
                File schematic = new File(new File(database.getParent().getDataFolder(), database.getParent().getReportManager().getReportsDirectoryName()), set.getString("SchematicName"));
                BBReportStatus reportStatus = BBReportStatus.valueOf(set.getString("Status").toUpperCase());
                BBBuildReport report = new BBBuildReport(id, reportedPlayers, reportedBy, schematic, date, reportStatus);
                database.getParent().getReportManager().getBuildReports().add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.getParent().info("§aBuildBattle build reports loaded !");
    }

    public boolean saveReport(BBBuildReport report) {
        if (database.getParent().isEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement statement = database.getStatement("INSERT INTO BuildBattlePro_ReportedBuilds(ID,ReportedPlayers,ReportedBy,Date,SchematicName,Status) VALUES(?,?,?,?,?,?)");
                    try {
                        statement.setString(1, report.getReportID());
                        statement.setString(2, report.getReportedPlayersInCommaSeparatedString());
                        statement.setString(3, report.getReportedBy().toString());
                        statement.setTimestamp(4, new Timestamp(report.getReportDate().getTime()));
                        statement.setString(5, report.getSchematicFile().getName());
                        statement.setString(6, report.getReportStatus().name().toUpperCase());
                        statement.execute();
                    } catch (SQLException e) {
                        database.getParent().severe("An error occurred while saving build report " + report.getReportID() + " into MySQLDatabase database !");
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(database.getParent());
        } else {
            PreparedStatement statement = database.getStatement("INSERT INTO BuildBattlePro_ReportedBuilds(ID,ReportedPlayers,ReportedBy,Date,SchematicName,Status) VALUES(?,?,?,?,?,?)");
            try {
                statement.setString(1, report.getReportID());
                statement.setString(2, report.getReportedPlayersInCommaSeparatedString());
                statement.setString(3, report.getReportedBy().toString());
                statement.setTimestamp(4, new Timestamp(report.getReportDate().getTime()));
                statement.setString(5, report.getSchematicFile().getName());
                statement.setString(6, report.getReportStatus().name().toUpperCase());
                statement.execute();
            } catch (SQLException e) {
                database.getParent().severe("An error occurred while saving build report " + report.getReportID() + " into MySQLDatabase database !");
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean deleteReport(BBBuildReport report) {
        database.update("DELETE FROM BuildBattlePro_ReportedBuilds WHERE ID='" + report.getReportID() + "'");
        return true;
    }

    public void savePlayerStats(BBPlayerStats playerStats) {

        if (!isUUIDInTable(playerStats.getUuid())) {
            this.addPlayerToTable(playerStats);
            return;
        }

        for (BBStat stat : BBStat.values()) {
            this.savePlayerStat(stat, playerStats);
        }
    }

    public synchronized void loadAllPlayerStats(HashMap<UUID, BBPlayerStats> map, CountDownLatch latch) {
        new BukkitRunnable() {

            @Override
            public void run() {
                ResultSet set = database.query(database.getStatement("SELECT * FROM BuildBattlePro_PlayerData"));
                try {
                    while (set.next()) {
                        BBPlayerStats stats = new BBPlayerStats(UUID.fromString(set.getString("UUID")));

                        for (BBStat stat : BBStat.values()) {
                            stats.setStat(stat, set.getObject(stat.getSQLKey()));
                        }
                        map.put(stats.getUuid(), stats);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        }.runTaskAsynchronously(database.getParent());
    }

    public void savePlayerStat(BBStat stat, BBPlayerStats bbPlayerStats) {
        database.update("UPDATE BuildBattlePro_PlayerData SET " + stat.getSQLKey() + "='" + bbPlayerStats.getStat(stat) + "' WHERE UUID='" + bbPlayerStats.getUuid().toString() + "'");
    }

    public Double getPlayerStat(BBStat stat, Player player) {
        ResultSet set = database.query(database.getStatement("SELECT " + stat.getSQLKey() + " FROM BuildBattlePro_PlayerData WHERE UUID='" + player.getUniqueId() + "'"));
        try {
            while (set.next()) {
                return set.getDouble(stat.getSQLKey());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
