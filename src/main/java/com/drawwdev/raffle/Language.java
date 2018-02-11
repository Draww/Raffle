package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.Config;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.regex.Pattern;

public class Language {

    private Main plugin;
    private Config config;

    private YamlConfiguration defYaml;

    public Language(Main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        try {
            InputStreamReader defaultConfigStream = new InputStreamReader(plugin.getResource("language.yml"), "UTF8"); /* Get the config.yml from jar */
            defYaml = new YamlConfiguration().loadConfiguration(defaultConfigStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        config.load();
        try {
            InputStreamReader defaultConfigStream = new InputStreamReader(plugin.getResource("language.yml"), "UTF8"); /* Get the config.yml from jar */
            defYaml = new YamlConfiguration().loadConfiguration(defaultConfigStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String tl(final String path, final Object... objects){
        return format(path, objects);
    }

    private String translate(final String path) {
        if (getConfig().getConfig().isString(path)){
            return getConfig().getConfig().getString(path);
        } else {
            return defYaml.getString(path);
        }
    }

    public String format(final String path, final Object... objects){
        String format = translate(path);
        format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
        MessageFormat messageFormat = new MessageFormat(format);
        return messageFormat.format(objects);
    }


    public Main getPlugin() {
        return plugin;
    }

    public Config getConfig() {
        return config;
    }

    public String getFormat(String l, Object... format){
        String get = getConfig().getConfig().getString(l);
        get = String.format(get, format);
        return get;
    }

    public String get(String l){
        return getConfig().getConfig().getString(l);
    }
}
