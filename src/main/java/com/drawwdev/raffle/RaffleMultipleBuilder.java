package com.drawwdev.raffle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaffleMultipleBuilder {

    private RaffleStorage raffle = null;
    private List<RaffleType> raffleTypes = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;

    public RaffleMultipleBuilder(RaffleStorage raffle, RaffleType... raffleType){
        this.raffle = raffle;
        this.raffleTypes = new ArrayList<>();
        raffleTypes.addAll(Arrays.asList(raffleType));
    }

    public RaffleMultipleBuilder setRaffleMain(RaffleStorage raffleMain){
        this.raffle = raffleMain;
        return this;
    }

    public RaffleMultipleBuilder addType(RaffleType raffleType){
        if (!raffleTypes.contains(raffleType)){
            raffleTypes.add(raffleType);
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

    public void build() throws RaffleException{
        if (raffle != null && raffleTypes != null && raffleConsumer != null && rafflePredicate != null){
            for (RaffleType rT : raffleTypes){
                raffle.create(rT, raffleConsumer, rafflePredicate);
            }
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
