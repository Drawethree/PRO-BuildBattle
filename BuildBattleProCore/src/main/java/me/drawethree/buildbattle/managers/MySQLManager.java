package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.mysql.MySQLDatabase;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawethree.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawethree.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawethree.buildbattle.objects.bbobjects.BBStat;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.utils.Time;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
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
        try (ResultSet set = database.query("SELECT * FROM BuildBattlePro_ReportedBuilds WHERE Player=? AND ReportedBy=?", player.getName(), reporter.getName())) {
            return set.next();
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
                    database.execute("INSERT INTO BuildBattlePro_ReportedBuilds(Player,ReportedBy,Date) VALUES(?,?,?)", player.getName(), reporter.getName(), Time.getCurrentDateTime());
                    reporter.sendMessage(Message.REPORT_SUCCESS.getChatMessage().replace("%player%", player.getName()));
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
        try (ResultSet set = database.query("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID=?", uuid.toString())) {
            return set.next();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return false;
    }

    public void addPlayerToTable(BBPlayerStats ps) {
        if (!isUUIDInTable(ps.getUuid())) {
            database.execute("INSERT INTO BuildBattlePro_PlayerData(UUID,Username,Played,Wins,MostPoints,BlocksPlaced,ParticlesPlaced,SuperVotes) VALUES(?,?,?,?,?,?,?,?)", ps.getUuid().toString(), ps.getUsername(), ps.getStat(BBStat.PLAYED), ps.getStat(BBStat.WINS), ps.getStat(BBStat.MOST_POINTS), ps.getStat(BBStat.BLOCKS_PLACED), ps.getStat(BBStat.PARTICLES_PLACED), ps.getStat(BBStat.SUPER_VOTES));
            database.getParent().debug("Player stats created!");
        } else {
            this.savePlayerStats(ps);
        }
    }

    public synchronized void loadPlayer(Player p) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try (ResultSet set = database.query("SELECT * FROM BuildBattlePro_PlayerData WHERE UUID=?", p.getUniqueId().toString())) {
                    if (set.next()) {
                        BBPlayerStats stats = new BBPlayerStats(UUID.fromString(set.getString("UUID")), set.getString("Username"));
                        
                        // add the player's Username to the database if null
                        if (set.getString("Username") == null) {
                        	database.getParent().debug(p.getName() + "'s username not found on database. Adding it now.");
                        	addPlayerUsernameToTable(p);
                        	stats.setUsername(p.getName());
                        }
                        
                        for (BBStat stat : BBStat.values()) {
                            stats.setStat(stat, set.getObject(stat.getSQLKey()), false);
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
    
    private void addPlayerUsernameToTable(Player p) {
    	database.execute("UPDATE BuildBattlePro_PlayerData SET Username=? WHERE UUID=?", p.getName(), p.getUniqueId().toString());
    }

    public void loadAllReports(ReportManager manager) {
        try (ResultSet set = database.query("SELECT * FROM BuildBattlePro_ReportedBuilds")) {
            while (set.next()) {
                String id = set.getString("ID");
                List<UUID> reportedPlayers = new ArrayList<>();
                String reportedPlayersString = set.getString("ReportedPlayers");
                Arrays.asList(reportedPlayersString.split(",")).forEach(uuid -> reportedPlayers.add(UUID.fromString(uuid)));
                UUID reportedBy = UUID.fromString(set.getString("ReportedBy"));
                Date date = set.getTimestamp("Date");
                File schematic = new File(new File(database.getParent().getDataFolder(), ReportManager.REPORTS_DIRECTORY_NAME), set.getString("SchematicName"));
                BBReportStatus reportStatus = BBReportStatus.valueOf(set.getString("Status").toUpperCase());
                BBBuildReport report = new BBBuildReport(id, reportedPlayers, reportedBy, schematic, date, reportStatus);
                manager.addReport(report);
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
                    database.execute("INSERT INTO BuildBattlePro_ReportedBuilds(ID,ReportedPlayers,ReportedBy,Date,SchematicName,Status) VALUES(?,?,?,?,?,?)", report.getReportIDRaw(), report.getReportedPlayersInCommaSeparatedString(), report.getReportedBy().toString(), new Timestamp(report.getReportDate().getTime()), report.getSchematicFile().getName(), report.getReportStatus().name().toUpperCase());
                }
            }.runTaskAsynchronously(database.getParent());
        }
        return true;
    }

    public boolean deleteReport(BBBuildReport report) {
        database.execute("DELETE FROM BuildBattlePro_ReportedBuilds WHERE ID=?", report.getReportIDRaw());
        return true;
    }

    public void savePlayerStats(BBPlayerStats playerStats) {

        database.getParent().debug("Saving player stats to MySQL Database...");

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
                try (ResultSet set = database.query("SELECT * FROM BuildBattlePro_PlayerData")) {
                    while (set.next()) {
                        BBPlayerStats stats = new BBPlayerStats(UUID.fromString(set.getString("UUID")), set.getString("Username"));

                        for (BBStat stat : BBStat.values()) {
                            stats.setStat(stat, set.getObject(stat.getSQLKey()), false);
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
        database.execute("UPDATE BuildBattlePro_PlayerData SET " + stat.getSQLKey() + "=? WHERE UUID=?", bbPlayerStats.getStat(stat), bbPlayerStats.getUuid().toString());
        database.getParent().debug("Saved statistic " + stat.getSQLKey() + "=" + bbPlayerStats.getStat(stat) + " for UUID " + bbPlayerStats.getUuid().toString());
    }

    public Double getPlayerStat(BBStat stat, Player player) {
        ResultSet set = database.query("SELECT ? FROM BuildBattlePro_PlayerData WHERE UUID=?", stat.getSQLKey(), player.getUniqueId().toString());
        try {
            while (set.next()) {
                return set.getDouble(stat.getSQLKey());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public List<BBPlayerStats> loadTopStatistics(BBStat stat, int amountToDisplay) {
        List<BBPlayerStats> returnList = new ArrayList<>();
        ResultSet set = database.query("SELECT UUID,Username," + stat.getSQLKey() + " FROM BuildBattlePro_PlayerData ORDER BY " + stat.getSQLKey() + " DESC LIMIT ?", amountToDisplay);
        while (true) {
            try {
                if (!set.next()) break;
                BBPlayerStats stats = new BBPlayerStats(UUID.fromString(set.getString("UUID")), set.getString("Username"));
                stats.setStat(stat, set.getInt(stat.getSQLKey()), false);
                returnList.add(stats);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }
}
