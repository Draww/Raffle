package com.drawwdev.raffle;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RaffleConsumer {
    public abstract void run(Player player, RaffleData raffleData, Player fromPlayer);
}
