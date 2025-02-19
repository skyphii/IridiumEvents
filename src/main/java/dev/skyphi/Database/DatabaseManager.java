package dev.skyphi.Database;

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
        return database.getStatistics(source, playerUUID);
    }

}
