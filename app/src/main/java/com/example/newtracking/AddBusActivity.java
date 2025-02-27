package com.example.newtracking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newtracking.model.Bus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBusActivity extends AppCompatActivity {

    private EditText busNameEditText;
    private Button addBusButton;
    private DatabaseReference busRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_bus);


        // Initialize Firebase
        busRef = FirebaseDatabase.getInstance().getReference("Buses");

        // Initialize UI components
        busNameEditText = findViewById(R.id.busNameEditText);

        addBusButton = findViewById(R.id.addBusButton);

        addBusButton.setOnClickListener(v -> addBusToFirebase());
    }

    private void addBusToFirebase() {
        String busName = busNameEditText.getText().toString().trim();



        if (busName.isEmpty() ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }



        // Generate a unique ID for the bus
        String busId = busRef.push().getKey();
        if (busId != null) {
            Bus newBus = new Bus(busId, busName);
            busRef.child(busId).setValue(newBus)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Bus added successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity after success
                        } else {
                            Toast.makeText(this, "Failed to add bus: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }}