package com.tander.locationtracker.di.components;

import com.tander.locationtracker.di.modules.AppModule;
import com.tander.locationtracker.di.modules.DatabaseModule;
import com.tander.locationtracker.di.modules.RepositoryModule;
import com.tander.locationtracker.mvp.model.repository.GpsBuffer;
import com.tander.locationtracker.mvp.model.repository.GpsSender;
import com.tander.locationtracker.room.AppDatabase;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DatabaseModule.class, RepositoryModule.class})
public interface AppComponent {
    AppDatabase provideAppDatabase();
    GpsSender provideGpsSender();
    GpsBuffer provideGpsBuffer();
}
