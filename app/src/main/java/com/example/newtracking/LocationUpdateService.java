package com.example.newtracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.util.Log;

public class LocationUpdateService extends Service {


    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference locationRef;
    private String driverId;
    private Handler handler = new Handler();

    private Runnable locationRunnable;


    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the driver ID dynamically
        driverId = getDriverId();

        if (driverId == null) {
            stopSelf(); // Stop service if no driver ID is found
            return;
        }

        locationRef = FirebaseDatabase.getInstance().getReference("Location").child(driverId).child("location");

        startForeground(1, createNotification());
        startLocationUpdates();
    }

    private String getDriverId() {
        // Method 1: Fetch driver ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("BusTrackingApp", MODE_PRIVATE);
        return prefs.getString("driver_id", null); // Returns null if not found
    }

    private Notification createNotification() {
        String channelId = "location_channel";

        // Check if Android version is 26+ before creating a NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Location Service", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Tracking Bus Location")
                .setContentText("Your location is being updated in real-time.")
                .setSmallIcon(R.drawable.baseline_directions_bus_24)
                .setPriority(NotificationCompat.PRIORITY_LOW) // For compatibility with lower versions
                .build();
    }


    private void startLocationUpdates() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            locationRef.child("latitude").setValue(location.getLatitude());
                            locationRef.child("longitude").setValue(location.getLongitude());
                        }
                    });

                } else {
                    Log.e("LocationUpdate", "Location permission not granted!");
                }

                handler.postDelayed(this, 5000); // Repeat every 5 seconds
            }
        };

        handler.post(locationRunnable);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Ensures service restarts if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(locationRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
