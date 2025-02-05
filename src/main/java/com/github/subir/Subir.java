package com.github.subir;

// Import necessary classes from the Bukkit API for plugin development
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Material;
import java.util.List;
import java.util.ArrayList;

// Main class that extends JavaPlugin, the base class for all Bukkit plugins
public class Subir extends JavaPlugin {
    
    // Called when the plugin is enabled
    public void onEnable() {
        // Save the default configuration file if it doesn't exist
        saveResource("config.yml", false);
        
        // Register event listeners for handling specific game events
        // Bukkit.getPluginManager().registerEvents(new Menu(this), this);
        
        // Set command executors for custom commands defined in plugin.yml
        this.getCommand("subir").setExecutor(this);
        this.getCommand("s").setExecutor(this);
    }

    // Handle commands sent by players or console
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        // Check if no arguments were provided; display plugin version info
        if (a.length == 0) {
            s.sendMessage("§d[Subir] Subir, version 0, created by magenta555");
            s.sendMessage("§d[Subir] Leveling plugin for Minecraft servers!");
            return true; // Command processed successfully
        }
        
        // Reload the plugin's configuration if "reload" command is issued
        if (a[0].equals("reload")) {
            reloadConfig(); // Reloads the config.yml file from disk
            s.sendMessage("§d[Subir] Reloaded Subir config.yml");
            return true; // Command processed successfully
        }
        
        if (a[0].equals("menu")) {
            s.sendMessage("§d[Subir] Opening the menu..."); // Confirm action to sender
        }
        
        return true; // Command processed successfully
    }

    // Provide tab completion options for commands
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> tabComplete = new ArrayList<String>();

        if (a.length == 1) {
            tabComplete.add("menu");
        }
        
        return tabComplete;
    }
}
