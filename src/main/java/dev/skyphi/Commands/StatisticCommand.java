package dev.skyphi.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.base.CaseFormat;

import dev.skyphi.IridiumEvents;

public class StatisticCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("iridiumevents.statistic")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /statistic <player> <table> <statistic>");
            return true;
        }

        Player player = IridiumEvents.INSTANCE.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.AQUA + args[0] + ChatColor.RED + "' not found.");
            return true;
        }
        String source = args[1].toLowerCase();
        String statistic = args[2].toLowerCase();

        long value = IridiumEvents.DB.getStatistic(source, statistic, player.getUniqueId());
        if (value == -1) {
            sender.sendMessage(ChatColor.RED + "Statistic '" + ChatColor.AQUA + source.toUpperCase() + " - " + statistic + ChatColor.RED + "' not found.");
            return true;
        }

        sender.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD + player.getName()
            + ChatColor.YELLOW+""+ChatColor.BOLD + " - " + source.toUpperCase() + " - " + formatStatistic(statistic) + ": "
            + ChatColor.WHITE+""+ChatColor.BOLD + value);

        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command arg1cmd, String s, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : IridiumEvents.INSTANCE.getServer().getOnlinePlayers()) {
                String name = player.getName();
                if (name.startsWith(args[0])) completions.add(name);
            }
        }
        if (args.length == 2) StringUtil.copyPartialMatches(args[1], IridiumEvents.DB.getStatisticsTableNames(), completions);
        if (args.length == 3) StringUtil.copyPartialMatches(args[2], IridiumEvents.DB.getStatisticsNames(args[1]), completions);
        return completions;
    }

    private String formatStatistic(String statistic) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, statistic);
    }

}
