package com.drawwdev.raffle;

import com.drawwdev.raffle.depend.Depend;
import com.drawwdev.raffle.utils.StringUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RaffleBuilder {

    private RaffleStorage raffle = null;
    private String raffleName = null;
    private RaffleConsumer raffleConsumer = null;
    private RafflePredicate rafflePredicate = null;
    private List<String> disabledGroups;
    private Integer time = null;
    private String datatype = "";
    private RaffleType raffleType;
    private HashMap<Class, Depend> depends = new HashMap<>();

    public RaffleBuilder(RaffleStorage raffle, String raffleName){
        this.raffle = raffle;
        this.raffleName = raffleName;
        this.raffleType = RaffleType.NORMAL;
        this.disabledGroups = new ArrayList<>();
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

    public RaffleBuilder setType(RaffleType raffleType) {
        this.raffleType = raffleType;
        return this;
    }

    public RaffleBuilder setDisabledGroups(List<String> list){
        this.disabledGroups = list;
        return this;
    }

    public RaffleBuilder deleteDisabledGroups(List<String> list){
        this.disabledGroups.removeAll(list);
        return this;
    }

    public RaffleBuilder addDisabledGroups(String string){
        this.disabledGroups.add(string);
        return this;
    }

    public RaffleBuilder removeDisabledGroups(String string){
        this.disabledGroups.remove(string);
        return this;
    }

    public RaffleBuilder addDepend(Depend depend) {
        if (!depends.containsKey(depend.getClass())){
            this.depends.put(depend.getClass(), depend);
        }
        return this;
    }

    public RaffleBuilder removeDepend(Depend depend) {
        if (depends.containsKey(depend.getClass())){
            this.depends.remove(depend.getClass());
        }
        return this;
    }

    public void build() throws RaffleException{
        if (raffle != null && raffleName != null && raffleConsumer != null && rafflePredicate != null && time != null && raffleType != null){
            if (!depends.isEmpty()) {
                for (Depend depend : depends.values()) {
                    if (!depend.dependent()){
                        throw new RaffleException(depend.name() + " is not dependent!");
                    }
                }
            }
            raffle.create(raffleName, raffleConsumer, rafflePredicate, disabledGroups, time, datatype, raffleType);
        } else {
            throw new RaffleException("Missing data");
        }
    }

}
