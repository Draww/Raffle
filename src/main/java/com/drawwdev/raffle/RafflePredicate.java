package com.drawwdev.raffle;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface RafflePredicate {

    boolean check(Player player, RaffleData raffleData);

}
