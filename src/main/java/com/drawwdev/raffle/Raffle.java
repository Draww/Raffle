package com.drawwdev.raffle;

public class Raffle {

    private Integer time;
    private RaffleConsumer consumer;
    private RafflePredicate predicate;
    private String datatype;
    private RaffleType raffleType;

    public Raffle(){

    }

    public Raffle(Integer time, RaffleConsumer consumer, RafflePredicate predicate, String datatype, RaffleType raffleType){
        this.time = time;
        this.consumer = consumer;
        this.predicate = predicate;
        this.datatype = datatype;
        this.raffleType = raffleType;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public RaffleConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(RaffleConsumer consumer) {
        this.consumer = consumer;
    }

    public RafflePredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(RafflePredicate predicate) {
        this.predicate = predicate;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public RaffleType getRaffleType() {
        return raffleType;
    }

    public void setRaffleType(RaffleType raffleType) {
        this.raffleType = raffleType;
    }
}
