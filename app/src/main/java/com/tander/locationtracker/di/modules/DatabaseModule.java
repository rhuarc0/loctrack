package com.tander.locationtracker.di.modules;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.tander.locationtracker.room.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "coordinatesDb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}
