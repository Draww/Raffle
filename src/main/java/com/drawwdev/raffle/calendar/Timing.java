package com.drawwdev.raffle.calendar;

import com.drawwdev.raffle.Raffle;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Timing {
    long nextCall;
    private String name;
    private CManager cManager;
    private ArrayList<Integer> days = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> monthlyDates = new ArrayList<>();
    private ArrayList<String> yearlyDates = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private Set<Long> relMills = new HashSet<>();
    private Set<ZonedDateTime> relDates = new HashSet<>();
    private ZoneId zone;
    private int currentMonth, currentYear, currentMonthDay, currentWeekDay;

    public Timing(String name, CManager cManager) {
        this.name = name;
        this.cManager = cManager;
    }

    public void addDay(Integer day) {
        days.add(day);
    }

    public void addTime(String time) {
        times.add(time);
    }

    public void addDate(String date) {
        dates.add(date);
    }

    public void addMonthlyDate(String date) {
        monthlyDates.add(date);
    }

    public void addYearlyDate(String date) {
        yearlyDates.add(date);
    }

    public void setup() {
        ZonedDateTime now = ZonedDateTime.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
        currentMonthDay = now.getDayOfMonth();
        currentWeekDay = now.getDayOfWeek().getValue();
        zone = ZoneId.systemDefault();

        if (!dates.isEmpty()) {
            for (String date : dates) {
                int day, month, year;
                String[] dateParts = date.split("\\.");
                try {
                    day = Integer.parseInt(dateParts[0]);
                    month = Integer.parseInt(dateParts[1]);
                    year = Integer.parseInt(dateParts[2]);
                } catch (Exception ex) {
                    continue;
                }
                relDates.add(ZonedDateTime.of(year, month, day, 0, 0, 0, 0, zone));
            }
        }
        if (!monthlyDates.isEmpty()) {
            for (String date : monthlyDates) {
                int day;
                try {
                    day = Integer.parseInt(date);
                } catch (Exception ex) {
                    continue;
                }
                relDates.add(ZonedDateTime.of(currentYear, currentMonth, day, 0, 0, 0, 0, zone));
                relDates.add(ZonedDateTime.of(currentYear, currentMonth, day, 0, 0, 0, 0, zone).plusMonths(1));
            }
        }
        if (!yearlyDates.isEmpty()) {
            for (String date : yearlyDates) {
                int day, month;
                String[] dateParts = date.split("\\.");
                try {
                    day = Integer.parseInt(dateParts[0]);
                    month = Integer.parseInt(dateParts[1]);
                } catch (Exception e) {
                    continue;
                }
                relDates.add(ZonedDateTime.of(currentYear, month, day, 0, 0, 0, 0, zone));
                relDates.add(ZonedDateTime.of(currentYear, month, day, 0, 0, 0, 0, zone).plusYears(1));
            }
        }
        if (!days.isEmpty()) {
            int diffDay;
            for (int day : days) {
                diffDay = day - currentWeekDay;
                if (diffDay == 0) {
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone));
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusWeeks(1));
                } else if (diffDay > 0) {
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusDays(diffDay));
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).plusDays(diffDay).plusWeeks(1));
                } else {
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).minusDays(-diffDay));
                    relDates.add(ZonedDateTime.of(currentYear, currentMonth, currentMonthDay, 0, 0, 0, 0, zone).minusDays(-diffDay).plusWeeks(1));
                }
            }
        }

        for (ZonedDateTime date : relDates){
            for (String timeString : times){
                String[] timeParts = timeString.split(":");
                int hour, min;
                try {
                    hour = Integer.parseInt(timeParts[0]);
                    min = Integer.parseInt(timeParts[1]);
                } catch (Exception ex){
                    continue;
                }
                relMills.add(date.plusHours(hour).plusMinutes(min).toInstant().toEpochMilli());
            }
        }
    }

    public void setNextMill(){
        long currentMill = System.currentTimeMillis(), toReturn = Long.MAX_VALUE;
        Iterator<Long> relMillIterator = relMills.iterator();
        while (relMillIterator.hasNext()){
            Long milli = relMillIterator.next();
            if (currentMill > milli){
                relMillIterator.remove();
                continue;
            }
            if (milli < toReturn) {
                toReturn = milli;
            }
        }
        if (relMills.isEmpty()){
            relDates.clear();
            relMills.clear();
            setup();

            relMillIterator = relMills.iterator();
            while (relMillIterator.hasNext()){
                Long milli = relMillIterator.next();
                if (currentMill > milli) {
                    relMillIterator.remove();
                    continue;
                }
                if (milli < toReturn) {
                    toReturn = milli;
                }
            }
            if (relMills.isEmpty()) {
                //TODO: Remove Timer
                return;
            }
        }
        nextCall = toReturn;
    }

    public long getNextCall() {
        return this.nextCall;
    }

}
