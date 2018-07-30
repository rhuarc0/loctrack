package com.tander.locationtracker.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tander.locationtracker.mvp.model.entity.Coordinates;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface CoordinatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeCoordinates(Coordinates coordinates);

    @Query("DELETE FROM Coordinates WHERE timestamp = :timestamp")
    void deleteCoordinatesByTimestamp(long timestamp);

    @Query("SELECT * FROM Coordinates ORDER BY timestamp")
    Maybe<List<Coordinates>> fetchCoordinates();
}

