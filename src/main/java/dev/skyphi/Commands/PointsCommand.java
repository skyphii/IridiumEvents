package dev.skyphi.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import dev.skyphi.IridiumEvents;

public class PointsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("iridium.points")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // 0 args, simulate /points get sender_name
        if (args.length == 0) {
            if (sender instanceof Player) {
                long points = IridiumEvents.DB.getPoints(((Player)sender).getUniqueId());
                sendPointsMsg(sender, sender.getName(), points);
            } else {
                sender.sendMessage(ChatColor.RED + "Console usage: /points <give/get> <player> [#]");
            }
            return true;
        }

        // regular players just need "/points"
        if (!sender.hasPermission("iridium.points.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        // 1 arg, simulate /points get <name>
        if (args.length == 1) {
            Player target = IridiumEvents.INSTANCE.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + "' not found.");
                return true;
            } else {
                long points = IridiumEvents.DB.getPoints(target.getUniqueId());
                sendPointsMsg(sender, target.getName(), points);
                return true;
            }
        }

        // 2 args, /points <give/get> <name>
        if (args.length == 2) {
            String action = args[0].toLowerCase();
            Player target = IridiumEvents.INSTANCE.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + "' not found.");
                return true;
            }

            switch (action) {
                case "get":
                    long points = IridiumEvents.DB.getPoints(target.getUniqueId());
                    sendPointsMsg(sender, target.getName(), points);
                    return true;
                case "give":
                    IridiumEvents.DB.addPoints(target.getUniqueId(), 1);
                    sender.sendMessage(IridiumEvents.PREFIX + ChatColor.DARK_AQUA
                                        + "Added " + ChatColor.GREEN + "1"
                                        + ChatColor.DARK_AQUA + " point to "
                                        + ChatColor.BOLD + target.getName());
                    return true;
            }
        }

        // 3 args, /points give <name> <amount>
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            Player target = IridiumEvents.INSTANCE.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + "' not found.");
                return true;
            }

            long amount = 0;
            try {
                amount = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Usage: /points <give/get> <player> [#]");
                return true;
            }

            IridiumEvents.DB.addPoints(target.getUniqueId(), amount);

            sender.sendMessage(IridiumEvents.PREFIX + ChatColor.DARK_AQUA
                                        + "Added " + ChatColor.GREEN + amount
                                        + ChatColor.DARK_AQUA + " points to "
                                        + ChatColor.BOLD + target.getName());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /points <give/get> <player> [#]");

        return true;
    }
    
    private void sendPointsMsg(CommandSender sender, String name, long points) {
        sender.sendMessage(IridiumEvents.PREFIX + ChatColor.DARK_AQUA
                    + name + "'s points: "
                    + ChatColor.BOLD + points);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLbl, String[] args) {
        final List<String> completions = new ArrayList<>();
        
        // regular players just need "/points"
        if (!sender.hasPermission("iridium.points.admin")) return completions;

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("give", "get"), completions);
        } else if (args.length == 2) {
            Collection<String> next = IridiumEvents.INSTANCE.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            StringUtil.copyPartialMatches(args[1], next, completions);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[0], List.of("1", "3", "5", "10"), completions);
        }
        return completions;
    }
    
}