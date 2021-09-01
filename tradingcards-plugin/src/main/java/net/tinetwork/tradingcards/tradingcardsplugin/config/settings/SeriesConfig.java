package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class SeriesConfig extends SimpleConfigurate {
    private List<Series> series;

    public SeriesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator, "series.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.series = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String seriesKey = nodeEntry.getValue().key().toString();
            try {
                series.add(getSeries(seriesKey));
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

    public List<Series> series() {
        return this.series;
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(ScheduleSerializer.TYPE, ScheduleSerializer.INSTANCE)
                        .registerExact(SeriesSerializer.TYPE, SeriesSerializer.INSTANCE)));
    }

    public static final class ScheduleSerializer implements TypeSerializer<Schedule> {
        public static final ScheduleSerializer INSTANCE = new ScheduleSerializer();
        public static final Class<Schedule> TYPE = Schedule.class;

        @Override
        public Schedule deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(final Type type, @Nullable final Schedule obj, final ConfigurationNode node) throws SerializationException {

        }
    }
    public static final class SeriesSerializer implements TypeSerializer<Series> {
        public static final SeriesSerializer INSTANCE = new SeriesSerializer();
        public static final Class<Series> TYPE = Series.class;

        private static final String DISPLAY_NAME = "display-name";
        private static final String MODE = "mode";

        private SeriesSerializer() {

        }

        @Override
        public Series deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(final Type type, @Nullable final Series obj, final ConfigurationNode node) throws SerializationException {

        }
    }
}
