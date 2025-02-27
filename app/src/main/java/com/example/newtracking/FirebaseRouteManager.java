package com.example.newtracking;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRouteManager {


    private static final DatabaseReference routeRef = FirebaseDatabase.getInstance().getReference("Routes");

    public static void storeRoutesToFirebase() {
        Map<String, Object> routes = new HashMap<>();

        routes.put("route_1", createRoute("Jail Road", new Object[][]{
                {"K K Wagh", 20.01385348554099, 73.82234722629283},
                {"Nandur Naka", 19.9953766, 73.8442457},
                {"Dasak", 19.981, 73.8357},
                {"Panchak", 19.981, 73.8357},
                {"Sailanibaba Chowk", 19.97918578, 73.842561},
                {"Chhatrapati Shivaji Nagar Water Tank", 19.97499932, 73.84126962},
                {"Bhim Nagar", 19.96744151, 73.83967874},
                {"Mahajan Hospital", 19.96396372, 73.83881236},
                {"Bytoco Point", 19.97118985, 73.82937802},
                {"Anuradha Theatre Gandhi Putala", 19.9720942, 73.82688788},
                {"Sansri Naka", 19.90716616, 73.83275708}
        }));

        routes.put("route_2", createRoute("Nashik Road", new Object[][]{
                {"K K Wagh", 20.01385348554099, 73.82234722629283},
                {"Aurangabad Naka", 20.01055506072877, 73.81118151094934},
                {"Janardhan Swami Math", 20.008286527798095, 73.8161503534895},
                {"Jayshankar Garden", 19.994008617052568, 73.80893271660706},
                {"Kathe Galli", 19.990089665480344, 73.80421948211297},
                {"Siddharth Hotel", 19.98577813866339, 73.8020624079894},
                {"Pournima Bus Stop", 19.98900135168899, 73.79983251094879},
                {"Mhasoba Mandir", 19.944938868222476, 73.83555759438018},
                {"Bytco Point", 19.95394772879361, 73.83728169747873}
        }));

        routes.put("route_3", createRoute("Highway", new Object[][]{
                {"K K Wagh", 20.01385348554099, 73.82234722629283},
                {"Aurangabad Naka", 19.99454195337196, 73.7971397937546},
                {"Dwarka", 20.008286527798095, 73.8161503534895},
                {"Wadala Naka", 19.99256497028347, 73.7938802993031},
                {"Bombay Naka", 19.986833303911084, 73.78423262629207},
                {"Indira Nagar Jogging Track", 19.9770165992301, 73.7885657839906},
                {"Ashoka Business School", 19.9684197096639, 73.77004690909722},
                {"Rane Nagar", 19.96612798497889, 73.76698653793306},
                {"Pathardi Phata", 19.957655536812307, 73.75888221934693},
                {"Uttam Nagar", 19.963237680204738, 73.75383361097293},
                {"Atul Dairy", 19.965367450315888, 73.7536322532764},
                {"Ambad Police Station", 19.966041393192597, 73.76332447841048},
                {"Satyam Sweets", 19.966041393192597, 73.76332447841048}
        }));

        routeRef.setValue(routes).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Routes added successfully!");
            } else {
                Log.e("Firebase", "Failed to add routes!");
            }
        });
    }

    private static Map<String, Object> createRoute(String routeName, Object[][] stops) {
        Map<String, Object> route = new HashMap<>();
        route.put("routeName", routeName);

        Map<String, Object> stopsMap = new HashMap<>();
        for (int i = 0; i < stops.length; i++) {
            stopsMap.put("stop_" + (i + 1), createStop((String) stops[i][0], (double) stops[i][1], (double) stops[i][2]));
        }
        route.put("stops", stopsMap);
        return route;
    }

    private static Map<String, Object> createStop(String name, double lat, double lng) {
        Map<String, Object> stop = new HashMap<>();
        stop.put("name", name);
        stop.put("latitude", lat);
        stop.put("longitude", lng);
        return stop;
    }
}
