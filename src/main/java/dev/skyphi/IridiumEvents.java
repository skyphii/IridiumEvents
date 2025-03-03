package dev.skyphi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Commands.PointsCommand;
import dev.skyphi.Commands.StatisticCommand;
import dev.skyphi.Databases.Database;
import dev.skyphi.Databases.SQLiteDatabase;
import dev.skyphi.Services.DatabaseService;
import dev.skyphi.Services.IridiumAPI;
import dev.skyphi.Services.PlaceholderService;

public class IridiumEvents extends JavaPlugin {

    public static final String PREFIX = ChatColor.GOLD+""+ChatColor.BOLD + "["
                                        + ChatColor.AQUA+""+ChatColor.BOLD + "IridiumEvents"
                                        + ChatColor.GOLD+""+ChatColor.BOLD + "] ";

    public static IridiumEvents INSTANCE;
    public static DatabaseService DB;
    
    private Database database;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.saveDefaultConfig();

        database = new SQLiteDatabase();
        database.initDatabase();
        DB = new DatabaseService(database);

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
