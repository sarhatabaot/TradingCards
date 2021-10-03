package net.tinetwork.tradingcards.api.exceptions;

public class UnsupportedDropTypeException extends Exception{
    public UnsupportedDropTypeException() {
    }

    public UnsupportedDropTypeException(final String message) {
        super(message);
    }
}