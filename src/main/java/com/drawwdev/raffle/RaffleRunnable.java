package com.drawwdev.raffle;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RaffleRunnable {
    public abstract void run(Player player, RaffleData raffleData);
}
