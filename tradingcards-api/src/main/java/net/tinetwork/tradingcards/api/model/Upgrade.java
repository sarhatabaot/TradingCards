package net.tinetwork.tradingcards.api.model;

import net.tinetwork.tradingcards.api.model.pack.PackEntry;

import java.util.Objects;

/**
 * @author sarhatabaot
 */
public final class Upgrade {
    private final String id;
    private PackEntry required;
    private PackEntry result;

    public Upgrade(String id, PackEntry required, PackEntry result) {
        this.id = id;
        this.required = required;
        this.result = result;
    }

    public void setRequired(final PackEntry required) {
        this.required = required;
    }

    public void setResult(final PackEntry result) {
        this.result = result;
    }

    public String id() {
        return id;
    }

    public PackEntry required() {
        return required;
    }

    public PackEntry result() {
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Upgrade) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.required, that.required) &&
                Objects.equals(this.result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, required, result);
    }

    @Override
    public String toString() {
        return "Upgrade[" +
                "id=" + id + ", " +
                "required=" + required + ", " +
                "result=" + result + ']';
    }


}
