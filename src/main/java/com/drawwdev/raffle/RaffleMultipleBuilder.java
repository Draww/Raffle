package com.drawwdev.raffle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaffleMultipleBuilder {

    private RaffleStorage raffle = null;
    private List<String> raffleNames = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;
    private Integer time = null;
    private String datatype = "";

    public RaffleMultipleBuilder(RaffleStorage raffle, String... raffleName){
        this.raffle = raffle;
        this.raffleNames = new ArrayList<>();
        raffleNames.addAll(Arrays.asList(raffleName));
    }

    public RaffleMultipleBuilder setRaffleMain(RaffleStorage raffleMain){
        this.raffle = raffleMain;
        return this;
    }

    public RaffleMultipleBuilder addName(String raffleName){
        if (!raffleNames.contains(raffleName)){
            raffleNames.add(raffleName);
        }
        return this;
    }

    public RaffleMultipleBuilder setConsumer(RaffleConsumer raffleConsumer){
        this.raffleConsumer = raffleConsumer;
        return this;
    }

    public RaffleMultipleBuilder setPredicate(RafflePredicate rafflePredicate) {
        this.rafflePredicate = rafflePredicate;
        return this;
    }

    public RaffleMultipleBuilder setTime(Integer time){
        this.time = time;
        return this;
    }

    public RaffleMultipleBuilder setDatatype(String datatype) {
        this.datatype = datatype;
        return this;
    }

    public void build() throws RaffleException{
        if (raffle != null && raffleNames != null && raffleConsumer != null && rafflePredicate != null && time != null){
            for (String rT : raffleNames){
                raffle.create(rT, raffleConsumer, rafflePredicate, time, datatype);
            }
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
