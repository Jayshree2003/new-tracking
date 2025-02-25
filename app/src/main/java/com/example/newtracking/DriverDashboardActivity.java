package com.example.newtracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import android.Manifest;
//import android.util.Log;
//import android.widget.Toast;


public class DriverDashboardActivity extends AppCompatActivity {
//    private FirebaseAuth auth;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE=1;
//    private DatabaseReference locationRef;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;

//    private static final int LOCATION_PERMISSION_REQUEST = 100;
//    private FusedLocationProviderClient fusedLocationClient;
//    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        // Start the location tracking service
        Intent serviceIntent = new Intent(this, DriverLocationService.class);
        serviceIntent.putExtra("busId", "bus_2");
        startService(serviceIntent);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        databaseReference = FirebaseDatabase.getInstance().getReference("drivers").child("bus_1");
//
//        requestLocationUpdates();

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRef = FirebaseDatabase.getInstance().getReference("Locations");
//
//        // Start capturing the driver's location
//        startLocationUpdates();
//        checkPermissions();

    }

//    private void requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
//            return;
//        }
//
//        LocationRequest locationRequest = LocationRequest.create()
//                .setInterval(5000)  // Update every 5 seconds
//                .setFastestInterval(2000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//
//                for (Location location : locationResult.getLocations()) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//
//                    Log.d("BusTracker", "Location: " + latitude + ", " + longitude);
//
//                    // Update Firebase with new location
//                    databaseReference.child("latitude").setValue(latitude);
//                    databaseReference.child("longitude").setValue(longitude);
//                }
//            }
//        };
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//    }


//    private void checkPermissions() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }

//    private void startLocationUpdates() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location
//                            if (location != null) {
//                                Toast.makeText(DriverDashboardActivity.this, "Location Fetched", Toast.LENGTH_SHORT).show();
//                                saveLocationToFirebase(location.getLatitude(), location.getLongitude());
//                            } else {
//                                Toast.makeText(DriverDashboardActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        } else {
//            // Request permissions if not granted
//            ActivityCompat.requestPermissions(DriverDashboardActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    1);
//        }
//    }

//    private void saveLocationToFirebase(double latitude, double longitude) {
//        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        LocationData locationData = new LocationData(latitude, longitude);
//        locationRef.child(driverId).setValue(locationData)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(DriverDashboardActivity.this, "Location saved!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(DriverDashboardActivity.this, "Failed to save location.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//

}
