package com.drawwdev.raffle;

import com.drawwdev.raffle.depend.DependType;
import com.drawwdev.raffle.depend.EconomyDepend;
import com.drawwdev.raffle.depend.PermissionsExDepend;
import com.drawwdev.raffle.nms.CompatabilityManager;
import com.drawwdev.raffle.nms.Compatability_1_12_R1;
import com.drawwdev.raffle.utils.Config;
import com.drawwdev.raffle.utils.Logger;
import com.drawwdev.raffle.utils.ScriptSystem;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main instance;
    private EconomyDepend economyDepend;
    private PermissionsExDepend permissionsExDepend;
    private Logger logger;
    private Configs configs;
    private CompatabilityManager compatabilityManager;
    private ScriptSystem scriptSystem;

    @Override
    public void onEnable() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(46) + 1).trim();
        compatabilityManager = setupCompatabilityNMS(version);
        if (compatabilityManager == null) {
            //this.getServer().getPluginManager().disablePlugin(this);
            //return;
        }
        scriptSystem = new ScriptSystem(this);
        logger = new Logger(this);
        instance = this;
        saveDefaultConfig();
        configs = new Configs(this);
        loadConfigs();
        economyDepend = new EconomyDepend(this, DependType.NORMAL);
        permissionsExDepend = new PermissionsExDepend(this, DependType.NORMAL);
        new RaffleCommand(this);
    }

    @Override
    public void onDisable() {

    }

    public void loadConfigs(){
        getConfigs().add("custom", new Config(this, "custom.yml", true));
    }

    private CompatabilityManager setupCompatabilityNMS(String version) {
        if (version.equals("v1_12_R1")) {
            return new Compatability_1_12_R1();
        } else {
            return null;
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public EconomyDepend getEconomyDepend() {
        return economyDepend;
    }

    public PermissionsExDepend getPermissionsExDepend() {
        return permissionsExDepend;
    }

    public Logger getLog() {
        return logger;
    }

    public Configs getConfigs() {
        return configs;
    }

    public CompatabilityManager getCompatabilityManager() {
        return compatabilityManager;
    }

    public ScriptSystem getScriptSystem() {
        return scriptSystem;
    }
}
