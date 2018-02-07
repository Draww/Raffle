package com.drawwdev.raffle;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class RaffleStorage {

    private Main plugin;

    private HashMap<RaffleType, Map.Entry<RaffleConsumer, RafflePredicate>> oraffle;

    public RaffleStorage(Main plugin){
        this.plugin = plugin;
        oraffle = new HashMap<>();
    }

    public RaffleConsumer getConsumer(RaffleType raffleType){
        if (getOraffle().getOrDefault(raffleType, null) == null) return null;
        return getOraffle().getOrDefault(raffleType, null).getKey();
    }

    public RafflePredicate getPredicate(RaffleType raffleType){
        if (getOraffle().getOrDefault(raffleType, null) == null) return null;
        return getOraffle().getOrDefault(raffleType, null).getValue();
    }

    public void create(RaffleType raffleType, RaffleConsumer raffleConsumer, RafflePredicate rafflePredicate){
        if (!getOraffle().containsKey(raffleType)){
            getOraffle().put(raffleType, new AbstractMap.SimpleEntry<>(raffleConsumer, rafflePredicate));
        }
    }

    public RaffleBuilder newBuilder(RaffleType raffleType){
        return new RaffleBuilder(this, raffleType);
    }

    public RaffleMultipleBuilder newBuilder(RaffleType... raffleType){
        return new RaffleMultipleBuilder(this, raffleType);
    }



    public Main getPlugin() {
        return plugin;
    }

    public HashMap<RaffleType, Map.Entry<RaffleConsumer, RafflePredicate>> getOraffle() {
        return oraffle;
    }
}
