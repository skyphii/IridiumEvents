package dev.skyphi.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Database {
    Connection getConnection() throws SQLException;
    void initDatabase();

    boolean tableExists(String tableName);

    void savePlayer(String playerUUID, String playerName);

    long getPoints(String playerUUID);
    void updatePoints(String playerUUID, long points);

    void addStatistic(String source, String statistic, UUID playerUUID, long value);
    Map<String, Integer> getStatistics(String source, UUID playerUUID);
    long getStatistic(String source, String statistic, UUID playerUUID);
    
    List<String> getStatisticsTableNames();
    List<String> getStatisticsNames(String source);
}