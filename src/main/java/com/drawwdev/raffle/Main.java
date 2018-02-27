package com.drawwdev.raffle;

import com.drawwdev.raffle.calendar.CListener;
import com.drawwdev.raffle.calendar.CManager;
import com.drawwdev.raffle.calendar.CTimer;
import com.drawwdev.raffle.depend.*;
import com.drawwdev.raffle.nms.*;
import com.drawwdev.raffle.utils.Config;
import com.drawwdev.raffle.utils.ScriptSystem;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main instance;
    private Configs configs;
    private CompatabilityManager compatabilityManager;
    private ScriptSystem scriptSystem;
    private Language language;
    private Depends depends;

    private CManager cManager;
    private CTimer cTimer;

    private RaffleCommand raffleCommand;

    @Override
    public void onEnable() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(46) + 1).trim();
        compatabilityManager = setupCompatabilityNMS(version);
        if (compatabilityManager == null) {
            getLogger().severe(version + " is not supported!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        scriptSystem = new ScriptSystem(this);
        instance = this;
        saveDefaultConfig();
        configs = new Configs(this);
        loadConfigs();
        language = new Language(this, getConfigs().get("language"));
        depends = new Depends(this);
        raffleCommand = new RaffleCommand(this);
        cManager = new CManager(this);
        cTimer = new CTimer(this);
        new CListener(this);
        setupMetrics();
    }

    @Override
    public void onDisable() {

    }

    private void setupMetrics() {
        Metrics m = new Metrics(this);
    }

    public void loadConfigs() {
        getConfigs().add("custom", new Config(this, "custom.yml", true));
        getConfigs().add("language", new Config(this, "language.yml", true));
        getConfigs().add("calendar", new Config(this, "calendar.yml", true));
    }

    private CompatabilityManager setupCompatabilityNMS(String version) {
        if (version.equals("v1_12_R1")) {
            return new Compatability_1_12_R1();
        } else if (version.equals("v1_11_R1")) {
            return new Compatability_1_11_R1();
        } else if (version.equals("v1_10_R1")) {
            return new Compatability_1_10_R1();
        } else if (version.equals("v1_9_R1")) {
            return new Compatability_1_9_R1();
        } else if (version.equals("v1_9_R2")) {
            return new Compatability_1_9_R2();
        } else if (version.equals("v1_8_R1")) {
            return new Compatability_1_8_R1();
        } else if (version.equals("v1_8_R2")) {
            return new Compatability_1_8_R2();
        } else if (version.equals("v1_8_R3")) {
            return new Compatability_1_8_R3();
        } else {
            return null;
        }
    }

    public void reloadCalendars(){
        if (cTimer != null) cTimer.cancel();
        cManager.reload();
        getNewTimer();
    }

    public void getNewTimer() {
        cTimer = new CTimer(this);
    }

    public CManager getCManager() {
        return cManager;
    }

    public static Main getInstance() {
        return instance;
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

    public Language getLanguage() {
        return language;
    }

    public Depends getDepends() {
        return depends;
    }

    public RaffleCommand getRaffleCommand() {
        return raffleCommand;
    }
}
