package com.tander.locationtracker.mvp.view;

import com.tander.locationtracker.di.scopes.PerActivity;
import com.tander.locationtracker.mvp.presenter.MainPresenter;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class MainModule {

    @Provides
    public MainPresenter providesMainPresenter(MyServiceConnection serviceConnection) {
        return new MainPresenter(serviceConnection);
    }

    @Provides
    public MyServiceConnection providesMyServiceConnection() {
        return new MyServiceConnection();
    }

}
