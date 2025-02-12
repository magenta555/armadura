package com.github.armadura;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class Armadura extends JavaPlugin {

    private PotionEffectTask potionEffectTask;

    @Override
    public void onEnable() {
        // Register command
        getCommand("armor").setExecutor(new ArmorCommand(this));

        // Register event listener
        getServer().getPluginManager().registerEvents(new ArmorEquipListener(this), this);

        // Start the PotionEffectTask
        potionEffectTask = new PotionEffectTask(this);
        potionEffectTask.runTaskTimer(this, 0L, 20L * 10); // Run every 10 seconds (20 ticks * 10 = 200 ticks)

        getLogger().info("Armadura plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop the PotionEffectTask
        if (potionEffectTask != null) {
            potionEffectTask.cancel();
        }
        getLogger().info("Armadura plugin has been disabled!");
    }

    public class ArmorCommand implements CommandExecutor {

        private final Plugin plugin;

        public ArmorCommand(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("armadura.armor")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /armor <potion_effect> <magnitude>");
                return true;
            }

            String potionEffectName = args[0].toUpperCase();
            int magnitude;

            try {
                magnitude = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Magnitude must be an integer.");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "You must be holding an item.");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

            // Generate the lore text using the PotionEffectHelper
            String loreText = PotionEffectHelper.generatePotionLore(potionEffectName, magnitude);
            lore.add(loreText);

            meta.setLore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "Potion effect lore added to the item.");
            return true;
        }
    }

    public class ArmorEquipListener implements Listener {

        private final Plugin plugin;

        public ArmorEquipListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            //No longer applying potion effects here, but it is still responsible for identifying if the item has lore and has potion effects.
        }
    }

    public class PotionEffectHelper {

        private static final Map<String, ChatColor> effectColors = new HashMap<>();

        static {
            effectColors.put("STRENGTH", ChatColor.RED);
            effectColors.put("NIGHT_VISION", ChatColor.DARK_PURPLE);
            effectColors.put("SPEED", ChatColor.GREEN);
            effectColors.put("RESISTANCE", ChatColor.DARK_GRAY);
            effectColors.put("REGENERATION", ChatColor.LIGHT_PURPLE);
            effectColors.put("FIRE_RESISTANCE", ChatColor.GOLD);
            // Add more potion effects and their corresponding colors here
        }

        public static String generatePotionLore(String potionEffectName, int magnitude) {
            ChatColor color = effectColors.getOrDefault(potionEffectName, ChatColor.WHITE);
            return color + potionEffectName.substring(0, 1).toUpperCase() + potionEffectName.substring(1).toLowerCase() + " " + toRoman(magnitude + 1);
        }

        public static PotionEffectData parsePotionEffect(String loreLine) {
            for (Map.Entry<String, ChatColor> entry : effectColors.entrySet()) {
                String effectName = entry.getKey();
                ChatColor color = entry.getValue();
                if (loreLine.startsWith(color + effectName.substring(0, 1).toUpperCase() + effectName.substring(1).toLowerCase())) {
                    try {
                        String romanNumeral = loreLine.split(" ")[1];
                        int magnitude = fromRoman(romanNumeral) - 1;
                        PotionEffectType effectType = PotionEffectType.getByName(effectName);
                        return new PotionEffectData(effectType, magnitude);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            return null;
        }

        public static String toRoman(int num) {
            String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
            int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
            StringBuilder roman = new StringBuilder();

            for (int i = 0; i < romanValues.length; i++) {
                while (num >= romanValues[i]) {
                    num -= romanValues[i];
                    roman.append(romanLiterals[i]);
                }
            }
            return roman.toString();
        }

        public static int fromRoman(String romanNumeral) {
            Map<Character, Integer> romanValues = new HashMap<>();
            romanValues.put('I', 1);
            romanValues.put('V', 5);
            romanValues.put('X', 10);
            romanValues.put('L', 50);
            romanValues.put('C', 100);
            romanValues.put('D', 500);
            romanValues.put('M', 1000);

            int result = 0;
            int prevValue = 0;

            for (int i = romanNumeral.length() - 1; i >= 0; i--) {
                int currValue = romanValues.get(romanNumeral.charAt(i));
                if (currValue < prevValue) {
                    result -= currValue;
                } else {
                    result += currValue;
                }
                prevValue = currValue;
            }
            return result;
        }
    }

    public class PotionEffectData {
        private final PotionEffectType effectType;
        private final int magnitude;

        public PotionEffectData(PotionEffectType effectType, int magnitude) {
            this.effectType = effectType;
            this.magnitude = magnitude;
        }

        public PotionEffectType getEffectType() {
            return effectType;
        }

        public int getMagnitude() {
            return magnitude;
        }
    }

    public class PotionEffectTask extends BukkitRunnable {

        private final Plugin plugin;

        public PotionEffectTask(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            // Iterate through all online players
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                // Apply potion effects based on equipped armor
                applyPotionEffects(player);
            }
        }

        private void applyPotionEffects(Player player) {
            PlayerInventory inventory = player.getInventory();
            ItemStack[] armorContents = inventory.getArmorContents();

            // Iterate through each piece of armor
            for (ItemStack armor : armorContents) {
                if (armor != null && armor.hasItemMeta() && armor.getItemMeta().hasLore()) {
                    ItemMeta meta = armor.getItemMeta();
                    List<String> lore = meta.getLore();

                    if (lore != null) {
                        for (String loreLine : lore) {
                            PotionEffectData effectData = PotionEffectHelper.parsePotionEffect(loreLine);
                            if (effectData != null) {
                                PotionEffectType effectType = effectData.getEffectType();
                                int magnitude = effectData.getMagnitude();
                                int duration = 20 * 30; // 30 seconds (20 ticks per second)

                                if (effectType != null) {
                                    player.addPotionEffect(new PotionEffect(effectType, duration, magnitude), true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
