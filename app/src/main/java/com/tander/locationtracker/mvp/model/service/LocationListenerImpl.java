package com.tander.locationtracker.mvp.model.service;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.tander.locationtracker.mvp.model.entity.Coordinates;
import com.tander.locationtracker.mvp.model.entity.CoordinatesProvider;
import com.tander.locationtracker.mvp.presenter.ServicePresenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LocationListenerImpl implements LocationListener {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private Location currentLocation = null;
    private ServicePresenter presenter;

    public LocationListenerImpl(ServicePresenter presenter) {
        this.presenter = presenter;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;

            CoordinatesProvider provider = location.getProvider().equals(LocationManager.GPS_PROVIDER)
                    ? CoordinatesProvider.GPS
                    : CoordinatesProvider.NETWORK;
            Coordinates coordinates = new Coordinates(location.getLatitude(),
                    location.getLongitude(),
                    provider,
                    location.getTime());
            presenter.processCoordinates(coordinates);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
