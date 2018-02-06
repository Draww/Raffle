package com.drawwdev.raffle.depend;

import com.drawwdev.raffle.Main;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyDepend implements Depend {

    private Main plugin;
    private Economy econ;
    private Boolean dependent = false;

    public EconomyDepend(Main plugin) {
        this.plugin = plugin;
        setup();
    }

    @Override
    public Economy get(){
        return econ;
    }

    @Override
    public Boolean dependent() {
        return dependent;
    }

    @Override
    public boolean setup() {
        if ((plugin.getServer().getPluginManager().getPlugin("Vault") instanceof Vault)) {
            RegisteredServiceProvider<Economy> service = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (service != null) {
                this.econ = ((Economy) service.getProvider());
                dependent = econ != null;
                return econ != null;
            }
        }
        dependent = false;
        return false;
    }
}
