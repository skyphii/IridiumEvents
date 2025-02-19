package dev.skyphi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Database.DatabaseManager;
import dev.skyphi.Commands.StatisticCommand;
import dev.skyphi.Database.Database;
import dev.skyphi.Database.SQLiteDatabase;

public class IridiumEvents extends JavaPlugin {

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
            // activate placeholder stuff
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will not be updated.");
        }

        this.getCommand("statistic").setExecutor(new StatisticCommand());
    }

    @Override
    public void onDisable() {
        
    }

}
