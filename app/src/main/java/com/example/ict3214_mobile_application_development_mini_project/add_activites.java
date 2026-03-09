package com.example.ict3214_mobile_application_development_mini_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class add_activites extends AppCompatActivity {

    private TextView tvSystemDate;
    private Button btnAddNow;
    private CardView cvActivityInput;
    private Spinner spinnerActivities, spinnerDuration;
    private ImageButton btnConfirmActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_activites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        tvSystemDate = findViewById(R.id.tvSystemDate);
        btnAddNow = findViewById(R.id.btnAddNow);
        cvActivityInput = findViewById(R.id.cvActivityInput);
        spinnerActivities = findViewById(R.id.spinnerActivities);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        btnConfirmActivity = findViewById(R.id.btnConfirmActivity);

        // Get current system date
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvSystemDate.setText(currentDate);

        // Setup Activities Spinner
        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Yoga", "Weightlifting", "Jumping Rope", "Hiking", "Dancing", "Pilates"};
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivities.setAdapter(activityAdapter);

        // Setup Duration Spinner
        String[] durations = {"10 mins", "20 mins", "30 mins", "45 mins", "60 mins", "1.5 hours", "2 hours"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        // Show input card when "Add Now" is clicked
        btnAddNow.setOnClickListener(v -> {
            cvActivityInput.setVisibility(View.VISIBLE);
        });

        // Confirm button click
        btnConfirmActivity.setOnClickListener(v -> {
            String selectedActivity = spinnerActivities.getSelectedItem().toString();
            String selectedDuration = spinnerDuration.getSelectedItem().toString();
            
            Toast.makeText(this, "Added: " + selectedActivity + " for " + selectedDuration, Toast.LENGTH_SHORT).show();
            
            // Hide the input card after adding
            cvActivityInput.setVisibility(View.GONE);
        });
    }
}