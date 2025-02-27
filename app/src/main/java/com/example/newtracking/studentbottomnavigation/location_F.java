package com.example.newtracking.studentbottomnavigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newtracking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class location_F extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final String API_KEY = "5b3ce3597851110001cf6248b91bc8253a004511a3036a771a510f36";

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference studentRef, driverRef;

    private MapView mapView;
    private IMapController mapController;
    private Marker studentMarker, driverMarker;
    private Polyline routePolyline;

    private GeoPoint lastStudentLocation = null;
    private final Map<String, Marker> busMarkers = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        studentRef = FirebaseDatabase.getInstance().getReference("students").child("student_1");
        driverRef = FirebaseDatabase.getInstance().getReference("buses").child("bus_1");

        driverRef.keepSynced(true);

        requestLocationUpdates();

        // Initialize Map
        Configuration.getInstance().load(requireContext(), requireActivity().getSharedPreferences("osmdroid", 0));
        mapView = rootView.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Setup markers
        studentMarker = new Marker(mapView);
        studentMarker.setTitle("Student Location");
        studentMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_location_on_24));
        mapView.getOverlays().add(studentMarker);

        driverMarker = new Marker(mapView);
        driverMarker.setTitle("Driver Location");
        driverMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_directions_bus_24));
        mapView.getOverlays().add(driverMarker);

        // Setup route polyline
        routePolyline = new Polyline();
        routePolyline.setColor(Color.BLUE);
        routePolyline.setWidth(5);
        mapView.getOverlays().add(routePolyline);

        // Track locations
        trackStudentLocation();
        trackAllBuses();

        return rootView;
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (android.location.Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    studentRef.child("latitude").setValue(latitude);
                    studentRef.child("longitude").setValue(longitude);
                    Log.d("StudentDashboard", "Updated Student Location: " + latitude + ", " + longitude);
                }
            }
        }, null);
    }

    private void trackStudentLocation() {
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                    double lat = snapshot.child("latitude").getValue(Double.class);
                    double lng = snapshot.child("longitude").getValue(Double.class);
                    GeoPoint newLocation = new GeoPoint(lat, lng);

                    if (lastStudentLocation == null || newLocation.distanceToAsDouble(lastStudentLocation) > 5) {
                        lastStudentLocation = newLocation;
                        studentMarker.setPosition(newLocation);
                        mapController.setCenter(newLocation);
                        mapView.invalidate();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentDashboard", "Error fetching student location", error.toException());
            }
        });
    }

    private void trackAllBuses() {
        DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("Location");

        busesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> updatedBusIds = new HashSet<>();

                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    String busId = busSnapshot.getKey();
                    String busName = busSnapshot.child("busName").getValue(String.class);

                    if (busSnapshot.child("location").exists()) {
                        Double lat = busSnapshot.child("location/latitude").getValue(Double.class);
                        Double lng = busSnapshot.child("location/longitude").getValue(Double.class);

                        if (lat != null && lng != null) {
                            updatedBusIds.add(busId);

                            if (busMarkers.containsKey(busId)) {
                                Marker existingMarker = busMarkers.get(busId);
                                existingMarker.setPosition(new GeoPoint(lat, lng));
                            } else {
                                Marker newMarker = new Marker(mapView);
                                newMarker.setPosition(new GeoPoint(lat, lng));
                                newMarker.setTitle("Bus: " + busName);
                                newMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_directions_bus_24));

                                busMarkers.put(busId, newMarker);
                                mapView.getOverlays().add(newMarker);
                            }
                        }
                    }
                }

                // Remove old markers
                Iterator<Map.Entry<String, Marker>> iterator = busMarkers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Marker> entry = iterator.next();
                    if (!updatedBusIds.contains(entry.getKey())) {
                        mapView.getOverlays().remove(entry.getValue());
                        iterator.remove();
                    }
                }

                mapView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentDashboard", "Error fetching buses location", error.toException());
            }
        });
    }
}
/*
package com.example.newtracking.studentbottomnavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.newtracking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class location_F extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final String API_KEY = "5b3ce3597851110001cf6248b91bc8253a004511a3036a771a510f36";

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference studentRef, driverRef;

    private MapView mapView;
    private IMapController mapController;
    private Marker studentMarker, driverMarker;
    private Polyline routePolyline;
    private GeoPoint studentLocation, driverLocation;
    private final Map<String, Marker> busMarkers = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_, container, false);

        // Initialize Firebase References
        studentRef = FirebaseDatabase.getInstance().getReference("students").child("student_1");
        driverRef = FirebaseDatabase.getInstance().getReference("buses").child("bus_1");

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        requestLocationUpdates();

        // Initialize Map
        Configuration.getInstance().load(requireContext(), requireActivity().getSharedPreferences("osmdroid", 0));
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Setup Student Marker
        studentMarker = new Marker(mapView);
        studentMarker.setTitle("Student Location");
        studentMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_location_on_24));
        mapView.getOverlays().add(studentMarker);

        // Setup Driver Marker
        driverMarker = new Marker(mapView);
        driverMarker.setTitle("Driver Location");
        driverMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_directions_bus_24));
        mapView.getOverlays().add(driverMarker);

        // Setup Route Polyline
        routePolyline = new Polyline();
        routePolyline.setColor(Color.BLUE);
        routePolyline.setWidth(5);
        mapView.getOverlays().add(routePolyline);

        // Track locations
        trackStudentLocation();
        trackAllBuses();

        return view;
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (android.location.Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    studentRef.child("latitude").setValue(latitude);
                    studentRef.child("longitude").setValue(longitude);
                    Log.d("StudentDashboard", "Updated Student Location: " + latitude + ", " + longitude);
                }
            }
        }, null);
    }
//confused
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        }
    }

    private GeoPoint lastStudentLocation = null;

    private void trackStudentLocation() {
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                    double lat = snapshot.child("latitude").getValue(Double.class);
                    double lng = snapshot.child("longitude").getValue(Double.class);
                    GeoPoint newLocation = new GeoPoint(lat, lng);

                    if (lastStudentLocation == null || newLocation.distanceToAsDouble(lastStudentLocation) > 5) {
                        lastStudentLocation = newLocation;
                        studentMarker.setPosition(newLocation);
                        mapController.setCenter(newLocation);
                        mapView.invalidate();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentDashboard", "Error fetching student location", error.toException());
            }
        });
    }

    private void trackAllBuses() {
        DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("buses");

        busesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> updatedBusIds = new HashSet<>();

                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    if (busSnapshot.child("latitude").exists() && busSnapshot.child("longitude").exists()) {
                        double lat = busSnapshot.child("latitude").getValue(Double.class);
                        double lng = busSnapshot.child("longitude").getValue(Double.class);
                        String busId = busSnapshot.getKey();

                        updatedBusIds.add(busId);

                        if (busMarkers.containsKey(busId)) {
                            Marker existingMarker = busMarkers.get(busId);
                            existingMarker.setPosition(new GeoPoint(lat, lng));
                        } else {
                            Marker newMarker = new Marker(mapView);
                            newMarker.setPosition(new GeoPoint(lat, lng));
                            newMarker.setTitle("Bus: " + busId);
                            newMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_directions_bus_24));

                            busMarkers.put(busId, newMarker);
                            mapView.getOverlays().add(newMarker);
                        }
                    }
                }

                Iterator<Map.Entry<String, Marker>> iterator = busMarkers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Marker> entry = iterator.next();
                    if (!updatedBusIds.contains(entry.getKey())) {
                        mapView.getOverlays().remove(entry.getValue());
                        iterator.remove();
                    }
                }

                mapView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentDashboard", "Error fetching buses location");
            }
        });
    }
}
*/
