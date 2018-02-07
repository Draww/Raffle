package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.StringUtil;
import org.bukkit.Bukkit;

public class RaffleBuilder {

    private RaffleStorage raffle = null;
    private String raffleName = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;
    private Integer time = null;
    private String datatype = "";

    public RaffleBuilder(RaffleStorage raffle, String raffleName){
        this.raffle = raffle;
        this.raffleName = raffleName;
    }

    public RaffleBuilder setRaffleMain(RaffleStorage raffleMain){
        this.raffle = raffleMain;
        return this;
    }

    public RaffleBuilder setName(String raffleName){
        this.raffleName = raffleName;
        return this;
    }

    public RaffleBuilder setConsumer(RaffleConsumer raffleConsumer){
        this.raffleConsumer = raffleConsumer;
        return this;
    }

    public RaffleBuilder setPredicate(RafflePredicate rafflePredicate) {
        this.rafflePredicate = rafflePredicate;
        return this;
    }

    public RaffleBuilder setTime(Integer time){
        this.time = time;
        return this;
    }

    public RaffleBuilder setDatatype(String datatype) {
        this.datatype = datatype;
        return this;
    }

    public void build() throws RaffleException{
        if (raffle != null && raffleName != null && raffleConsumer != null && rafflePredicate != null && time != null){
            raffle.create(raffleName, raffleConsumer, rafflePredicate, time, datatype);
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
