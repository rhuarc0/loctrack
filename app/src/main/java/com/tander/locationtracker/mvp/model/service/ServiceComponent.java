package com.tander.locationtracker.mvp.model.service;

import com.tander.locationtracker.di.components.AppComponent;
import com.tander.locationtracker.di.scopes.PerService;

import dagger.Component;
import dagger.Subcomponent;

@PerService
@Component(dependencies = {AppComponent.class}, modules = {ServiceModule.class})
public interface ServiceComponent {
    void inject(TrackerService trackerService);
}
