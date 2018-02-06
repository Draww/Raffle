package com.drawwdev.raffle;

import com.drawwdev.raffle.depend.EconomyDepend;
import com.drawwdev.raffle.depend.PermissionsExDepend;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Main instance;
    private EconomyDepend economyDepend;
    private PermissionsExDepend permissionsExDepend;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        economyDepend = new EconomyDepend(this);
        permissionsExDepend = new PermissionsExDepend(this);
        new RaffleCommand(this);
    }

    @Override
    public void onDisable() {

    }

    public Main getInstance() {
        return instance;
    }

    public EconomyDepend getEconomyDepend() {
        return economyDepend;
    }

    public PermissionsExDepend getPermissionsExDepend() {
        return permissionsExDepend;
    }
}
