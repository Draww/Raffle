package com.drawwdev.raffle;

import java.util.HashMap;

public class RaffleData<T> {

    private HashMap<Integer, T> dataList = new HashMap<>();

    public RaffleData(){

    }

    public RaffleData set(Integer arg, T data){
        if (getDataList().containsKey(arg)){
            getDataList().remove(arg);
        }
        getDataList().put(arg, data);
        return this;
    }

    public T get(Integer arg){
        return getDataList().getOrDefault(arg,null);
    }

    public HashMap<Integer, T> getDataList() {
        return dataList;
    }
}
