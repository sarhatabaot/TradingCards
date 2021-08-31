package net.tinetwork.tradingcards.api.model.schedule;


import java.time.LocalDate;

public class DateSchedule extends Schedule{
    private LocalDate start;
    private LocalDate end;


    public DateSchedule(final LocalDate start, final LocalDate end) {
        super(ScheduleType.DATE);
        this.start = start;
        this.end = end;
    }


    @Override
    public boolean isActive() {
        return false;
    }
}
