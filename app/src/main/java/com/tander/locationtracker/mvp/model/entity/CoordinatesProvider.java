package com.tander.locationtracker.mvp.model.entity;

public enum CoordinatesProvider {
    GPS(true),
    NETWORK(false);

    private boolean value;

    public boolean getValue() {
        return value;
    }

    public static CoordinatesProvider fromValue(boolean value) {
        if (value)
            return GPS;
        else
            return NETWORK;
    }

    CoordinatesProvider(boolean isGps) {
        value = isGps;
    }
}
