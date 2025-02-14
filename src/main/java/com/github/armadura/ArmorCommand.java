// Package declaration for the Armadura plugin
package com.github.armadura;

// Importing classes for chat color handling from the Bukkit API
import org.bukkit.ChatColor;

// Importing classes for command handling from the Bukkit API
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// Importing Player class to interact with player entities in the game
import org.bukkit.entity.Player;

// Importing classes for item handling in the player's inventory
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

// Importing classes for tab completion functionality in commands
import org.bukkit.command.TabCompleter;

// Importing classes for list handling
import java.util.ArrayList;
import java.util.List;

// Importing PotionEffectType to handle potion effects in the game
import org.bukkit.potion.PotionEffectType;

// Class that implements CommandExecutor and TabCompleter interfaces to handle armor commands
public class ArmorCommand implements CommandExecutor, TabCompleter {

    // Suppressing deprecation warnings for using deprecated methods
    @SuppressWarnings("deprecation")
    @Override
    // Method that is called when a command is executed
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Checking if the command sender is not a player
        if (!(sender instanceof Player)) {
            // Sending a message to non-player command senders
            sender.sendMessage(ChatColor.AQUA + "This command can only be used by players.");
            return true; // Command execution ends here
        }

        // Casting the sender to a Player object
        Player player = (Player) sender;

        // Checking if the number of arguments is not equal to 2
        if (args.length != 2) {
            // Sending usage instructions to the player
            player.sendMessage(ChatColor.AQUA + "Usage: /armor [potion effect] [magnitude]");
            return true; // Command execution ends here
        }

        // Converting the potion effect name argument to uppercase for consistency
        String potionEffectName = args[0].toUpperCase();
        int magnitude;

        // Attempting to parse the second argument as an integer for magnitude
        try {
            magnitude = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            // Sending an error message if parsing fails
            player.sendMessage(ChatColor.AQUA + "Invalid magnitude. Must be a number.");
            return true; // Command execution ends here
        }

        // Getting the PotionEffectType corresponding to the provided name
        PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffectName);

        // Checking if the potion effect type is valid (not null)
        if (potionEffectType == null) {
            // Sending an error message if the potion effect is invalid
            player.sendMessage(ChatColor.AQUA + "Invalid potion effect.");
            return true; // Command execution ends here
        }

        // Getting the item currently held in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Checking if the item is null or of type AIR (not holding anything)
        if (item == null || item.getType() == Material.AIR) {
            // Sending an error message if no armor piece is being held
            player.sendMessage(ChatColor.AQUA + "You must be holding an armor piece.");
            return true; // Command execution ends here
        }

        // Getting the item's metadata to modify its lore (description)
        ItemMeta meta = item.getItemMeta();
        
        // Retrieving the current lore of the item, or initializing it as a new list if null
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>(); // Initializing a new list for lore if it was null
        }

        // Creating a string representing the potion effect and its magnitude in Roman numerals
        String loreText = ChatColor.AQUA + potionEffectName + " " + toRoman(magnitude);
        
        // Adding the new lore text to the item's lore list
        lore.add(loreText);
        
        // Setting the modified lore back to the item's metadata
        meta.setLore(lore);
        
        // Updating the item with its new metadata, which includes updated lore
        item.setItemMeta(meta);

        // Sending confirmation message to the player that the potion effect was added successfully
        player.sendMessage(ChatColor.AQUA + "Potion effect added to the armor!");
        
        return true; // Command execution completed successfully
    }

    // Method to convert an integer into its Roman numeral representation as a string
    private String toRoman(int num) {
        String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

        StringBuilder roman = new StringBuilder(); // StringBuilder to construct Roman numeral string

        for (int i = 0; i < romanValues.length; i++) {
            while (num >= romanValues[i]) { 
                num -= romanValues[i]; 
                roman.append(romanLiterals[i]); 
            }
        }
        
        return roman.toString(); // Returning final Roman numeral string representation
    }

    // Suppressing deprecation warnings for using deprecated methods in tab completion functionality 
    @SuppressWarnings("deprecation")
    @Override 
    // Method that provides tab completion suggestions for commands 
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        // Checking if there is only one argument provided by user 
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(); // List to hold possible completions
            
            for (PotionEffectType type : PotionEffectType.values()) { 
                // Checking if type is not null and starts with user input 
                if (type != null && type.getName().toUpperCase().startsWith(args[0].toUpperCase())) { 
                    completions.add(type.getName()); 
                }
            }
            
            return completions; // Returning list of completions based on user input 
            
        } else { 
            return null; // Returning null when no completions are applicable 
            
        }
    }
}
