// Package declaration for the Armadura plugin
package com.github.armadura;

// Importing the Player class from the Bukkit API to handle player-related functionality
import org.bukkit.entity.Player;

// Importing the JavaPlugin class from the Bukkit API, which is the base class for all plugins
import org.bukkit.plugin.java.JavaPlugin;

// Importing BukkitRunnable, a class that allows scheduling tasks in the Bukkit server
import org.bukkit.scheduler.BukkitRunnable;

// Main class for the Armadura plugin, extending JavaPlugin to inherit plugin functionalities
public class Armadura extends JavaPlugin {

    // Declaring a private variable to hold an instance of ArmorListener
    private ArmorListener armorListener;

    // Method called when the plugin is enabled
    @Override
    public void onEnable() {
        // Setting up a command executor for the "armor" command with an instance of ArmorCommand
        getCommand("armor").setExecutor(new ArmorCommand());

        // Registering event listeners for armor-related events using ArmorListener
        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);

        // Initializing the armorListener variable with a new instance of ArmorListener
        armorListener = new ArmorListener(this);

        // Creating a new BukkitRunnable to periodically apply armor effects to online players
        new BukkitRunnable() {
            // Overriding the run method to define the task that will be executed periodically
            @Override
            public void run() {
                // Iterating through all online players on the server
                for (Player player : getServer().getOnlinePlayers()) {
                    // Applying armor effects to each online player using armorListener
                    armorListener.applyArmorEffects(player);
                }
            }
        // Scheduling the task to run every 20 ticks (1 second) after an initial delay of 0 ticks
        }.runTaskTimer(this, 0L, 20);
    }
}
