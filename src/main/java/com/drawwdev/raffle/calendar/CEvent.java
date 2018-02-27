package com.drawwdev.raffle.calendar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private ArrayList<String> listId;
    private int hour, minute, year;
    private String month;

    public CEvent(ArrayList listId){
        this.listId = listId;
        ZonedDateTime now = ZonedDateTime.now();
        this.minute = now.getSecond() < 20 ? now.getMinute() : (now = now.plusMinutes(1)).getMinute();
        this.hour = now.getHour();
        this.year = now.getYear();
        this.month = now.getMonth().toString();
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList(){
        return handlers;
    }

    public ArrayList<String> getListId(){
        return this.listId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getYear() {
        return year;
    }

    public String getMonth() {
        return this.month;
    }

    public String getTime() {
        return (this.hour < 10 ? "0" + this.hour : String.valueOf(this.hour)) + ":" + (this.minute < 10 ? "0" + this.minute : String.valueOf(this.minute));
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
