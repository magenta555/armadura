package com.github.armadura;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.potion.PotionEffectType;

public class ArmorCommand implements CommandExecutor, TabCompleter {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /armor [potion effect] [magnitude]");
            return true;
        }

        String potionEffectName = args[0].toUpperCase();
        int magnitude;

        try {
            magnitude = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid magnitude. Must be a number.");
            return true;
        }
        PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffectName);

        if (potionEffectType == null) {
            player.sendMessage(ChatColor.RED + "Invalid potion effect.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an armor piece.");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        String loreText = ChatColor.DARK_PURPLE + potionEffectName + " " + toRoman(magnitude);
        lore.add(loreText);
        meta.setLore(lore);
        item.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "Potion effect added to the armor!");
        return true;
    }

    private String toRoman(int num) {
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

    @SuppressWarnings("deprecation")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (PotionEffectType type : PotionEffectType.values()) {
                if (type != null && type.getName().toUpperCase().startsWith(args[0].toUpperCase())) {
                    completions.add(type.getName());
                }
            }
            return completions;
        } else {
            return null;
        }
    }
}
