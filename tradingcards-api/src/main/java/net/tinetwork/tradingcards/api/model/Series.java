package net.tinetwork.tradingcards.api.model;


import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;
import org.apache.commons.lang.NotImplementedException;

public class Series {
    private final String id;
    private Mode mode;
    private String displayName;
    private ColorSeries colorSeries;

    private final Schedule schedule;

    public Series(final String id, final Mode mode, final String displayName, final Schedule schedule, final ColorSeries colorSeries) {
        this.id = id;
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
            throw new NotImplementedException();
            //return schedule.isActive(); todo
        }

        //fallthrough
        return false;
    }

    public String getDisplayName() {
        if(displayName == null || displayName.isEmpty())
            return id.replace("_"," ");
        return displayName;
    }
    
    public Schedule getSchedule() {
        return schedule;
    }

    public String getId() {
        return id;
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
                "name='" + id + '\'' +
                ", mode=" + mode +
                ", displayName='" + displayName + '\'' +
                ", colorSeries=" + colorSeries +
                ", schedule=" + schedule +
                '}';
    }
}
