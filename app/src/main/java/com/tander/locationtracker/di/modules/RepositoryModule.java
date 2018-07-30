package com.tander.locationtracker.di.modules;

import com.tander.locationtracker.mvp.model.repository.GpsBuffer;
import com.tander.locationtracker.mvp.model.repository.GpsSender;
import com.tander.locationtracker.room.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    GpsSender provideGpsSender() {
        return new GpsSender();
    }

    @Provides
    @Singleton
    GpsBuffer provideGpsBuffer(AppDatabase appDatabase) {
        return new GpsBuffer(appDatabase);
    }
}
