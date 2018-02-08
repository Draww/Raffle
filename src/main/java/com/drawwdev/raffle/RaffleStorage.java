package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.StringUtil;
import javafx.util.Pair;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class RaffleStorage {

    private Main plugin;

    private HashMap<String, Raffle> oraffle;

    public RaffleStorage(Main plugin){
        this.plugin = plugin;
        oraffle = new HashMap<>();
    }

    public RaffleConsumer getConsumer(String raffleName){
        if (getOraffle().getOrDefault(raffleName, null) == null) return null;
        return getOraffle().getOrDefault(raffleName, null).getConsumer();
    }

    public RafflePredicate getPredicate(String raffleName){
        if (getOraffle().getOrDefault(raffleName, null) == null) return null;
        return getOraffle().getOrDefault(raffleName, null).getPredicate();
    }

    public Integer getTime(String raffleName){
        if (getOraffle().getOrDefault(raffleName, null) == null) return null;
        return getOraffle().getOrDefault(raffleName, null).getTime();
    }

    public String getDatatype(String raffleName){
        if (getOraffle().getOrDefault(raffleName, null) == null) return null;
        return getOraffle().getOrDefault(raffleName, null).getDatatype();
    }

    public RaffleType getType(String raffleName){
        if (getOraffle().getOrDefault(raffleName, null) == null) return null;
        return getOraffle().getOrDefault(raffleName, null).getRaffleType();
    }

    public void create(String raffleName, RaffleConsumer raffleConsumer, RafflePredicate rafflePredicate, Integer time, String datatype, RaffleType raffleType){
        if (!getOraffle().containsKey(raffleName)){
            getOraffle().put(raffleName, new Raffle(time, raffleConsumer, rafflePredicate, datatype, raffleType));
        }
    }

    public RaffleBuilder newBuilder(String raffleName){
        return new RaffleBuilder(this, raffleName);
    }

    public RaffleMultipleBuilder newBuilder(String... raffleName){
        return new RaffleMultipleBuilder(this, raffleName);
    }

    public List<String> getAllKey(){
        return new ArrayList<>(getOraffle().keySet());
    }

    public Main getPlugin() {
        return plugin;
    }

    public HashMap<String, Raffle> getOraffle() {
        return oraffle;
    }
}
