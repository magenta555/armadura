package com.github.armadura;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ArmorListener implements Listener {

    public JavaPlugin plugin;
    
        public ArmorListener(JavaPlugin plugin) {
            this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            applyArmorEffects(player);
        }
    }

    @SuppressWarnings("deprecation")
    private void applyArmorEffects(Player player) {
        // Clear existing potion effects from armor
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null && player.hasPotionEffect(type)) {
                player.removePotionEffect(type);
            }
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack[] armorContents = inventory.getArmorContents();

        for (ItemStack armorPiece : armorContents) {
            if (armorPiece != null && armorPiece.hasItemMeta()) {
                ItemMeta meta = armorPiece.getItemMeta();
                if (meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    for (String loreLine : lore) {
                        if (loreLine.startsWith(ChatColor.DARK_PURPLE.toString())) {
                            // Extract potion effect and magnitude from lore
                            String effectData = ChatColor.stripColor(loreLine);
                            String[] parts = effectData.split(" ");
                            if (parts.length == 2) {
                                String effectName = parts[0].toUpperCase();
                                int magnitude = romanToInt(parts[1]); // Convert Roman numeral to integer

                                PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
                                if (potionEffectType != null) {
                                    player.addPotionEffect(new PotionEffect(potionEffectType, 40 * 20, magnitude - 1, false, false)); // 40 seconds, magnitude -1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper function to convert Roman numerals to integers
    private int romanToInt(String roman) {
        String romanLiterals[] = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int romanValues[] = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

        int result = 0;
        for (int i = 0; i < romanLiterals.length; i++) {
            while (roman.startsWith(romanLiterals[i])) {
                result += romanValues[i];
                roman = roman.substring(romanLiterals[i].length());
            }
        }
        return result;
    }
}
