package com.drawwdev.raffle.calendar;

import com.drawwdev.raffle.Main;
import com.drawwdev.raffle.Raffle;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CListener implements Listener {

    private Main plugin;

    public CListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCEvent(CEvent event){
        for (String id : event.getListId()){
            Raffle raffle = plugin.getRaffleCommand().getRaffleManager().getRaffleStorage().get(id.toUpperCase());
            if (raffle != null){
                //TODO: keep going.
            }
        }
    }

}
