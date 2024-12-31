package com.akenland.elytradactyl;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.annotation.command.Commands;

/**
 * Commands for the Elytradactyl plugin.
 */
@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "elytradactyl", desc = "View plugin information, and reload the plugin.", usage = "/elytradactyl [version|reload]", permission = "elytradactyl.admin"))
public class ElytradactylCommands implements TabExecutor {

    private final ElytradactylPlugin plugin;

    public ElytradactylCommands(ElytradactylPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Version command
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(plugin.getName() + " " + plugin.getDescription().getVersion() + " by "
                    + plugin.getDescription().getAuthors().get(0));
            sender.sendMessage("- " + plugin.getDescription().getDescription());
            sender.sendMessage("- Website: " + plugin.getDescription().getWebsite());
            sender.sendMessage("- Checking for falling mobs every " + plugin.checkTicks + " ticks.");
            return true;
        }

        // Reload command
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage("Elytradactyl reloaded.");
            return true;
        }

        // Invalid command
        sender.sendMessage("Invalid arguments.");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Main command - return each sub-command
        if (args.length <= 1)
            return Arrays.asList("version", "reload");
        // Otherwise return nothing
        return Arrays.asList("");
    }

}