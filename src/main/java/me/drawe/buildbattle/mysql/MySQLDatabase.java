package me.drawe.buildbattle.mysql;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;


public class MySQLDatabase {

    @Getter
    private BuildBattle parent;

    private Connection connection;
    private String host, database, username, password;
    private int port;

    public MySQLDatabase(BuildBattle parent) {

        this.parent = parent;
        this.host = parent.getConfig().getString("mysql.host");
        this.port = parent.getConfig().getInt("mysql.port");
        this.database = parent.getConfig().getString("mysql.database");
        this.username = parent.getConfig().getString("mysql.username");
        this.password = parent.getConfig().getString("mysql.password");

    }


    public void connect() {
        if (!this.parent.isEnabled()) {
            this.parent.warning("Cannot maintain MySQL connection when plugin is disabled!");
            return;
        }

        Bukkit.getConsoleSender().sendMessage(parent.getSettings().getPrefix() + ChatColor.GREEN + "Attemping to connect to MySQL database...");

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    createTables();
                    parent.info("MySQL Database Connected !");
                } catch (Exception e) {
                    parent.severe("MySQL Database could not be connected ! Check your config.yml !");
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(parent);
    }

    private void openConnection() throws SQLException {

        synchronized (this) {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
        }
    }

    public ResultSet query(PreparedStatement statement) {

        if (!parent.isEnabled()) {
            this.parent.warning("Cannot execute SQL Query when plugin is disabled!");
            return null;
        }

        synchronized (this) {

            if (!isConnected()) {
                connect();
            }

            try {
                return statement.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void createTables() throws Exception {
        this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int NOT NULL, Wins int NOT NULL, MostPoints int NOT NULL, BlocksPlaced int NOT NULL, ParticlesPlaced int NOT NULL, SuperVotes int NOT NULL)").execute();
        this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS BuildBattlePro_ReportedBuilds(ID varchar(100) NOT NULL, ReportedPlayers text NOT NULL, ReportedBy varchar(36) NOT NULL, Date date NOT NULL, SchematicName text NOT NULL, Status varchar(30) NOT NULL)").execute();
        this.approveChanges();
    }

    private void approveChanges() throws Exception {
        DatabaseMetaData md;
        md = this.connection.getMetaData();
        ResultSet rs = md.getColumns(null, null, "BuildBattlePro_PlayerData", "SuperVotes");
        if (!rs.next()) {
            connection.prepareStatement("ALTER TABLE BuildBattlePro_PlayerData ADD SuperVotes int NOT NULL DEFAULT 0").execute();
            parent.info("MySQL detected that your table doesn't have SuperVotes column, adding it automatically!");
        }
        this.connection.prepareStatement("DROP TABLE IF EXISTS BuildBattlePro_Reports").execute();
    }

    public void update(String sql) {

        if (!parent.isEnabled()) {
            this.parent.warning("Cannot execute SQL when plugin is disabled!");
            return;
        }

        synchronized (this) {
            if (!isConnected()) {
                connect();
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(sql);
                        statement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(BuildBattle.getInstance());
        }
    }


    public PreparedStatement getStatement(String sql) {

        if (!parent.isEnabled()) {
            this.parent.warning("Cannot create SQL Statement when plugin is disabled!");
            return null;
        }

        synchronized (this) {

            if (!isConnected()) {
                connect();
            }

            try {
                return this.connection.prepareStatement(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public final void close() {
        if (connection != null)
            synchronized (this) {
                try {
                    connection.close();
                } catch (final SQLException e) {
                    parent.warning("Error closing MySQL connection!");
                }
            }
    }

    public final boolean isLoaded() {
        return connection != null;
    }

    public final boolean isConnected() {
        if (!isLoaded())
            return false;

        synchronized (this) {
            try {
                return connection != null && !connection.isClosed() && connection.isValid(0);
            } catch (final SQLException ex) {
                return false;
            }
        }
    }
}