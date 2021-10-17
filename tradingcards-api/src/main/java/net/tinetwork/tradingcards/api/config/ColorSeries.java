package net.tinetwork.tradingcards.api.config;

public class ColorSeries {
    private final String id;
    private String series;
    private String type;
    private String info;
    private String about;
    private String rarity;

    public ColorSeries(final String id, final String series, final String type, final String info, final String about, final String rarity) {
        this.id = id;
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

    public String getId() {
        return id;
    }
}
