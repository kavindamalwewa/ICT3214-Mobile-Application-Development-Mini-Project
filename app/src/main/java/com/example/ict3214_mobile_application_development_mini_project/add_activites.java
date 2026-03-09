package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private Button btnAddNow, btnComplete;
    private CardView cvActivityInput;
    private Spinner spinnerActivities, spinnerDuration;
    private ImageButton btnConfirmActivity;
    private LinearLayout llActivitiesList;

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
        btnComplete = findViewById(R.id.btnComplete);
        cvActivityInput = findViewById(R.id.cvActivityInput);
        spinnerActivities = findViewById(R.id.spinnerActivities);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        btnConfirmActivity = findViewById(R.id.btnConfirmActivity);
        llActivitiesList = findViewById(R.id.llActivitiesList);

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

            addActivityToList(selectedActivity, selectedDuration);

            Toast.makeText(this, "Added: " + selectedActivity, Toast.LENGTH_SHORT).show();

            // Hide the input card after adding
            cvActivityInput.setVisibility(View.GONE);
        });

        // Complete button click
        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent(add_activites.this, DashboardActivity.class);
            // Pass user email if available
            String userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");
            if (userEmail != null) {
                intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            }
            startActivity(intent);
            finish();
        });
    }

    private void addActivityToList(String activity, String duration) {
        // Create a new CardView for each activity added
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(12f);
        cardView.setCardElevation(4f);
        cardView.setContentPadding(20, 20, 20, 20);

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView tvActivity = new TextView(this);
        tvActivity.setText(activity);
        tvActivity.setTextSize(18); // Default unit is SP
        tvActivity.setTypeface(null, Typeface.BOLD);
        tvActivity.setTextColor(Color.BLACK);
        tvActivity.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvDuration = new TextView(this);
        tvDuration.setText(duration);
        tvDuration.setTextSize(16); // Default unit is SP
        tvDuration.setTextColor(Color.GRAY);

        horizontalLayout.addView(tvActivity);
        horizontalLayout.addView(tvDuration);
        cardView.addView(horizontalLayout);

        llActivitiesList.addView(cardView, 0); // Add to top of the list
    }
}

