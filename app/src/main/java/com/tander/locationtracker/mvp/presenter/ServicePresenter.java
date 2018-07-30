package com.tander.locationtracker.mvp.presenter;

import com.tander.locationtracker.mvp.model.entity.Coordinates;
import com.tander.locationtracker.mvp.model.repository.GpsBuffer;
import com.tander.locationtracker.mvp.model.repository.GpsSender;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServicePresenter extends AbstractPresenter {

    private GpsSender gpsSender;
    private GpsBuffer gpsBuffer;

    public ServicePresenter(GpsSender sender, GpsBuffer buffer) {
        gpsSender = sender;
        gpsBuffer = buffer;
    }

    public void processCoordinates(final Coordinates coordinates) {
        Disposable disposable = gpsBuffer.storeCoordinates(coordinates)
                .subscribeOn(Schedulers.computation())
                .subscribe(this::tryToSend);
        subscriptions.add(disposable);
    }

    public void tryToSend() {
        Disposable disposable = gpsBuffer.loadCoordinates()
                .flatMapCompletable(coordinates -> gpsSender
                        .sendCoordinates(coordinates)
                        .onErrorComplete()
                        .andThen(gpsBuffer.deleteCoordinateByTimestamp(coordinates.getTimestamp())))
                .subscribeOn(Schedulers.computation())
                .subscribe();
        subscriptions.add(disposable);
    }

}
