package dev.skyphi.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteDatabase implements Database {
    private static final String DB_URL = "jdbc:sqlite:plugins/IridiumEvents/database.db";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void initDatabase() {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {

            String createTableQuery = "CREATE TABLE IF NOT EXISTS player_data (" +
                    "player_uuid TEXT PRIMARY KEY, " +
                    "player_name TEXT NOT NULL, " +
                    "points INTEGER DEFAULT 0, " +
                    "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            statement.execute(createTableQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tableExists(String tableName) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void savePlayer(String playerUUID, String playerName) {
        try (Connection connection = getConnection()) {
            String query = "INSERT OR REPLACE INTO player_data (player_uuid, player_name) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ** Points ** //

    @Override
    public long getPoints(String playerUUID) {
        try (Connection connection = getConnection()) {
            String query = "SELECT points FROM player_data WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void updatePoints(String playerUUID, long points) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE player_data SET points = ? WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, points);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ** Statistics ** //

    public void addStatistic(String source, String statistic, UUID playerUUID, long value) {
        String tableName = source + "_statistics";

        createTableIfNotExists(tableName);
        addColumnIfNotExists(tableName, statistic);

        try (Connection connection = getConnection()) {
            String query = "INSERT OR REPLACE INTO " + tableName + " (player_uuid, " + statistic + ") " +
                    "VALUES (?, COALESCE((SELECT " + statistic + " FROM " + tableName
                    + " WHERE player_uuid = ?), 0) + ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            statement.setString(2, playerUUID.toString());
            statement.setLong(3, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Integer> getStatistics(String source, UUID playerUUID) {
        String tableName = source + "_statistics";
        Map<String, Integer> statistics = new HashMap<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 2; i <= columnCount; i++) { // Skip the first column (player_uuid)
                    String columnName = metaData.getColumnName(i);
                    int value = resultSet.getInt(i);
                    statistics.put(columnName, value);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statistics;
    }

    @Override
    public long getStatistic(String source, String statistic, UUID playerUUID) {
        String tableName = source + "_statistics";

        try (Connection connection = getConnection()) {
            String query = "SELECT " + statistic + " FROM " + tableName + " WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(statistic);
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("no such column")) {
                return -1;
            } else e.printStackTrace();
        }

        return 0;
    }

    @Override
    public List<String> getStatisticsTableNames() {
        List<String> tableNames = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name LIKE ?")) {
            statement.setString(1, "%_statistics");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String tableName = resultSet.getString("name");
                    tableNames.add(tableName.substring(0, tableName.indexOf('_')));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableNames;
    }

    @Override
    public List<String> getStatisticsNames(String source) {
        String tableName = source + "_statistics";
        List<String> statNames = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + tableName + " LIMIT 0")) {

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData meta = resultSet.getMetaData();
                // start at index=2 because index=1 is the player UUID
                for(int i = 2; i <= meta.getColumnCount(); i++) {
                    statNames.add(meta.getColumnLabel(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statNames;
    }


    // ** Helper Methods ** //

    private boolean columnExists(String tableName, String columnName) {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
                return columns.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addColumnIfNotExists(String tableName, String columnName) {
        if (!columnExists(tableName, columnName)) {
            try (Connection connection = getConnection();
                    Statement statement = connection.createStatement()) {

                String alterTableQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName
                        + " INTEGER DEFAULT 0";
                statement.execute(alterTableQuery);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTableIfNotExists(String tableName) {
        tableName = tableName.toLowerCase();
        if (!tableExists(tableName)) {
            try (Connection connection = getConnection();
                    Statement statement = connection.createStatement()) {

                String createTableQuery = "CREATE TABLE " + tableName + " (" +
                        "player_uuid TEXT PRIMARY KEY" +
                        ")";
                statement.execute(createTableQuery);
                System.out.println("Table '" + tableName + "' created successfully.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
