package com.example.ict3214_mobile_application_development_mini_project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class add_activites extends AppCompatActivity {

    private TextView tvSystemDate;
    private Button btnAddNow, btnComplete;
    private ImageButton btnBack;
    private CardView cvActivityInput;
    private Spinner spinnerActivities, spinnerDuration;
    private ImageButton btnConfirmActivity;
    private LinearLayout llActivitiesList;
    private DatabaseHelper db;
    private String userEmail;

    // List to keep track of activities for the current UI session
    private List<ActivityItem> activityList = new ArrayList<>();

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

        db = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack);
        tvSystemDate = findViewById(R.id.tvSystemDate);
        btnAddNow = findViewById(R.id.btnAddNow);
        btnComplete = findViewById(R.id.btnComplete);
        cvActivityInput = findViewById(R.id.cvActivityInput);
        spinnerActivities = findViewById(R.id.spinnerActivities);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        btnConfirmActivity = findViewById(R.id.btnConfirmActivity);
        llActivitiesList = findViewById(R.id.llActivitiesList);

        // Back Button logic
        btnBack.setOnClickListener(v -> finish());

        // Set initial date
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvSystemDate.setText(currentDate);

        // Date Selection
        tvSystemDate.setOnClickListener(v -> showDatePicker());

        // Setup Spinners
        setupSpinners();

        // Load existing activities for the current date
        loadActivitiesFromDB(currentDate);

        // Show input card
        btnAddNow.setOnClickListener(v -> cvActivityInput.setVisibility(View.VISIBLE));

        // Confirm button click
        btnConfirmActivity.setOnClickListener(v -> {
            String activity = spinnerActivities.getSelectedItem().toString();
            String duration = spinnerDuration.getSelectedItem().toString();
            
            ActivityItem newItem = new ActivityItem(activity, duration);
            activityList.add(newItem);
            refreshListView();
            
            cvActivityInput.setVisibility(View.GONE);
            Toast.makeText(this, "Added to list", Toast.LENGTH_SHORT).show();
        });

        // Save and Finish
        btnComplete.setOnClickListener(v -> {
            String selectedDate = tvSystemDate.getText().toString();
            
            // Delete existing and save new list to ensure sync
            db.deleteActivitiesForDate(userEmail, selectedDate);
            for (ActivityItem item : activityList) {
                db.insertActivity(userEmail, item.activity, item.duration, selectedDate);
            }

            Toast.makeText(this, "Activities saved successfully!", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(add_activites.this, DashboardActivity.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selected.getTime());
            tvSystemDate.setText(dateStr);
            
            // Reload activities for the new date
            loadActivitiesFromDB(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void setupSpinners() {
        String[] activities = {"Walking", "Running", "Cycling", "Push Ups", "Squats", "Weightlifting", "Jumping Jacks", "Bicycle Crunch", "Bicep Curls", "Shoulder Press"};
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivities.setAdapter(activityAdapter);

        String[] durations = {"10 mins", "20 mins", "30 mins", "45 mins", "60 mins", "1.5 hours", "2 hours"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);
    }

    private void loadActivitiesFromDB(String date) {
        activityList.clear();
        Cursor cursor = db.getActivitiesForDate(userEmail, date);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("ACTIVITY_NAME"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("DURATION"));
                activityList.add(new ActivityItem(name, duration));
            } while (cursor.moveToNext());
            cursor.close();
        }
        refreshListView();
    }

    private void refreshListView() {
        llActivitiesList.removeAllViews();
        for (int i = 0; i < activityList.size(); i++) {
            addActivityCardToUI(activityList.get(i), i);
        }
    }

    private void addActivityCardToUI(ActivityItem item, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 16);
        card.setLayoutParams(lp);
        card.setRadius(16f);
        card.setCardElevation(4f);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        // Info
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(item.activity);
        tvName.setTextSize(18);
        tvName.setTextColor(Color.parseColor("#111827"));
        tvName.setTypeface(null, Typeface.BOLD);

        TextView tvDur = new TextView(this);
        tvDur.setText(item.duration);
        tvDur.setTextSize(14);
        tvDur.setTextColor(Color.parseColor("#6B7280"));

        infoLayout.addView(tvName);
        infoLayout.addView(tvDur);

        // Actions (Edit/Delete)
        ImageButton btnEdit = new ImageButton(this);
        btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
        btnEdit.setBackgroundColor(Color.TRANSPARENT);
        btnEdit.setOnClickListener(v -> {
            // Fill spinners with current item values
            setSpinnerValue(spinnerActivities, item.activity);
            setSpinnerValue(spinnerDuration, item.duration);
            cvActivityInput.setVisibility(View.VISIBLE);
            // Remove from list so it can be re-added or updated
            activityList.remove(index);
            refreshListView();
        });

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackgroundColor(Color.TRANSPARENT);
        btnDelete.setColorFilter(Color.RED);
        btnDelete.setOnClickListener(v -> {
            activityList.remove(index);
            refreshListView();
            Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
        });

        layout.addView(infoLayout);
        layout.addView(btnEdit);
        layout.addView(btnDelete);
        card.addView(layout);
        
        llActivitiesList.addView(card, 0);
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) spinner.setSelection(position);
    }

    private static class ActivityItem {
        String activity;
        String duration;

        ActivityItem(String activity, String duration) {
            this.activity = activity;
            this.duration = duration;
        }
    }
}
