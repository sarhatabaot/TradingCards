package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.DateSchedule;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;
import net.tinetwork.tradingcards.api.model.schedule.ScheduleType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class SeriesConfig extends SimpleConfigurate {
    private Map<String,Series> series;

    public SeriesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator, "series.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.series = new HashMap<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String seriesKey = nodeEntry.getValue().key().toString();
            try {
                series.put(seriesKey,getSeries(seriesKey));
            } catch (SerializationException e){
                plugin.getLogger().severe(e.getMessage());
                plugin.debug("Couldn't add="+seriesKey);
            }
        }
        plugin.debug("Total Series="+series.size());
    }

    private Series getSeries(final String key) throws SerializationException {
        return rootNode.node(key).get(Series.class);
    }

    public Map<String,Series> series() {
        return this.series;
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(DateScheduleSerializer.TYPE, DateScheduleSerializer.INSTANCE)
                        .registerExact(SeriesSerializer.TYPE, SeriesSerializer.INSTANCE)));
    }


    public static final class DateScheduleSerializer implements TypeSerializer<DateSchedule> {
        public static final DateScheduleSerializer INSTANCE = new DateScheduleSerializer();
        public static final Class<DateSchedule> TYPE = DateSchedule.class;

        //This should be changed for an abstract configuration, something that is mostly consistent
        //across all types.
        private static final String ACTIVE_FROM = "active-from";
        private static final String ACTIVE_UNTIL = "active-until";
        private  DateScheduleSerializer(){

        }
        @Override
        public DateSchedule deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final LocalDate activeFromDate = LocalDate.parse(node.node(ACTIVE_FROM).getString());
            final LocalDate activeUntilDate = LocalDate.parse(node.node(ACTIVE_UNTIL).getString());

            return new DateSchedule(activeFromDate,activeUntilDate);
        }

        @Override
        public void serialize(final Type type, @Nullable final DateSchedule obj, final ConfigurationNode node) throws SerializationException {
            //
        }
    }

    public static final class SeriesSerializer implements TypeSerializer<Series> {
        public static final SeriesSerializer INSTANCE = new SeriesSerializer();
        public static final Class<Series> TYPE = Series.class;

        private static final String DISPLAY_NAME = "display-name";
        private static final String MODE = "mode";
        private static final String SCHEDULE = "schedule";

        private static final String SCHEDULE_TYPE = "type";

        private SeriesSerializer() {

        }

        @Override
        public Series deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String modeString = node.node(MODE).getString();

            final Mode mode = Mode.getMode(modeString);
            Schedule schedule = null;
            ScheduleType scheduleType = null;

            if(mode == Mode.SCHEDULED) {
                scheduleType = ScheduleType.valueOf(node.node(SCHEDULE).node(SCHEDULE_TYPE).getString());
            }


            if(scheduleType == ScheduleType.DATE) {
                schedule = node.node(SCHEDULE).get(Schedule.class);
            }


            return new Series(node.key().toString(),mode,displayName,schedule);
        }

        @Override
        public void serialize(final Type type, @Nullable final Series obj, final ConfigurationNode node) throws SerializationException {
            //
        }
    }
}
