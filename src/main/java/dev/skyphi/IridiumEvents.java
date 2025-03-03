package dev.skyphi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Commands.PointsCommand;
import dev.skyphi.Commands.StatisticCommand;
import dev.skyphi.Database.Database;
import dev.skyphi.Database.DatabaseManager;
import dev.skyphi.Database.SQLiteDatabase;
import dev.skyphi.Services.PlaceholderService;

public class IridiumEvents extends JavaPlugin {
    public static final String PREFIX = ChatColor.GOLD+""+ChatColor.BOLD + "["
                                        + ChatColor.AQUA+""+ChatColor.BOLD + "IridiumEvents"
                                        + ChatColor.GOLD+""+ChatColor.BOLD + "] ";

    public static IridiumEvents INSTANCE;
    public static DatabaseManager DB;
    
    private Database database;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.saveDefaultConfig();

        database = new SQLiteDatabase();
        database.initDatabase();
        DB = new DatabaseManager(database);

        getServer().getServicesManager().register(IridiumAPI.class, DB, this, ServicePriority.Normal);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderService().register();
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will not be updated.");
        }

        this.getCommand("statistic").setExecutor(new StatisticCommand());
        this.getCommand("points").setExecutor(new PointsCommand());
    }

    @Override
    public void onDisable() {
        
    }

}
