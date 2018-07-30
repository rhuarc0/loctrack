package com.tander.locationtracker.room;

import android.arch.persistence.room.TypeConverter;

import com.tander.locationtracker.mvp.model.entity.CoordinatesProvider;

public class ProviderConverter {

    @TypeConverter
    public boolean fromProvider(CoordinatesProvider coordinatesProvider) {
        return coordinatesProvider.getValue();
    }

    @TypeConverter
    public CoordinatesProvider toProvider(boolean value) {
        return CoordinatesProvider.fromValue(value);
    }
}
