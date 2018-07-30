package com.tander.locationtracker.mvp.view;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.tander.locationtracker.mvp.model.service.TrackerService;

public class MyServiceConnection implements ServiceConnection {
    private static final String TAG = MyServiceConnection.class.getSimpleName();

    private TrackerService.ServiceBinder binder;

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Log.d(TAG, "onServiceConnected");
        this.binder = (TrackerService.ServiceBinder) binder;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");

    }

    public TrackerService.ServiceBinder getBinder() {
        return binder;
    }
}
