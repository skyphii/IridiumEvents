package dev.skyphi.Services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.skyphi.Databases.Database;

public class DatabaseService implements IridiumAPI {
    
    private Database database;

    public DatabaseService(Database database) {
        this.database = database;
    }

    // ** API Methods ** //

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
    public void addStatistic(String source, String statistic, UUID playerUUID, long value) {
        database.addStatistic(source, statistic, playerUUID, value);
    }

    @Override
    public Map<String, Long> getStatistics(String source, UUID playerUUID) {
        return database.getStatistics(source, playerUUID);
    }

    @Override
    public long getStatistic(String source, String statistic, UUID playerUUID) {
        return database.getStatistic(source, statistic, playerUUID);
    }


    // ** Unexposed Methods ** //

    public List<String> getStatisticsTableNames() {
        return database.getStatisticsTableNames();
    }

    public List<String> getStatisticsNames(String source) {
        return database.getStatisticsNames(source);
    }

}
