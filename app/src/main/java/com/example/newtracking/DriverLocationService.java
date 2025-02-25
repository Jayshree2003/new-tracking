package com.example.newtracking;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;

public class DriverLocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference driverRef;
    private String busId;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        driverRef = FirebaseDatabase.getInstance().getReference("buses").child(busId);
//        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("busId")) {
            busId = intent.getStringExtra("busId");
            driverRef = FirebaseDatabase.getInstance().getReference("buses").child(busId);

            // Mark bus as active in Firebase
            driverRef.child("status").setValue("active");

            requestLocationUpdates();
        } else {
            Log.e("DriverLocationService", "Bus ID not provided, stopping service.");
            stopSelf();
        }
        return START_STICKY;
    }



    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("DriverLocationService", "Location permission not granted");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (busId != null) {
                        driverRef.child("latitude").setValue(location.getLatitude());
                        driverRef.child("longitude").setValue(location.getLongitude());
                        Log.d("DriverLocationService", "Updated location for " + busId + ": " + location.getLatitude() + ", " + location.getLongitude());
                    }
                }
            }
        }, null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

