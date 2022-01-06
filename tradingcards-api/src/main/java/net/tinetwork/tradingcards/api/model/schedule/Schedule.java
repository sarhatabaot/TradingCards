package net.tinetwork.tradingcards.api.model.schedule;

public abstract class Schedule {
    private final ScheduleType type;

    public Schedule(final ScheduleType type) {
        this.type = type;
    }

    public abstract boolean isActive();

    public ScheduleType getType() {
        return this.type;
    }
}
