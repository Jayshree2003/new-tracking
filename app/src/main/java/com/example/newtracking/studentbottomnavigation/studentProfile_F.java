package com.example.newtracking.studentbottomnavigation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newtracking.R;

import java.util.Calendar;

public class studentProfile_F extends Fragment {

    private EditText editTextStartDate, editTextEndDate;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize views
        editTextStartDate = view.findViewById(R.id.editTextStartDate);
        editTextEndDate = view.findViewById(R.id.editTextEndDate);
        Button buttonBookPass = view.findViewById(R.id.buttonBookPass);

        calendar = Calendar.getInstance();

        // Open Date Picker when clicking Start Date
        editTextStartDate.setOnClickListener(v -> showDatePickerDialog(editTextStartDate));

        // Open Date Picker when clicking End Date
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog(editTextEndDate));

        // Handle Book Pass button click
        buttonBookPass.setOnClickListener(v -> {
            // Implement Firebase or database integration for storing pass details here
        });
    }

    private void showDatePickerDialog(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
