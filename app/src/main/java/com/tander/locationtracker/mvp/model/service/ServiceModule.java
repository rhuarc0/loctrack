package com.tander.locationtracker.mvp.model.service;

import android.location.LocationListener;

import com.tander.locationtracker.di.scopes.PerService;
import com.tander.locationtracker.mvp.model.repository.GpsBuffer;
import com.tander.locationtracker.mvp.model.repository.GpsSender;
import com.tander.locationtracker.mvp.presenter.ServicePresenter;

import dagger.Module;
import dagger.Provides;

@Module
@PerService
public class ServiceModule {

    @Provides
    LocationListener provideLocationListener(ServicePresenter servicePresenter) {
        return new LocationListenerImpl(servicePresenter);
    }

    @Provides
    ServicePresenter provideServicePresenter(GpsSender gpsSender, GpsBuffer gpsBuffer) {
        return new ServicePresenter(gpsSender, gpsBuffer);
    }
}
