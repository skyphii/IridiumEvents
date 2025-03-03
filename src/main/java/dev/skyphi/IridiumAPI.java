package dev.skyphi;

import java.util.Map;
import java.util.UUID;

public interface IridiumAPI {

    /**
     * Adds points to a player
     * 
     * @param playerUUID the player's UUID
     * @param points the number of points to add
     */
    public void addPoints(UUID playerUUID, long points);

    /**
     * Gets number of points a player has
     * 
     * @param playerUUID the player's UUID
     * @return the number of points
     */
    public long getPoints(UUID playerUUID);


    /**
     * Adds a value to a player's statistic
     * 
     * @param source the unique source string (each plugin MUST have a unique identifier)
     * @param statistic the statistic key string (should be formatted in lower snake case)
     * @param playerUUID the player's UUID
     * @param value the number to add to the statistic
     */
    public void addStatistic(String source, String statistic, UUID playerUUID, long value);

    /**
     * Gets all statistics from the specified source for a player
     * 
     * @param source the unique source string (each plugin MUST have a unique identifier)
     * @param playerUUID the player's UUID
     * @return a mapping of statistic key:value pairs
     */
    public Map<String, Long> getStatistics(String source, UUID playerUUID);

    /**
     * Gets a statistic from the specified source for a player
     * 
     * @param source the unique source string (each plugin MUST have a unique identifier)
     * @param statistic the statistic key string (should be formatted in lower snake case)
     * @param playerUUID the player's UUID
     * @return the value of the statistic
     */
    public long getStatistic(String source, String statistic, UUID playerUUID);

}
