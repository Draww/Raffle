package com.drawwdev.raffle.utils;

import com.drawwdev.raffle.Main;

public class Logger {

    private Main plugin;

    private java.util.logging.Logger logger;

    public Logger(Main plugin){
        this.plugin = plugin;
        logger = java.util.logging.Logger.getLogger("Minecraft");
    }

    public Main getPlugin() {
        return plugin;
    }

    public java.util.logging.Logger getLogger() {
        return logger;
    }
}
