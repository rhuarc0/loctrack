package com.tander.locationtracker.mvp.model.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tander.locationtracker.App;
import com.tander.locationtracker.R;
import com.tander.locationtracker.mvp.model.repository.GpsBuffer;
import com.tander.locationtracker.mvp.model.repository.GpsSender;
import com.tander.locationtracker.mvp.presenter.ServicePresenter;
import com.tander.locationtracker.mvp.view.MainActivity;
import com.tander.locationtracker.room.AppDatabase;

import javax.inject.Inject;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    public static final int REQUEST_TIMING = 3000;

    private static final int DEFAULT_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ServiceTrackerChannelId";

    private LocationManager locationManager;

    @Inject
    LocationListener locationListener;

    @Inject
    ServicePresenter presenter;

    private long timeDiff;

    public static Intent getIntent(Context context) {
        return new Intent(context, TrackerService.class);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        injectDependencies();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQUEST_TIMING, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQUEST_TIMING, 0, locationListener);
        }
    }

    private void injectDependencies() {
        DaggerServiceComponent.builder()
                .appComponent(((App) getApplication()).getAppComponent())
                .serviceModule(new ServiceModule())
                .build()
                .inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        presenter.clear();
        locationListener = null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel(); // ToDo API 26. Probably need to delete the channel in onDestroy
        sendNotification("GpsTracker", "Отслеживание включено");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new ServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }

    private void sendNotification(String title, String text) {
        Intent notificationIntent = MainActivity.launchFromService(this);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentIntent(contentIntent)
                .setOngoing(true)   //invulnerable
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setBadgeIconType(Notification.BADGE_ICON_SMALL);
        }


        Notification notification;
        notification = builder.build();

        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel_name", importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public class ServiceBinder extends Binder {
        public long getTimeDiff() {
            return TrackerService.this.timeDiff;
        }

        public void setTimeDiff(long timeDiff) {
            TrackerService.this.timeDiff = timeDiff;
        }
    }
}
