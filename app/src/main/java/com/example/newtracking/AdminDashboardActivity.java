package com.example.newtracking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private ListView driverListView;
    private Spinner busSpinner, routeSpinner;
    private DatabaseReference driverRef, busRef, routeRef;
    private ArrayAdapter<String> driverAdapter, busAdapter, routeAdapter;
    private ArrayList<String> driverList, busList, routeList;
    private HashMap<String, String> driverMap, busMap, routeMap;
    private String selectedDriverId, selectedBusId, selectedRouteId;
    private boolean isRouteAssigned; // To track if route is already assigned


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        driverListView = findViewById(R.id.driverListView);
        busSpinner = findViewById(R.id.busSpinner);
        routeSpinner = findViewById(R.id.routeSpinner);

        driverRef = FirebaseDatabase.getInstance().getReference("Drivers");
        busRef = FirebaseDatabase.getInstance().getReference("Buses");
        routeRef = FirebaseDatabase.getInstance().getReference("Routes");

        driverList = new ArrayList<>();
        busList = new ArrayList<>();
        routeList = new ArrayList<>();
        driverMap = new HashMap<>();
        busMap = new HashMap<>();
        routeMap = new HashMap<>();

        driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, driverList);
        driverListView.setAdapter(driverAdapter);

        busAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, busList);
        busAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, busList);
        busSpinner.setAdapter(busAdapter);

        routeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, routeList);
        routeSpinner.setAdapter(routeAdapter);

        Button openAddBusButton = findViewById(R.id.openAddBusButton);
        openAddBusButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddBusActivity.class);
            startActivity(intent);
        });

        FirebaseRouteManager.storeRoutesToFirebase();

        fetchDrivers();
        fetchAvailableBuses();
        fetchRoutes();

        driverListView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedDriverId = driverMap.get(driverList.get(position));
            checkIfRouteAssigned(selectedDriverId);
            Toast.makeText(AdminDashboardActivity.this, "Selected Driver: " + driverList.get(position), Toast.LENGTH_SHORT).show();
        });

        // Handle bus selection
        busSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBusId = busMap.get(busList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRouteId = routeMap.get(routeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAvailableBuses();
    }


    private void fetchDrivers() {
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverList.clear();
                driverMap.clear();
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    String driverId = driverSnapshot.getKey();
                    String driverName = driverSnapshot.child("name").getValue(String.class);

                    if (driverName != null) {
                        driverList.add(driverName);
                        driverMap.put(driverName, driverId);
                    }
                }
                driverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminDashboard", "Error fetching drivers", error.toException());
            }
        });
    }


    private void fetchAvailableBuses() {
        busRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                busList.clear();
                busMap.clear();
                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    String busId = busSnapshot.getKey();
                    String busName = busSnapshot.child("busName").getValue(String.class);

                    if (busName != null) {
                        busList.add(busName);
                        busMap.put(busName, busId);
                    }
                }
                runOnUiThread(() -> busAdapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminDashboard", "Error fetching buses", error.toException());
            }
        });
    }

    private void fetchRoutes() {
        routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeList.clear();
                routeMap.clear();
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    String routeId = routeSnapshot.getKey();
                    String routeName = routeSnapshot.child("routeName").getValue(String.class);

                    if (routeName != null) {
                        routeList.add(routeName);
                        routeMap.put(routeName, routeId);
                    }
                }
                runOnUiThread(() -> routeAdapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminDashboard", "Error fetching routes", error.toException());
            }
        });
    }

    private void checkIfRouteAssigned(String driverId) {
        driverRef.child(driverId).child("routeId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                isRouteAssigned = true;
            } else {
                isRouteAssigned = false;
            }
        });
    }

    public void assignRouteToDriver(View view) {
        if (selectedDriverId != null && selectedRouteId != null) {
            if (isRouteAssigned) {
                Toast.makeText(this, "Route already assigned to this driver!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> driverData = new HashMap<>();
            driverData.put("routeId", selectedRouteId);
            driverData.put("busId", "");

            driverRef.child(selectedDriverId).updateChildren(driverData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Route assigned successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to assign route!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select a driver and a route", Toast.LENGTH_SHORT).show();
        }
    }


    public void assignBusToDriver(View view) {
        if (selectedDriverId != null && selectedBusId != null) {
            // 🔍 Fetch the busName from Firebase before assigning
            busRef.child(selectedBusId).child("busName").get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String selectedBusName = snapshot.getValue(String.class);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("busId", selectedBusId);      // ✅ Store busId
                    updates.put("busName", selectedBusName); // ✅ Store busName

                    driverRef.child(selectedDriverId).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Bus Assigned Successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Log.e("AdminDashboard", "Failed to assign bus", e));
                } else {
                    Toast.makeText(this, "Bus name not found!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.e("Firebase", "Failed to fetch busName", e));
        } else {
            Toast.makeText(this, "Please select a driver and a bus", Toast.LENGTH_SHORT).show();
        }
    }

}