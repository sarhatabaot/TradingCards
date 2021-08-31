package net.tinetwork.tradingcards.api.model;


import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;

import java.time.LocalDate;

public class Series {
    private String name;
    private Mode mode;
    private String displayName;
    private String displayColor;

    private Schedule schedule;

    public Series(final String name, final Mode mode, final String displayName, final String displayColor, final LocalDate activeFrom, final LocalDate activeUntil) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.displayColor = displayColor;
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

    public Mode getMode() {
        return mode;
    }
}
