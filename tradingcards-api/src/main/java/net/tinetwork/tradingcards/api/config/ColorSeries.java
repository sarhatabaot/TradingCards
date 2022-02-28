package net.tinetwork.tradingcards.api.config;

import java.util.Objects;

public final class ColorSeries {
    private String series;
    private String type;
    private String info;
    private String about;
    private String rarity;

    public ColorSeries(String series, String type, String info, String about,
                       String rarity) {
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


    public void setSeries(final String series) {
        this.series = series;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setInfo(final String info) {
        this.info = info;
    }

    public void setAbout(final String about) {
        this.about = about;
    }

    public void setRarity(final String rarity) {
        this.rarity = rarity;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ColorSeries) obj;
        return Objects.equals(this.series, that.series) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.info, that.info) &&
                Objects.equals(this.about, that.about) &&
                Objects.equals(this.rarity, that.rarity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, type, info, about, rarity);
    }

}
