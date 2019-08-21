package me.drawe.buildbattle.mysql;

import me.drawe.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;


public class MySQL {

    private static final MySQL mysql = new MySQL();
    private static Connection connection;
    private static String host, database, username, password;
    private static int port;

    private MySQL() {

    }

    public static MySQL getInstance() {
        return mysql;
    }


    public static Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useUnicode=yes", username, password);
        }
        return connection;
    }

    public void connect() {
        Bukkit.getConsoleSender().sendMessage(BuildBattle.getInstance().getSettings().getPrefix() + "§aAttemping to connect to MySQL database...");
        try {
            host = BuildBattle.getInstance().getConfig().getString("mysql.host");
            port = BuildBattle.getInstance().getConfig().getInt("mysql.port");
            database = BuildBattle.getInstance().getConfig().getString(".mysql.database");
            username = BuildBattle.getInstance().getConfig().getString("mysql.username");
            password = BuildBattle.getInstance().getConfig().getString("mysql.password");
        } catch (Exception e) {
            BuildBattle.getInstance().severe("§cMySQL could not be connected ! Check your config.yml !");
            e.printStackTrace();
            return;
        }
        if (BuildBattle.getInstance().isEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        openConnection();
                        createTables();
                        BuildBattle.getInstance().info("§aMySQL Connected !");
                    } catch (Exception e) {
                        BuildBattle.getInstance().severe("§cMySQL could not be connected ! Check your config.yml !");
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(BuildBattle.getInstance());
        } else {
            try {
                openConnection();
                createTables();
                BuildBattle.getInstance().info("§aMySQL Connected !");
            } catch (Exception e) {
                BuildBattle.getInstance().severe("§cMySQL could not be connected ! Check your config.yml !");
                e.printStackTrace();
            }
        }
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public static ResultSet getResult(String sql) {
        try {
            return getConnection().prepareStatement(sql).executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createTables() throws Exception {
        getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int NOT NULL, Wins int NOT NULL, MostPoints int NOT NULL, BlocksPlaced int NOT NULL, ParticlesPlaced int NOT NULL, SuperVotes int NOT NULL)").execute();
        getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS BuildBattlePro_ReportedBuilds(ID varchar(100) NOT NULL, ReportedPlayers text NOT NULL, ReportedBy varchar(36) NOT NULL, Date date NOT NULL, SchematicName text NOT NULL, Status varchar(30) NOT NULL)").execute();
        approveChanges();
    }

    private static void approveChanges() throws Exception {
        DatabaseMetaData md;
        md = getConnection().getMetaData();
        ResultSet rs = md.getColumns(null, null, "BuildBattlePro_PlayerData", "SuperVotes");
        if (!rs.next()) {
            getConnection().prepareStatement("ALTER TABLE BuildBattlePro_PlayerData ADD SuperVotes int NOT NULL DEFAULT 0").execute();
            BuildBattle.getInstance().info("§aMySQL detected that your table doesn't have §eSuperVotes §acolumn, adding it automatically!");
        }
        getConnection().prepareStatement("DROP TABLE IF EXISTS BuildBattlePro_Reports").execute();
    }

    public static void update(String sql) {
        if (BuildBattle.getInstance().isEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        getConnection().prepareStatement(sql).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(BuildBattle.getInstance());
        } else {
            try {
                getConnection().prepareStatement(sql).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static PreparedStatement getStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}