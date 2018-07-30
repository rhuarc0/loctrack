package com.tander.locationtracker.mvp.model.repository;

import android.util.Log;

import com.tander.locationtracker.mvp.model.entity.Coordinates;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;

public class GpsSender {
    private static final String TAG = GpsSender.class.getSimpleName();

    public Completable sendCoordinates(Coordinates coordinate) {
        return Completable.timer(5, TimeUnit.SECONDS)
                .doOnEvent(throwable -> Log.d(TAG, "Send coordinates: coord = " + coordinate.toString()));
    }

}
