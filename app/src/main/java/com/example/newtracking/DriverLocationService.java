package com.example.newtracking;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;

public class DriverLocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference driverRef,locationRef;
    private String driverId, busId, driverName,busName;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get driver ID

        if (driverId != null) {
            DatabaseReference driverRefDB = FirebaseDatabase.getInstance().getReference("Drivers").child(driverId);  // Use a temporary variable

            driverRefDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        busId = snapshot.child("busId").getValue(String.class);
                        String busName = snapshot.child("busName").getValue(String.class);
                        driverName = snapshot.child("name").getValue(String.class);

                        if (busId != null && busName != null && driverName != null) {
                            Log.d("DriverLocationService", "Bus ID: " + busId + ", Bus Name: " + busName + ", Driver: " + driverName);

                            // ✅ Update the class-level driverRef variable
                            driverRef = FirebaseDatabase.getInstance().getReference("Location").child(busId);

                            // Store driver and bus details in Firebase
                            driverRef.child("driverName").setValue(driverName);
                            driverRef.child("busName").setValue(busName);

                            // Start location updates
                            requestLocationUpdates();
                        } else {
                            Log.e("DriverLocationService", "Bus ID, Bus Name, or Driver Name is NULL.");
                            stopSelf();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DriverLocationService", "Failed to fetch driver data", error.toException());
                }
            });
        } else {
            Log.e("DriverLocationService", "Driver ID is null, stopping service.");
            stopSelf();
        }
        return START_STICKY;
    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("DriverLocationService", "Location permission not granted");
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (driverRef != null) {
                        // Store location under /Location/{busId}/location/
                        driverRef.child("location").child("latitude").setValue(location.getLatitude());
                        driverRef.child("location").child("longitude").setValue(location.getLongitude());

                        Log.d("DriverLocationService", "Updated location: " + location.getLatitude() + ", " + location.getLongitude());
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


