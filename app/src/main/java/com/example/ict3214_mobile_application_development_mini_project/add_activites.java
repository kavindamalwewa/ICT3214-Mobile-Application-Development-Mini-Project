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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private List<ActivityItem> activityList = new ArrayList<>();

    private final String[] ACTIVITIES = {"Walking", "Running", "Cycling", "Push Ups", "Squats", "Weightlifting", "Jumping Jacks", "Bicycle Crunch", "Bicep Curls", "Shoulder Press"};
    private final int[] ACTIVITY_COLORS = {
            Color.parseColor("#4ADE80"), Color.parseColor("#F87171"), Color.parseColor("#60A5FA"),
            Color.parseColor("#A78BFA"), Color.parseColor("#FBBF24"), Color.parseColor("#FB923C"),
            Color.parseColor("#F472B6"), Color.parseColor("#22D3EE"), Color.parseColor("#2DD4BF"),
            Color.parseColor("#94A3B8")
    };
    
    // Map to link activity names to their specific colors
    private Map<String, Integer> colorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_activites);
        
        // Handle system window insets (status bar/navigation bar) for a full-screen experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the color mapping for activities
        colorMap = new HashMap<>();
        for (int i = 0; i < ACTIVITIES.length; i++) {
            colorMap.put(ACTIVITIES[i], ACTIVITY_COLORS[i]);
        }

        db = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        btnBack = findViewById(R.id.btnBack);
        tvSystemDate = findViewById(R.id.tvSystemDate);
        btnAddNow = findViewById(R.id.btnAddNow);
        btnComplete = findViewById(R.id.btnComplete);
        cvActivityInput = findViewById(R.id.cvActivityInput);
        spinnerActivities = findViewById(R.id.spinnerActivities);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        btnConfirmActivity = findViewById(R.id.btnConfirmActivity);
        llActivitiesList = findViewById(R.id.llActivitiesList);

        btnBack.setOnClickListener(v -> finish());

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvSystemDate.setText(currentDate);
        tvSystemDate.setOnClickListener(v -> showDatePicker());

        // Initialize drop-down menus
        setupSpinners();

        // Load any existing activities already saved for the current date
        loadActivitiesFromDB(currentDate);

        btnAddNow.setOnClickListener(v -> cvActivityInput.setVisibility(View.VISIBLE));

        // Add selected activity to the temporary list and update the UI
        btnConfirmActivity.setOnClickListener(v -> {
            activityList.add(new ActivityItem(
                spinnerActivities.getSelectedItem().toString(), 
                spinnerDuration.getSelectedItem().toString()
            ));
            refreshListView();
            cvActivityInput.setVisibility(View.GONE);
        });

        // Save all activities in the list to the database and exit
        btnComplete.setOnClickListener(v -> {
            String date = tvSystemDate.getText().toString();
            
            // Clear existing records for this date to prevent duplicates
            db.deleteActivitiesForDate(userEmail, date);

            for (ActivityItem item : activityList) {
                db.insertActivity(userEmail, item.activity, item.duration, date);
            }
            
            Toast.makeText(this, "Activities saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar sel = Calendar.getInstance(); 
            sel.set(year, month, day);
            String d = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(sel.getTime());
            tvSystemDate.setText(d);
            
            // Refresh the list whenever the date changes
            loadActivitiesFromDB(d);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupSpinners() {
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ACTIVITIES);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivities.setAdapter(a);

        String[] ds = {"10 mins", "20 mins", "30 mins", "45 mins", "60 mins", "1.5 hours", "2 hours"};
        ArrayAdapter<String> da = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ds);
        da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(da);
    }

    private void loadActivitiesFromDB(String date) {
        activityList.clear();
        Cursor c = db.getActivitiesForDate(userEmail, date);
        if (c != null && c.moveToFirst()) {
            do {
                activityList.add(new ActivityItem(
                    c.getString(c.getColumnIndexOrThrow("ACTIVITY_NAME")), 
                    c.getString(c.getColumnIndexOrThrow("DURATION"))
                ));
            } while (c.moveToNext());
            c.close();
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
        // Create the Card container
        CardView card = new CardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 20);
        card.setLayoutParams(lp);
        card.setRadius(24f);
        card.setCardElevation(4f);

        // Main layout inside the card
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Colored vertical strip (Indicator)
        View strip = new View(this);
        strip.setLayoutParams(new LinearLayout.LayoutParams(16, ViewGroup.LayoutParams.MATCH_PARENT));
        strip.setBackgroundColor(colorMap.getOrDefault(item.activity, Color.GRAY));

        // Horizontal content container
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setPadding(40, 40, 40, 40);
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content.setGravity(Gravity.CENTER_VERTICAL);

        // Activity details (Name and Duration)
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(this); 
        name.setText(item.activity); 
        name.setTextSize(18); 
        name.setTextColor(Color.BLACK); 
        name.setTypeface(null, Typeface.BOLD);
        
        TextView dur = new TextView(this); 
        dur.setText(item.duration); 
        dur.setTextSize(14); 
        dur.setTextColor(Color.GRAY);
        
        info.addView(name); 
        info.addView(dur);

        ImageButton del = new ImageButton(this); 
        del.setImageResource(android.R.drawable.ic_menu_delete); 
        del.setBackgroundColor(Color.TRANSPARENT); 
        del.setColorFilter(Color.RED);
        del.setOnClickListener(v -> { 
            activityList.remove(index); 
            refreshListView(); 
        });

        // Assemble the card
        content.addView(info); 
        content.addView(del);
        mainLayout.addView(strip); 
        mainLayout.addView(content);
        card.addView(mainLayout);

        llActivitiesList.addView(card, 0);
    }

    private static class ActivityItem {
        String activity, duration;
        ActivityItem(String a, String d) { 
            this.activity = a; 
            this.duration = d; 
        }
    }
}
