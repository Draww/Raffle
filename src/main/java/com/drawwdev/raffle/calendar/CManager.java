package com.drawwdev.raffle.calendar;

import com.drawwdev.raffle.Main;
import com.drawwdev.raffle.Raffle;
import com.drawwdev.raffle.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CManager {

    private Main plugin;
    private Config config;
    private Map<String, Timing> timingMap;
    private Map<String, Raffle> raffleMap;

    public CManager(Main plugin) {
        this.plugin = plugin;
        config = plugin.getConfigs().get("calendar");
        timingMap = new HashMap<>();
        raffleMap = new HashMap<>();
        load();
    }

    private void load() {
        if (!config.getConfig().isConfigurationSection("calendars")) return;
        ConfigurationSection calendars = config.getConfig().getConfigurationSection("calendars");
        for (String id : calendars.getKeys(false)) {
            if (!calendars.isString(id + ".type") || !calendars.isString(id + ".time")) {
                plugin.getLogger().warning("Could not load the raffle calendar '" + id + "'");
                plugin.getLogger().warning("Due to missing type or timing.");
                continue;
            }
            if (timingMap.containsKey(id) || raffleMap.containsKey(id)) {
                plugin.getLogger().warning("There is already an raffle calendar with the id '" + id + "'");
                continue;
            }
            if (!plugin.getRaffleCommand().getRaffleManager().getRaffleStorage().has(id.toUpperCase())) {
                plugin.getLogger().warning("There is no raffle with id '" + id + "'");
                continue;
            }
            Timing timing = new Timing(id, this);

            String type = calendars.getString(id + ".type");
            if (!loadType(timing, id, type)) {
                continue;
            }
            String time = calendars.getString(id + ".time");
            if (!loadTime(timing, id, time)) {
                continue;
            }

            plugin.getLogger().info("Listing loaded dates and times from: " + id);
            timing.setup();

            timingMap.put(id, timing);
            raffleMap.put(id, plugin.getRaffleCommand().getRaffleManager().getRaffleStorage().get(id));
            timing.setNextMill();
        }
    }

    public void reload() {
        config = plugin.getConfigs().get("calendar");
        timingMap.clear();
        raffleMap.clear();
        load();
    }

    private boolean loadTime(Timing timing, String id, String time) {
        String[] times = time.replaceAll(" ", "").split(",");
        times = placeholders(times);
        int firstInt, secondInt;
        for (String t : times) {
            String[] timeParts = t.split(":");
            Bukkit.broadcastMessage(timeParts.length + " " + timeParts[0].length() + " " + timeParts[1].length());
            if (timeParts.length != 2 || timeParts[0].length() != 2 || timeParts[1].length() != 2) {
                plugin.getLogger().warning("Could not load the time '" + time + "' in the raffle calendar '" + id + "'");
                return false;
            }
            try {
                firstInt = Integer.parseInt(timeParts[0]);
                secondInt = Integer.parseInt(timeParts[1]);

                if (firstInt > 23 || firstInt < 0 || secondInt > 59 || secondInt < 0) {
                    plugin.getLogger().warning("Could not load the time '" + time + "' in the raffle calendar '" + id + "'");
                    plugin.getLogger().warning("Timing has invalid values! Use 24h format.");
                    return false;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load the time '" + time + "' in the raffle calendar '" + id + "'");
                plugin.getLogger().warning("Timing has invalid values! Use integers.");
                return false;
            }
            timing.addTime(t);
        }
        return true;
    }

    private boolean loadType(Timing timing, String id, String type) {
        if (type.equalsIgnoreCase("everyday")) {
            timing.addDay(1);
            timing.addDay(2);
            timing.addDay(3);
            timing.addDay(4);
            timing.addDay(5);
            timing.addDay(6);
            timing.addDay(7);
            return true;
        }

        type = type.replaceAll(" ", "");
        String[] types = type.split(",");

        singleType:
        for (String singleType : types) {
            if (!singleType.contains(".")) {
                if (singleType.equalsIgnoreCase("monday") ||
                        singleType.equalsIgnoreCase("tuesday") ||
                        singleType.equalsIgnoreCase("wednesday") ||
                        singleType.equalsIgnoreCase("thursday") ||
                        singleType.equalsIgnoreCase("friday") ||
                        singleType.equalsIgnoreCase("saturday") ||
                        singleType.equalsIgnoreCase("sunday")) {
                    switch (singleType.toLowerCase()) {
                        case "monday":
                            timing.addDay(1);
                            break;
                        case "tuesday":
                            timing.addDay(2);
                            break;
                        case "wednesday":
                            timing.addDay(3);
                            break;
                        case "thursday":
                            timing.addDay(4);
                            break;
                        case "friday":
                            timing.addDay(5);
                            break;
                        case "saturday":
                            timing.addDay(6);
                            break;
                        case "sunday":
                            timing.addDay(7);
                            break;
                    }
                    continue singleType;
                } else if (singleType.length() == 2) {
                    try {
                        Integer.parseInt(singleType);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Could not load the dd date '" + singleType + "' in the raffle calendar '" + id + "'");
                        return false;
                    }
                    timing.addMonthlyDate(singleType);
                } else {
                    plugin.getLogger().warning("Could not load the dd date or day '" + singleType + "' in the raffle calendar '" + id + "'");
                    return false;
                }
            } else {
                String[] dateParts = singleType.split("\\.");
                if (dateParts.length == 2) {
                    if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2)) {
                        plugin.getLogger().warning("Could not load the dd.mm date '" + singleType + "' in the raffle calendar '" + id + "'");
                        return false;
                    }
                    for (String datePart : dateParts) {
                        try {
                            Integer.parseInt(datePart);
                        } catch (Exception e) {
                            plugin.getLogger().warning("Could not load the dd.mm date '" + singleType + "' in the raffle calendar '" + id + "'");
                            return false;
                        }
                    }
                    timing.addYearlyDate(singleType);
                } else if (dateParts.length == 3) {
                    if (!(dateParts[0].length() == 2 && dateParts[1].length() == 2 && dateParts[2].length() == 4)) {
                        plugin.getLogger().warning("Could not load the date '" + singleType + "' in the raffle calendar '" + id + "'");
                        return false;
                    }
                    for (String datePart : dateParts) {
                        try {
                            Integer.parseInt(datePart);
                        } catch (Exception e) {
                            plugin.getLogger().warning("Could not load the date '" + singleType + "' in the raffle calendar '" + id + "'");
                            return false;
                        }
                    }
                    timing.addDate(singleType);
                } else {
                    plugin.getLogger().warning("Could not load the general date '" + singleType + "' in the raffle calendar '" + id + "'");
                    return false;
                }
            }
        }
        return true;
    }

    private String[] placeholders(String[] times) {
        HashSet<String> toReturn = new HashSet<>();

        for (int i = 0; i < times.length; i++) {
            times[i] = times[i].replace("X", "x");
        }
        ArrayList<String> current = new ArrayList<>();
        for (String timing : times) {
            current.clear();
            if (!timing.contains("x")) {
                toReturn.add(timing);
                continue;
            }
            current.add(timing);
            ListIterator<String> iterator = current.listIterator(1);
            while (iterator.hasPrevious()) {
                String currentTiming = iterator.previous();

                if (currentTiming.charAt(0) == 'x') {
                    iterator.remove();
                    iterator.add(currentTiming.replaceFirst("x", "0"));
                    iterator.add(currentTiming.replaceFirst("x", "1"));
                    if (currentTiming.charAt(1) == 'x' ||
                            currentTiming.charAt(1) == '0' ||
                            currentTiming.charAt(1) == '1' ||
                            currentTiming.charAt(1) == '2' ||
                            currentTiming.charAt(1) == '3') {
                        iterator.add(currentTiming.replaceFirst("x", "2"));
                    }
                } else if (currentTiming.charAt(1) == 'x') {
                    iterator.remove();
                    iterator.add(currentTiming.replaceFirst("x", "0"));
                    iterator.add(currentTiming.replaceFirst("x", "1"));
                    iterator.add(currentTiming.replaceFirst("x", "2"));
                    iterator.add(currentTiming.replaceFirst("x", "3"));
                    if (currentTiming.charAt(0) == '0' || currentTiming.charAt(0) == '1') {
                        iterator.add(currentTiming.replaceFirst("x", "4"));
                        iterator.add(currentTiming.replaceFirst("x", "5"));
                        iterator.add(currentTiming.replaceFirst("x", "6"));
                        iterator.add(currentTiming.replaceFirst("x", "7"));
                        iterator.add(currentTiming.replaceFirst("x", "8"));
                        iterator.add(currentTiming.replaceFirst("x", "9"));
                    }
                } else if (currentTiming.charAt(3) == 'x') {
                    iterator.remove();
                    iterator.add(currentTiming.replaceFirst("x", "0"));
                    iterator.add(currentTiming.replaceFirst("x", "1"));
                    iterator.add(currentTiming.replaceFirst("x", "2"));
                    iterator.add(currentTiming.replaceFirst("x", "3"));
                    iterator.add(currentTiming.replaceFirst("x", "4"));
                    iterator.add(currentTiming.replaceFirst("x", "5"));
                } else if (currentTiming.charAt(4) == 'x') {
                    iterator.remove();
                    iterator.add(currentTiming.replaceFirst("x", "0"));
                    iterator.add(currentTiming.replaceFirst("x", "1"));
                    iterator.add(currentTiming.replaceFirst("x", "2"));
                    iterator.add(currentTiming.replaceFirst("x", "3"));
                    iterator.add(currentTiming.replaceFirst("x", "4"));
                    iterator.add(currentTiming.replaceFirst("x", "5"));
                    iterator.add(currentTiming.replaceFirst("x", "6"));
                    iterator.add(currentTiming.replaceFirst("x", "7"));
                    iterator.add(currentTiming.replaceFirst("x", "8"));
                    iterator.add(currentTiming.replaceFirst("x", "9"));
                }
            }
            toReturn.addAll(current);
        }
        String[] arrayToReturn = new String[toReturn.size()];
        Iterator<String> iterator = toReturn.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            arrayToReturn[i] = iterator.next();
            i++;
        }
        return arrayToReturn;
    }

    private void call(ArrayList<String> listId) {
        long callMilli = timingMap.get(listId.get(0)).getNextCall();
        long current = System.currentTimeMillis();
        if (callMilli > current) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            call(listId);
                        }
                    }.runTask(plugin);
                }
            }.runTaskLaterAsynchronously(plugin, (callMilli - current) / 50 + 1);
        }
        Bukkit.getPluginManager().callEvent(new CEvent(listId));

        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String id : listId) {
                            Timing timing = timingMap.get(id);
                            timing.setNextMill();
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskLaterAsynchronously(plugin, 100);
    }

    public void callNextMinute() {
        ArrayList<String> toCall = new ArrayList<>();
        long currentMillis = System.currentTimeMillis(), milli = 0;
        long diff = 0;
        for (String label : timingMap.keySet()) {
            Timing timing = timingMap.get(label);
            if (((timing.getNextCall() - currentMillis) / 1000) < 60) {
                if (toCall.isEmpty()) {
                    milli = timing.getNextCall();
                    diff = (milli - currentMillis) / 1000;
                }
                toCall.add(label);
            }
        }
        if (!toCall.isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            call(toCall);
                        }
                    }.runTask(plugin);
                }
            }.runTaskLaterAsynchronously(plugin, diff * 20 + 10);
        }
    }

    public void reCalcNextMillis() {
        timingMap.values().forEach(Timing::setNextMill);
    }

    public boolean add(String id, String type, String time) {
        if (timingMap.keySet().contains(id)) {
            return false;
        }
        Timing timing = new Timing(id, this);
        if (!loadType(timing, id, type)) {
            return false;
        }
        if (!loadTime(timing, id, time)) {
            return false;
        }
        if (!plugin.getRaffleCommand().getRaffleManager().getRaffleStorage().has(id)) {
            return false;
        }
        timing.setup();
        timingMap.put(id, timing);
        raffleMap.put(id, plugin.getRaffleCommand().getRaffleManager().getRaffleStorage().get(id));
        timing.setNextMill();
        return true;
    }

    public void remove(String id) {
        timingMap.remove(id);
        raffleMap.remove(id);
    }

    public boolean isRegistered(String id) {
        return timingMap.containsKey(id);
    }

    public int secondsToNextCall(String id) {
        Timing timing = timingMap.get(id);
        if (timing == null) return -1;
        long nextCall = timing.getNextCall();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > nextCall) {
            timing.setNextMill();
            return secondsToNextCall(id);
        }
        return (int) ((currentTimeMillis - nextCall) / 1000.);
    }

    public int getNumberOfCalendars() {
        return timingMap.keySet().size();
    }

    private Map<String, Timing> getAllRegisteredTimings() {
        HashMap<String, Timing> toReturn = new HashMap<>();
        for (String id : timingMap.keySet()) {
            if (config.getConfig().isConfigurationSection("calendars." + id)) continue;
            toReturn.put(id, timingMap.get(id));
        }
        return toReturn;
    }
}
