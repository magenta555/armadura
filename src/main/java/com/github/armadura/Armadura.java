package com.github.armadura;

import org.bukkit.plugin.java.JavaPlugin;

public final class Armadura extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Armadura plugin enabled!");

        getCommand("armor").setExecutor(new ArmorCommand());

        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Armadura plugin disabled!");
    }
}
