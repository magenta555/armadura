// Package declaration for the Armadura plugin
package com.github.armadura;

// Importing classes for chat color handling from the Bukkit API
import org.bukkit.ChatColor;

// Importing Player class to interact with player entities in the game
import org.bukkit.entity.Player;

// Importing Listener interface to handle events in the Bukkit API
import org.bukkit.event.Listener;

// Importing classes for item handling in the player's inventory
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

// Importing JavaPlugin class to access plugin functionalities
import org.bukkit.plugin.java.JavaPlugin;

// Importing classes for potion effects in the game
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// Importing classes for list handling
import java.util.List;

// Class that implements Listener interface to handle armor-related events
public class ArmorListener implements Listener {

    // Declaring a variable to hold a reference to the main plugin instance
    public JavaPlugin plugin;

    // Constructor that initializes the ArmorListener with a reference to the main plugin
    public ArmorListener(JavaPlugin plugin) {
        this.plugin = plugin; // Assigning the provided plugin instance to the class variable
    }

    // Suppressing deprecation warnings for using deprecated methods
    @SuppressWarnings("deprecation")
    // Method that applies potion effects based on armor lore to a player
    public void applyArmorEffects(Player player) {
        // Iterating through all possible potion effect types
        for (PotionEffectType type : PotionEffectType.values()) {
            // Checking if the type is not null and if the player currently has that potion effect applied
            if (type != null && player.hasPotionEffect(type)) {
                // Removing the existing potion effect from the player
                player.removePotionEffect(type);
            }
        }

        // Getting the player's inventory to access their armor contents
        PlayerInventory inventory = player.getInventory();
        ItemStack[] armorContents = inventory.getArmorContents(); // Retrieving all equipped armor pieces

        // Iterating through each armor piece in the player's inventory
        for (ItemStack armorPiece : armorContents) {
            // Checking if the armor piece is not null and has item metadata (lore)
            if (armorPiece != null && armorPiece.hasItemMeta()) {
                ItemMeta meta = armorPiece.getItemMeta(); // Retrieving item metadata

                // Checking if the item metadata has lore associated with it
                if (meta.hasLore()) {
                    List<String> lore = meta.getLore(); // Retrieving lore lines from metadata

                    // Iterating through each line of lore associated with the armor piece
                    for (String loreLine : lore) {
                        // Checking if the lore line starts with Aqua color code, indicating it's an effect description
                        if (loreLine.startsWith(ChatColor.AQUA.toString())) {
                            String effectData = ChatColor.stripColor(loreLine); // Stripping color codes from lore line

                            String[] parts = effectData.split(" "); // Splitting effect data into parts (effect name and magnitude)
                            if (parts.length == 2) { // Ensuring there are exactly two parts: effect name and magnitude
                                String effectName = parts[0].toUpperCase(); // Converting effect name to uppercase for consistency
                                int magnitude = romanToInt(parts[1]); // Converting Roman numeral magnitude to integer

                                PotionEffectType potionEffectType = PotionEffectType.getByName(effectName); // Getting potion effect type by name

                                // Checking if the potion effect type is valid (not null)
                                if (potionEffectType != null) {
                                    // Adding a new potion effect to the player with a duration of 12 seconds and specified magnitude
                                    player.addPotionEffect(new PotionEffect(potionEffectType, 12 * 20, magnitude - 1, false, false));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Method to convert a Roman numeral string into its integer representation
    private int romanToInt(String roman) {
        String romanLiterals[] = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}; 
        int romanValues[] = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1}; 

        int result = 0; // Variable to accumulate the integer value of the Roman numeral

        // Iterating through each Roman literal and its corresponding value
        for (int i = 0; i < romanLiterals.length; i++) {
            while (roman.startsWith(romanLiterals[i])) { 
                result += romanValues[i]; // Adding value of Roman literal to result
                roman = roman.substring(romanLiterals[i].length()); // Removing processed Roman literal from string
            }
        }
        
        return result; // Returning final integer value of Roman numeral representation
    }
}
