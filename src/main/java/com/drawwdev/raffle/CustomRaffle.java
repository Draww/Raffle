package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CustomRaffle extends Raffle {

    private Config config;
    private List<String> actions = new ArrayList<>();
    private List<String> predicate = new ArrayList<>();

    public CustomRaffle(String id) {
        super(Main.getInstance().getConfigs().get("custom").getConfig().getInt(id + ".time"), null, null, Main.getInstance().getConfigs().get("custom").getConfig().getString(id + ".datatype", ""));
        config = Main.getInstance().getConfigs().get("custom");
        actions = config.getConfig().getStringList(id + ".actions");
        predicate = config.getConfig().getStringList(id + ".predicate");
        setConsumer((player, raffleData) -> Main.getInstance().getScriptSystem().executeActions(player, getActions()));
        setPredicate((player, raffleData) -> {
            Boolean r = false;
            for (String a : getPre()) {
                if (!Main.getInstance().getScriptSystem().runCondition(player, a)) {
                    return false;
                } else {
                    r = true;
                }
            }
            return r;
        });
    }

    public Config getConfig() {
        return config;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getPre() {
        return predicate;
    }
}
