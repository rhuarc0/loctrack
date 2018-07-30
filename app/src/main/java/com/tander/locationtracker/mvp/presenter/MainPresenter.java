package com.tander.locationtracker.mvp.presenter;

import android.content.ServiceConnection;

import com.tander.locationtracker.mvp.view.MainActivity;
import com.tander.locationtracker.mvp.view.MyServiceConnection;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends AbstractPresenter {

    private MyServiceConnection serviceConnection;
    private MainActivity view;
    private long timeDiff;

    public MainPresenter(MyServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    public void checkTime() {
        Disposable disposable = Observable.fromCallable(() -> {
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    // Set timeout of 5 seconds
                    client.setDefaultTimeout(5_000);
                    client.connect("time.nist.gov");
                    return client.getDate();
                } finally {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new Date();
            }
        }).subscribeOn(Schedulers.newThread())
        .subscribe(date -> {
            this.timeDiff = new Date().getTime() - date.getTime();
            view.setTimeDiff(timeDiff);
        });
        subscriptions.add(disposable);
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void attachView(MainActivity activity) {
        view = activity;
    }

    public void detachView() {
        view = null;
        subscriptions.clear();
    }

}
