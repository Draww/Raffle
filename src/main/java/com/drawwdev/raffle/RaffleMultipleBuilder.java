package com.drawwdev.raffle;

import com.drawwdev.raffle.depend.Depend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RaffleMultipleBuilder {

    private RaffleStorage raffle = null;
    private List<String> raffleNames = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;
    private List<String> disabledGroups;
    private Integer time = null;
    private String datatype = "";
    private RaffleType raffleType;
    private HashMap<Class, Depend> depends = new HashMap<>();

    public RaffleMultipleBuilder(RaffleStorage raffle, String... raffleName){
        this.raffle = raffle;
        this.raffleNames = new ArrayList<>();
        for (String rN : raffleName){
            raffleNames.add(rN.toUpperCase());
        }
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

    public RaffleMultipleBuilder setType(RaffleType raffleType) {
        this.raffleType = raffleType;
        return this;
    }

    public RaffleMultipleBuilder setDisabledGroups(List<String> list){
        this.disabledGroups = list;
        return this;
    }

    public RaffleMultipleBuilder deleteDisabledGroups(List<String> list){
        this.disabledGroups.removeAll(list);
        return this;
    }

    public RaffleMultipleBuilder addDisabledGroups(String string){
        this.disabledGroups.add(string);
        return this;
    }

    public RaffleMultipleBuilder removeDisabledGroups(String string){
        this.disabledGroups.remove(string);
        return this;
    }

    public RaffleMultipleBuilder addDepend(Depend depend) {
        if (!depends.containsKey(depend.getClass())){
            this.depends.put(depend.getClass(), depend);
        }
        return this;
    }

    public RaffleMultipleBuilder removeDepend(Depend depend) {
        if (depends.containsKey(depend.getClass())){
            this.depends.remove(depend.getClass());
        }
        return this;
    }

    public void build() throws RaffleException{
        if (raffle != null && raffleNames != null && raffleConsumer != null && rafflePredicate != null && time != null && raffleType != null){
            if (!depends.isEmpty()) {
                for (Depend depend : depends.values()) {
                    if (!depend.dependent()){
                        throw new RaffleException(depend.name() + " is not dependent!");
                    }
                }
            }
            for (String rT : raffleNames){
                raffle.create(rT, raffleConsumer, rafflePredicate, disabledGroups, time, datatype, raffleType);
            }
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
