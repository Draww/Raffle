package com.drawwdev.raffle;

public class RaffleBuilder {

    private RaffleStorage raffle = null;
    private RaffleType raffleType = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;

    public RaffleBuilder(RaffleStorage raffle, RaffleType raffleType){
        this.raffle = raffle;
        this.raffleType = raffleType;
    }

    public RaffleBuilder setRaffleMain(RaffleStorage raffleMain){
        this.raffle = raffleMain;
        return this;
    }

    public RaffleBuilder setType(RaffleType raffleType){
        this.raffleType = raffleType;
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

    public void build() throws RaffleException{
        if (raffle != null && raffleType != null && raffleConsumer != null && rafflePredicate != null){
            raffle.create(raffleType, raffleConsumer, rafflePredicate);
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
