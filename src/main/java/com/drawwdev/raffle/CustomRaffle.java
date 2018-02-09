package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.Config;
import com.drawwdev.raffle.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomRaffle extends Raffle {

    private Config config;
    private List<String> actions = new ArrayList<>();
    private List<String> predicate = new ArrayList<>();
    private String name;

    public CustomRaffle(String id) {
        super(Main.getInstance().getConfigs().get("custom").getConfig().getInt("CustomRaffles" + id + ".time"), null, null, Main.getInstance().getConfigs().get("custom").getConfig().getString("CustomRaffles" + id + ".datatype", ""), RaffleType.CUSTOM);
        config = Main.getInstance().getConfigs().get("custom");
        actions = config.getConfig().getStringList("CustomRaffles" + id + ".actions");
        predicate = config.getConfig().getStringList("CustomRaffles" + id + ".predicate");
        name = config.getConfig().getString("CustomRaffles" + id + ".name");
        setConsumer((player, raffleData, fromPlayer) -> Main.getInstance().getScriptSystem().executeActions(player, getActions(), raffleData));
        setPredicate((player, raffleData) -> {
            Boolean r = false;
            for (String a : getPre()) {
                if (!Main.getInstance().getScriptSystem().runCondition(player, a, raffleData)) {
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

    public String getName() {
        return name;
    }
}
/*Iterator<String> run = getPre().iterator();
            if (run.hasNext()) {
                String execute = run.next();
                if (execute.startsWith("#Script#")) {
                    if ((execute.startsWith("#Script#[IF]")) || (execute.startsWith("#Script# [IF]")) || (execute.startsWith("#Script#[ELSE IF]")) || (execute.startsWith("#Script# [ELSE IF]"))) {
                        if (Main.getInstance().getScriptSystem().runCondition(player, execute, raffleData)) {
                            List<String> newList = new ArrayList<>();
                            Boolean add = true;

                            Boolean firstadded = false;
                            for (String s : getPre()) {
                                if ((!s.equalsIgnoreCase(execute)) || (firstadded)) {
                                    firstadded = true;
                                    if ((s.startsWith("#Script#[ELSE IF]")) || (s.startsWith("#Script# [ELSE IF]")) || (s.startsWith("#Script#[ELSE]")) || (s.startsWith("#Script# [ELSE]"))) {
                                        add = false;
                                    }
                                    if (add) {
                                        newList.add(s);
                                    }
                                }
                            }
                            Bukkit.broadcastMessage("a1");
                            Main.getInstance().getScriptSystem().executeActions(player, newList, raffleData);
                            return true;
                        }
                        List<String> newlist = new ArrayList<>();

                        boolean add = false;
                        boolean firstadded = false;
                        for (String s : getPre()) {
                            if ((s.equalsIgnoreCase(execute)) && (!firstadded)) {
                                add = false;
                            }
                            firstadded = true;
                            if (((!s.equalsIgnoreCase(execute)) && (s.startsWith("#Script#[ELSE IF]")) || (s.startsWith("#Script# [ELSE IF]"))) ||
                                    ((!s.equalsIgnoreCase(execute)) && (s.startsWith("#Script#[IF]")) || (s.startsWith("#Script# [IF]"))) ||
                                    (s.startsWith("#Script#[ELSE]")) || (s.startsWith("#Script# [ELSE]"))) {
                                add = true;
                            }
                            if (add) {
                                newlist.add(s);
                            }
                        }
                        if (!newlist.isEmpty()) {
                            Bukkit.broadcastMessage("a2");
                            Main.getInstance().getScriptSystem().executeActions(player, newlist, raffleData);
                        }
                    }
                    if (!Main.getInstance().getScriptSystem().runCondition(player, execute, raffleData)) {
                        Bukkit.broadcastMessage("a3");
                        String tocheck = null;
                        for (String s : getPre()) {
                            Bukkit.broadcastMessage("a3 - " + s);
                            if ((s.startsWith("#Script#[IF]")) || (s.startsWith("#Script# [IF]")) || (s.startsWith("#Script#[ELSE IF]")) || (s.startsWith("#Script# [ELSE IF]"))) {
                                Bukkit.broadcastMessage("a3 - a1");
                                tocheck = s;
                                Bukkit.broadcastMessage("a3 - a1 - " + tocheck);
                                break;
                            }
                        }
                        Bukkit.broadcastMessage("a3 - a2");
                        if (tocheck != null) {
                            Bukkit.broadcastMessage("a3 - a3");
                            if (Main.getInstance().getScriptSystem().runCondition(player, tocheck, raffleData)) {
                                Bukkit.broadcastMessage("a4");
                                return true;
                            }
                        }
                    }
                    if ((execute.startsWith("#Script#[ELSE]")) || (execute.startsWith("#Script# [ELSE]")))
                    {
                        List<String> newlist = new ArrayList<>();

                        boolean add = false;
                        for (String s : getPre())
                        {
                            if (add) {
                                newlist.add(s);
                            }
                            if ((s.equalsIgnoreCase("#Script#[ELSE]")) || (s.equalsIgnoreCase("#Script# [ELSE]"))) {
                                add = true;
                            }
                        }
                        Main.getInstance().getScriptSystem().executeActions(player, newlist, raffleData);
                        Bukkit.broadcastMessage("a5");
                        return false;
                    }
                }
            }
            return true;*/