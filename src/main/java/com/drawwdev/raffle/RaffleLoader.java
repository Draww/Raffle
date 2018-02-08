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
            if (plugin.getEconomyDepend().dependent()) {
                raffleStorage.newBuilder("LEVEL")
                        .setTime(5)
                        .setDatatype("Numeral")
                        .setConsumer((player, raffleData) -> {
                            Double money = Double.parseDouble(String.valueOf(raffleData.get(0)));
                            plugin.getEconomyDepend().get().depositPlayer(player, money);
                            Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a$" + money + " &7was given"));
                        })
                        .setPredicate((player, raffleData) -> {
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
            }
        } catch (RaffleException e) {
            plugin.getLog().getLogger().log(Level.SEVERE, e.getMessage());
        }
        return raffleStorage;
    }

}
