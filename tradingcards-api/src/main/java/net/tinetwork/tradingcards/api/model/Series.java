package net.tinetwork.tradingcards.api.model;


import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;

import java.time.LocalDate;

public class Series {
    private String name;
    private Mode mode;
    private String displayName;

    private Schedule schedule;

    public Series(final String name, final Mode mode, final String displayName, final Schedule schedule) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.schedule = schedule;
    }

    public boolean isActive() {
        if(mode == Mode.DISABLED)
            return false;
        if(mode == Mode.ACTIVE)
            return true;

        if(mode == Mode.SCHEDULED) {
            return schedule.isActive();
        }

        //fallthrough
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public Schedule getSchedule() {
        return schedule;
    }

    public String getName() {
        return name;
    }

    public Mode getMode() {
        return mode;
    }
}
