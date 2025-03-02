package dev.skyphi.Services;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.skyphi.IridiumEvents;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderService extends PlaceholderExpansion {
    
    public String onPlaceholderRequest(Player player, String identifier) {
        /*
         * %iridium_online%
         * Returns the number of online players
         */
        if (identifier.equalsIgnoreCase("online")) {
            return String.valueOf(IridiumEvents.INSTANCE.getServer().getOnlinePlayers().size());
        }

        if (player == null) return "";

        /*
         * %iridium_player%
         * Returns the player name
         */
        if (identifier.equalsIgnoreCase("player")) {
            return player.getName();
        }

        /*
         * %iridium_points%
         * Returns the player's points
         */
        if (identifier.equalsIgnoreCase("points")) {
            return ""+IridiumEvents.DB.getPoints(player.getUniqueId());
        }

        String[] split = identifier.split("_");

        /*
         * %iridium_statistic_<table>_<stat>%
         * Returns the player's specified statistic
         */
        if (split.length >= 3 && split[0].equalsIgnoreCase("statistic")) {
            String table = split[1],
                stat = identifier.substring(split[0].length()+split[1].length()+2); // some stats may contain underscores, so just strip "statistic_<table>_"
            return ""+IridiumEvents.DB.getStatistic(table, stat, player.getUniqueId());
        }

        return null;
    }

    @Override
    public String getIdentifier() {
        return "iridium";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public @NotNull String getAuthor() {
        return "skyphi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

}
