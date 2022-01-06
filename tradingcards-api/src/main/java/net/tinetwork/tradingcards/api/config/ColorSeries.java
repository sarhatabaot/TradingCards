package net.tinetwork.tradingcards.api.config;

public class ColorSeries {
    private final String series;
    private final String type;
    private final String info;
    private final String about;
    private final String rarity;

    public ColorSeries(final String series, final String type, final String info, final String about, final String rarity) {
        this.series = series;
        this.type = type;
        this.info = info;
        this.about = about;
        this.rarity = rarity;
    }

    public String getSeries() {
        return series;
    }

    public String getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public String getAbout() {
        return about;
    }

    public String getRarity() {
        return rarity;
    }

    @Override
    public String toString() {
        return "ColorSeries{" +
                "series='" + series + '\'' +
                ", type='" + type + '\'' +
                ", info='" + info + '\'' +
                ", about='" + about + '\'' +
                ", rarity='" + rarity + '\'' +
                '}';
    }
}
