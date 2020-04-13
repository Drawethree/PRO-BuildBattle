package me.drawethree.buildbattle.mysql;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;


public class MySQLDatabase {

    @Getter
    private BuildBattle parent;

    private Connection connection;
    private DatabaseCredentials credentials;

    public MySQLDatabase(BuildBattle parent) {

        this.parent = parent;
        this.credentials = new DatabaseCredentials(
                parent.getConfig().getString("mysql.host"),
                parent.getConfig().getString("mysql.database"),
                parent.getConfig().getString("mysql.username"),
                parent.getConfig().getString("mysql.password"),
                parent.getConfig().getInt("mysql.port")
        );
        this.connect();
    }


    private void connect() {
        if (!this.parent.isEnabled()) {
            this.parent.warning("Cannot maintain MySQL connection when plugin is disabled!");
            return;
        }

        Bukkit.getConsoleSender().sendMessage(parent.getSettings().getPrefix() + ChatColor.GREEN + "Attemping to connect to MySQL database...");

        try {
            openConnection();
            createTables();
            parent.info("MySQL Database Connected !");
        } catch (Exception e) {
            parent.severe("MySQL Database could not be connected ! Check your config.yml !");
            e.printStackTrace();
        }
    }

    private void openConnection() throws SQLException {
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://" + this.credentials.getHost() + ":" + this.credentials.getPort() + "/" + this.credentials.getDatabaseName(), this.credentials.getUserName(), this.credentials.getPassword());
    }

    public ResultSet query(String sql, Object... replacements) {

        if (!parent.isEnabled()) {
            this.parent.warning("Cannot execute SQL Query when plugin is disabled!");
            return null;
        }

        try {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            if (replacements != null) {
                for (int i = 0; i < replacements.length; i++) {
                    statement.setObject(i + 1, replacements[i]);
                }
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void createTables() {
        execute("CREATE TABLE IF NOT EXISTS BuildBattlePro_PlayerData(UUID varchar(36) NOT NULL, Played int NOT NULL, Wins int NOT NULL, MostPoints int NOT NULL, BlocksPlaced int NOT NULL, ParticlesPlaced int NOT NULL, SuperVotes int NOT NULL, primary key (UUID))");
        execute("CREATE TABLE IF NOT EXISTS BuildBattlePro_ReportedBuilds(ID int NOT NULL AUTO_INCREMENT, ReportedPlayers text NOT NULL, ReportedBy varchar(36) NOT NULL, Date date NOT NULL, SchematicName text NOT NULL, Status varchar(30) NOT NULL, primary key (ID))");
        this.approveChanges();
    }

    private void approveChanges() {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    DatabaseMetaData md = getConnection().getMetaData();
                    ResultSet rs = md.getColumns(null, null, "BuildBattlePro_PlayerData", "SuperVotes");
                    if (!rs.next()) {
                        execute("ALTER TABLE BuildBattlePro_PlayerData ADD SuperVotes int NOT NULL DEFAULT 0");
                        parent.info("MySQL detected that your table doesn't have SuperVotes column, adding it automatically!");
                    }
                    execute("DROP TABLE IF EXISTS BuildBattlePro_Reports");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.parent);

    }


    public void execute(String sql, Object... replacements) {

        if (!parent.isEnabled()) {
            this.parent.warning("Cannot execute SQL when plugin is disabled!");
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
                    if (replacements != null) {
                        for (int i = 0; i < replacements.length; i++) {
                            statement.setObject(i + 1, replacements[i]);
                        }
                    }
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.parent);

    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                parent.warning("Error closing MySQL connection!");
                e.printStackTrace();
            }
        }
    }


    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.credentials.getHost() + ":" + this.credentials.getPort() + "/" + this.credentials.getDatabaseName(), this.credentials.getUserName(), this.credentials.getPassword());
        }
        return connection;
    }
}