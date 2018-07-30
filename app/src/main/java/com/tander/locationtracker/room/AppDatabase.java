package com.tander.locationtracker.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.tander.locationtracker.mvp.model.entity.Coordinates;

@Database(entities = {Coordinates.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CoordinatesDao getCoordinatesDao();
}
