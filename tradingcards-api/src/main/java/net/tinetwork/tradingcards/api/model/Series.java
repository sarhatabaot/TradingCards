package net.tinetwork.tradingcards.api.model;


import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;

public class Series {
    private final String name;
    private Mode mode;
    private String displayName;
    private ColorSeries colorSeries;

    private final Schedule schedule;

    public Series(final String name, final Mode mode, final String displayName, final Schedule schedule, final ColorSeries colorSeries) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.schedule = schedule;
        this.colorSeries = colorSeries;
    }

    public boolean isActive() {
        if(mode == Mode.DISABLED)
            return false;
        if(mode == Mode.ACTIVE)
            return true;

        //not implemented yet
        if(mode == Mode.SCHEDULED) {
            //return schedule.isActive();
            return false;
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

    public ColorSeries getColorSeries() {
        return colorSeries;
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setColorSeries(final ColorSeries colorSeries) {
        this.colorSeries = colorSeries;
    }

    @Override
    public String toString() {
        return "Series{" +
                "name='" + name + '\'' +
                ", mode=" + mode +
                ", displayName='" + displayName + '\'' +
                ", colorSeries=" + colorSeries +
                ", schedule=" + schedule +
                '}';
    }
}
