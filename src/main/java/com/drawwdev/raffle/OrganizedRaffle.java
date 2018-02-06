package com.drawwdev.raffle;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class OrganizedRaffle {

    private Main plugin;

    private HashMap<RaffleType, RaffleRunnable> organizedRaffles = new HashMap<>();

    public OrganizedRaffle(Main plugin){
        this.plugin = plugin;
    }

    public void create(RaffleType raffleType, RaffleRunnable raffleRunnable){
        getOrganizedRaffles().put(raffleType, raffleRunnable);
    }

    public RaffleRunnable get(RaffleType raffleType){
        return getOrganizedRaffles().getOrDefault(raffleType, null);
    }

    public Main getPlugin() {
        return plugin;
    }

    public HashMap<RaffleType, RaffleRunnable> getOrganizedRaffles() {
        return organizedRaffles;
    }
}
