package com.tander.locationtracker.mvp.presenter;

import io.reactivex.disposables.CompositeDisposable;

abstract class AbstractPresenter {
    protected CompositeDisposable subscriptions = new CompositeDisposable();

    public void clear() {
        subscriptions.clear();
    }
}
