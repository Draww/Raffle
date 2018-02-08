package com.drawwdev.raffle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.drawwdev.raffle.utils.StringUtil.cc;

public class RaffleLoader {

    private Main plugin;
    private RaffleStorage raffleStorage;

    public RaffleLoader(Main plugin, RaffleStorage raffleStorage) {
        this.plugin = plugin;
        this.raffleStorage = raffleStorage;
    }

    public RaffleStorage load() {
        try {
            raffleStorage.newBuilder("MONEY")
                    .setTime(5)
                    .setDatatype("Numeral")
                    .addDepend(plugin.getEconomyDepend())
                    .setConsumer((player, raffleData, fromPlayer) -> {
                        Double money = Double.parseDouble(String.valueOf(raffleData.get(0)));
                        plugin.getEconomyDepend().get().depositPlayer(player, money);
                        Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a$" + money + " &7was given"));
                    })
                    .setPredicate((fromPlayer, raffleData) -> {
                        if (raffleData.size() < 1) {
                            return false;
                        }
                        try {
                            Double parseDouble = Double.parseDouble(raffleData.get(0).toString());
                        } catch (NumberFormatException error) {
                            return false;
                        }
                        return true;
                    }).build();
            raffleStorage.newBuilder("EXP")
                    .setTime(5)
                    .setDatatype("Numeral")
                    .setConsumer((player, raffleData, fromPlayer) -> {
                        Integer EXP = Integer.parseInt(String.valueOf(raffleData.get(0)));
                        player.giveExp(EXP);
                        Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a" + EXP + " Exp &7was given"));
                    })
                    .setPredicate((fromPlayer, raffleData) -> {
                        if (raffleData.size() < 1) {
                            return false;
                        }
                        try {
                            Integer EXP = Integer.parseInt(String.valueOf(raffleData.get(0)));
                        } catch (NumberFormatException error) {
                            return false;
                        }
                        return true;
                    }).build();
            raffleStorage.newBuilder("LEVEL")
                    .setTime(5)
                    .setDatatype("Numeral")
                    .setConsumer((player, raffleData, fromPlayer) -> {
                        Integer LEVEL = Integer.parseInt(String.valueOf(raffleData.get(0)));
                        player.giveExpLevels(LEVEL);
                        Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a" + LEVEL + " Level &7was given"));
                    })
                    .setPredicate((fromPlayer, raffleData) -> {
                        if (raffleData.size() < 1) {
                            return false;
                        }
                        try {
                            Integer LEVEL = Integer.parseInt(String.valueOf(raffleData.get(0)));
                        } catch (NumberFormatException error) {
                            return false;
                        }
                        return true;
                    }).build();
        } catch (RaffleException e) {
            plugin.getLog().getLogger().log(Level.SEVERE, e.getMessage());
        }
        loadCustomRaffle();
        return raffleStorage;
    }

    public void loadCustomRaffle(){
        if (plugin.getConfigs().has("custom")){
            for (String custom : plugin.getConfigs().get("custom").getConfigurationSection("").getKeys(false)){
                if (raffleStorage.getAllKey().stream().noneMatch(p -> p.equals(custom))){
                    CustomRaffle customRaffle = new CustomRaffle(custom);
                    try {
                        raffleStorage.newBuilder(customRaffle.getName())
                                .setType(RaffleType.CUSTOM)
                                .setTime(customRaffle.getTime())
                                .setDatatype(customRaffle.getDatatype())
                                .setPredicate(customRaffle.getPredicate())
                                .setConsumer(customRaffle.getConsumer())
                                .build();
                    } catch (RaffleException e) {
                        Bukkit.getConsoleSender().sendMessage(cc(plugin.getConfig().getString("prefix") + e.getMessage()));
                    }
                }
            }
        }
    }

}
