package com.drawwdev.raffle;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;

public class RaffleData<T> {

    private HashMap<Integer, T> dataList = new HashMap<>();

    public RaffleData(){

    }

    public RaffleData set(List<String> args){
        Integer count = 0;
        for (String arg : args){
            dataList.put(count, (T) arg);
            count++;
        }
        return this;
    }

    public RaffleData set(Integer arg, T data){
        if (getDataList().containsKey(arg)){
            getDataList().remove(arg);
        }
        getDataList().put(arg, data);
        return this;
    }

    public Integer size(){
        return dataList.size();
    }

    public T get(Integer arg){
        return getDataList().getOrDefault(arg,null);
    }

    public HashMap<Integer, T> getDataList() {
        return dataList;
    }
}
