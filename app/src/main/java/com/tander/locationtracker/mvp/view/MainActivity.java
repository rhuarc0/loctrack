package com.tander.locationtracker.mvp.view;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.annimon.stream.Stream;
import com.tander.locationtracker.App;
import com.tander.locationtracker.R;
import com.tander.locationtracker.mvp.model.entity.Coordinates;
import com.tander.locationtracker.mvp.model.service.TrackerService;
import com.tander.locationtracker.mvp.presenter.MainPresenter;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String FROM_SERVICE_KEY = "from_service";

    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.act_main_tv)
    TextView tvText;

    @BindView(R.id.act_main_tv_timediff)
    TextView tvTimeDiff;

    @BindView(R.id.content_main_container)
    ViewGroup vgContainer;

    @BindView(R.id.act_main_btn_toggle_service)
    ToggleImageButton toggleService;

    @Inject
    MainPresenter mainPresenter;

    public static Intent launchFromService(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.putExtra(FROM_SERVICE_KEY, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injectDependencies();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tvText.setText("None");

        mainPresenter.attachView(this);
        if (checkPermissions()) {
            enableButton();
        }
    }

    private void injectDependencies() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(App.get(this).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = TrackerService.getIntent(this);

        boolean fromService = getIntent().getBooleanExtra(FROM_SERVICE_KEY, false);
        bindService(intent, mainPresenter.getServiceConnection(), 0);
        if (fromService) {
            toggleService.setActive(true);
        }
        mainPresenter.checkTime();
    }

    public void setTimeDiff(long timeDiff) {
        Log.d(TAG, String.format("Current date difference = %d", timeDiff));
        tvTimeDiff.setText(String.format(Locale.getDefault(), "timeDiff = %d sec", timeDiff / 1000));
    }

    public void setNewCoord(Coordinates coordinates) {
        String text = Stream.of(coordinates)
                .reduce("", (str, coord) -> str += coord.toString() + "\n\n");
        tvText.setText(text);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mainPresenter.getServiceConnection());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.detachView();
    }

    @OnClick(R.id.act_main_tv)
    void onTextClick() {
        Disposable disposable = App.get(this).getAppComponent()
                .provideAppDatabase()
                .getCoordinatesDao()
                .fetchCoordinates()
                .subscribe(coordinates -> {
                    String text = Stream.of(coordinates)
                            .reduce("", (str, coord) -> str += coord.toString() + "\n");
                    tvText.setText(text);
                });
    }

    private void enableButton() {
        toggleService.setEnabled(true);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableButton();
            }
        }
    }

    @OnClick(R.id.act_main_btn_toggle_service)
    public void onToggleServiceClick(View view) {
        Intent intent = new Intent(this, TrackerService.class);
        if (toggleService.isActive()) {
            startService(intent);
            bindService(intent, mainPresenter.getServiceConnection(), 0);
        } else {
            getIntent().removeExtra(FROM_SERVICE_KEY);
            stopService(intent);
        }
    }
}
