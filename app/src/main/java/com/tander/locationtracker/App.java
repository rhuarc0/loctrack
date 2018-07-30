package com.tander.locationtracker;

import android.app.Activity;
import android.app.Application;

import com.tander.locationtracker.di.components.AppComponent;
import com.tander.locationtracker.di.components.DaggerAppComponent;
import com.tander.locationtracker.di.modules.AppModule;
import com.tander.locationtracker.di.modules.DatabaseModule;
import com.tander.locationtracker.di.modules.RepositoryModule;

public class App extends Application {

    private AppComponent appComponent;

    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .databaseModule(new DatabaseModule())
                .repositoryModule(new RepositoryModule())
                .build();

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
