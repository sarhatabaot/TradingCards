package net.tinetwork.tradingcards.api.config;

public record ColorSeries(String series, String type, String info, String about,
                          String rarity) {

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
