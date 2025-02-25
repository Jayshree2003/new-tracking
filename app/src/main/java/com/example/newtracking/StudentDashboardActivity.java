package com.example.newtracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentDashboardActivity extends AppCompatActivity {
    BottomNavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        // Find Bottom Navigation View
        navigationView = findViewById(R.id.navbar);
        AppBarConfiguration appBarConfiguration=new AppBarConfiguration.Builder(R.layout.fragment_location_,R.layout.fragment_stopdetails_,R.layout.fragment_student_profile).build();

        // Initialize NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // Get NavController
            NavController navController = navHostFragment.getNavController();
           // navController.navigate(R.id.stop);
            // Set up Navigation with BottomNavigationView
            NavigationUI.setupWithNavController(navigationView, navController);
        } else {
            Log.e("StudentDashboardActivity", "NavHostFragment is NULL!");
            Toast.makeText(this, "Error: Navigation not found!", Toast.LENGTH_SHORT).show();
        }

        /*navigationView=findViewById(R.id.navbar);
        AppBarConfiguration appBarConfiguration=new AppBarConfiguration.Builder(R.layout.fragment_location_,R.layout.fragment_stopdetails_,R.layout.fragment_student_profile).build();
        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
       // NavController navController=navHostFragment.getNavController();
        // NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
           *//* navHostFragment = new NavHostFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, navHostFragment)
                    .setPrimaryNavigationFragment(navHostFragment)
                    .commitNow();*//*

            NavController navController = navHostFragment.getNavController();
        } else {
            Log.e("StudentDashboardActivity", "NavHostFragment is NULL!");
        }
      //  NavigationUI.setupWithNavController(navigationView,navController);*/



    }
}





