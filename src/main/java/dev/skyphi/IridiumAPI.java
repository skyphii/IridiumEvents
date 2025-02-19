package dev.skyphi;

import java.util.Map;
import java.util.UUID;

public interface IridiumAPI {

    public void addPoints(UUID playerUUID, long points);
    public long getPoints(UUID playerUUID);

    public void addStatistic(String source, UUID playerUUID, String statistic, long value);
    public Map<String, Integer> getStatistics(String source, UUID playerUUID);

}
