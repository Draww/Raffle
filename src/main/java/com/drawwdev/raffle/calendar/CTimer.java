package com.drawwdev.raffle.calendar;

import com.drawwdev.raffle.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZonedDateTime;

public class CTimer extends BukkitRunnable {

    private CManager cManager;
    private Main plugin;

    public CTimer(Main plugin){
        this.plugin = plugin;
        this.cManager = plugin.getCManager();

        int diff = ZonedDateTime.now().getSecond() - 30;

        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cManager.reCalcNextMillis();
                    }
                }.runTask(plugin);
            }
        }.runTaskLaterAsynchronously(plugin, (diff < 0 ? (-diff * 20 - 20) : ((60 - diff) * 20 - 20)));

        this.runTaskTimerAsynchronously(plugin, (diff < 0 ? (-diff * 20) : ((60 - diff) * 20)), 20 * 60);
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cManager.callNextMinute();
            }
        }.runTask(plugin);

        // make sure the timer is not getting of time
        //   tolerance: xx:20 to xx:40
        int sec = ZonedDateTime.now().getSecond();
        if (sec < 20 || sec > 40) {
            plugin.getNewTimer();
            cancel();
        }
    }
}
