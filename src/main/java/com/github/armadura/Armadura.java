package com.github.armadura;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Armadura extends JavaPlugin {

    private ArmorListener armorListener;

    @Override
    public void onEnable() {
        getCommand("armor").setExecutor(new ArmorCommand());

        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    armorListener.applyArmorEffects(player);
                }
            }
        }.runTaskTimer(this, 0L, 20);

    }

    @Override
    public void onDisable() {
    }
}
