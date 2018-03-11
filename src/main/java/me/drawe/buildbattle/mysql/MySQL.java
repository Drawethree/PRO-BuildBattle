package me.drawe.buildbattle.mysql;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.Properties;


public class MySQL {

    private static final MySQL mysql = new MySQL();

    private MySQL() {

    }

    public static MySQL getInstance() {
        return mysql;
    }

    private static Connection connection;
    private static String host, database, username, password;
    private static int port;

    public static Connection getConnection() throws Exception {
        if(connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        }
        return connection;
    }

    public void connect() {
        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix() + " §aAttemping to connect to MySQL database...");
        try {
            host = BuildBattle.getInstance().getConfig().getString("mysql.host");
            port = BuildBattle.getInstance().getConfig().getInt("mysql.port");
            database = BuildBattle.getInstance().getConfig().getString(".mysql.database");
            username = BuildBattle.getInstance().getConfig().getString("mysql.username");
            password = BuildBattle.getInstance().getConfig().getString("mysql.password");
        } catch (Exception e) {
            BuildBattle.severe("§cMySQL could not be connected ! Check your config.yml !");
            e.printStackTrace();
            return;
        }
        try {
            openConnection();
            createTables();
            BuildBattle.info("§aMySQL Connected !");
            BuildBattle.getInstance().setMysqlEnabled(true);
        } catch (Exception e) {
            BuildBattle.severe("§cMySQL could not be connected ! Check your config.yml !");
            e.printStackTrace();
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
            connection = (Connection) DriverManager.getConnection(
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

    public static void createTables() {
        update("CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int NOT NULL, Wins int NOT NULL, MostPoints int NOT NULL, BlocksPlaced int NOT NULL, ParticlesPlaced int NOT NULL)");
        update("CREATE TABLE IF NOT EXISTS BuildBattlePro_Reports(Player varchar(36) NOT NULL, ReportedBy varchar(36) NOT NULL, Date TEXT NOT NULL)");
    }

    public static void update(String sql) {
        try {
            getConnection().prepareStatement(sql).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insert(String sql) {
        try {
            getConnection().prepareStatement(sql).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}