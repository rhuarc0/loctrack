package com.tander.locationtracker.mvp.model.repository;


import android.util.Log;

import com.tander.locationtracker.mvp.model.entity.Coordinates;
import com.tander.locationtracker.room.AppDatabase;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GpsBuffer {

    private static final String TAG = GpsBuffer.class.getSimpleName();

    private AppDatabase appDatabase;

    public GpsBuffer(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Completable storeCoordinates(Coordinates coordinates) {
        return Completable.fromAction(() ->
                appDatabase.getCoordinatesDao().storeCoordinates(coordinates))
                .doFinally(() -> Log.d(TAG, "store coordinates: coordinates = " + coordinates.toString()));
    }

    public Observable<Coordinates> loadCoordinates() {
        return appDatabase.getCoordinatesDao()
                .fetchCoordinates()
                .toObservable()
                .doOnNext(coordinates -> Log.d(TAG, "load coordinates: items obtained = " + coordinates.size()))
                .flatMapIterable(coordinates -> coordinates)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteCoordinateByTimestamp(long timestamp) {
        return Completable.fromAction(() ->
                appDatabase.getCoordinatesDao().deleteCoordinatesByTimestamp(timestamp))
                .doFinally(() -> Log.d(TAG, "delete coordinates: timestamp = " + timestamp));
    }

}
