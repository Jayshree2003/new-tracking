package com.example.newtracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import android.Manifest;
//import android.util.Log;
//import android.widget.Toast;


public class DriverDashboardActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private DatabaseReference driverRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        // Start the location tracking service
//        Intent serviceIntent = new Intent(this, DriverLocationService.class);
//        serviceIntent.putExtra("busId", "bus_2");
//        startService(serviceIntent);

        // Initialize Firebase Authentication and Database Reference
        auth = FirebaseAuth.getInstance();
//        driverRef = FirebaseDatabase.getInstance().getReference("drivers").child(driverId).child("location");

        checkPermissions();

        // Fetch busId dynamically and start the location tracking service
        fetchBusIdAndStartTracking();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        databaseReference = FirebaseDatabase.getInstance().getReference("drivers").child("bus_1");
//
//        requestLocationUpdates();

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRef = FirebaseDatabase.getInstance().getReference("Locations");
//
//        // Start capturing the driver's location
//        startLocationUpdates();


    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchBusIdAndStartTracking();
            } else {
                Log.e("DriverDashboard", "Permission denied. Location tracking will not start.");
            }
        }
    }

    private void fetchBusIdAndStartTracking() {
        if (auth.getCurrentUser() != null) {
            String driverId = auth.getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("Drivers").child(driverId);

            driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String busId = snapshot.child("busId").getValue(String.class);
                        String busName = snapshot.child("busName").getValue(String.class);
                        String driverName = snapshot.child("name").getValue(String.class);

                        Log.d("DriverDashboard", "Fetched from DB -> Bus ID: " + busId + ", Bus Name: " + busName + ", Driver: " + driverName);

                        if (busId != null && busName != null && driverName != null) {
                            startLocationTrackingService(busId, busName, driverName);
                        } else {
                            Log.e("DriverDashboard", "Bus ID, Bus Name, or Driver Name is NULL in database.");
                        }
                    } else {
                        Log.e("DriverDashboard", "Snapshot does not exist for driverId: " + driverId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DriverDashboard", "Error fetching bus details", error.toException());
                }
            });
        }
    }





    private void startLocationTrackingService(String busId, String busName, String driverName) {
        Intent serviceIntent = new Intent(this, DriverLocationService.class);
        serviceIntent.putExtra("BUS_ID", busId);
        serviceIntent.putExtra("BUS_NAME", busName);
        serviceIntent.putExtra("DRIVER_NAME", driverName);
        startService(serviceIntent);
    }






}
