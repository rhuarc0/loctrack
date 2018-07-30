package com.tander.locationtracker.mvp.view;

import com.tander.locationtracker.di.components.AppComponent;
import com.tander.locationtracker.di.scopes.PerActivity;

import dagger.Component;

@Component(modules = {MainModule.class}, dependencies = {AppComponent.class})
@PerActivity
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
