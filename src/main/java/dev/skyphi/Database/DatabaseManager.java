package dev.skyphi.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.skyphi.IridiumAPI;

public class DatabaseManager implements IridiumAPI {
    
    private Database database;

    public DatabaseManager(Database database) {
        this.database = database;
    }

    @Override
    public void addPoints(UUID playerUUID, long points) {
        long newPoints = getPoints(playerUUID) + points;
        database.updatePoints(playerUUID.toString(), newPoints);
    }
    
    @Override
    public long getPoints(UUID playerUUID) {
        return database.getPoints(playerUUID.toString());
    }

    @Override
    public void addStatistic(String source, UUID playerUUID, String statistic, long value) {
        database.addStatistic(source, playerUUID, statistic, value);
    }

    @Override
    public Map<String, Integer> getStatistics(String source, UUID playerUUID) {
        String tableName = source + "_statistics";
        Map<String, Integer> statistics = new HashMap<>();

        try (Connection connection = database.getConnection()) {
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

}
