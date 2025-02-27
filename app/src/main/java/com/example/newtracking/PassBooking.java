package com.example.newtracking;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newtracking.R;

import java.util.Calendar;

public class PassBooking extends AppCompatActivity {

    EditText editTextStartDate, editTextEndDate;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_booking);

        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        Button buttonBookPass = findViewById(R.id.buttonBookPass);

        calendar = Calendar.getInstance();

        // Open Date Picker when clicking Start Date
        editTextStartDate.setOnClickListener(view -> showDatePickerDialog(editTextStartDate));

        // Open Date Picker when clicking End Date
        editTextEndDate.setOnClickListener(view -> showDatePickerDialog(editTextEndDate));

        // Handle Book Pass button click
        buttonBookPass.setOnClickListener(view -> {
            // Here, you can implement Firebase or database integration for storing pass details
        });
    }

    private void showDatePickerDialog(EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PassBooking.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
