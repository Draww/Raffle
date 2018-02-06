package com.drawwdev.raffle.depend;

import com.drawwdev.raffle.Main;
import net.milkbowl.vault.economy.Economy;
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
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            dependent = false;
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            dependent = false;
            return false;
        }
        econ = rsp.getProvider();
        dependent = econ != null;
        return econ != null;
    }
}
