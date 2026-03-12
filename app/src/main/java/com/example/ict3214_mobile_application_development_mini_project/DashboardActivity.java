package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    TextView tvBMIValue, tvBMIStatus, tvCurrentDate, tvNoActivities;
    ImageView ivUserProfile;
    CardView btnAddActivity, btnEditActivity;
    LinearLayout llActivitiesList;
    BarChart barChart;

    DatabaseHelper myDb;
    String userEmail;

    private final String[] ACTIVITIES = {"Walking", "Running", "Cycling", "Push Ups", "Squats", "Weightlifting", "Jumping Jacks", "Bicycle Crunch", "Bicep Curls", "Shoulder Press"};
    private final int[] ACTIVITY_COLORS = {
            Color.parseColor("#4ADE80"), Color.parseColor("#F87171"), Color.parseColor("#60A5FA"),
            Color.parseColor("#A78BFA"), Color.parseColor("#FBBF24"), Color.parseColor("#FB923C"),
            Color.parseColor("#F472B6"), Color.parseColor("#22D3EE"), Color.parseColor("#2DD4BF"),
            Color.parseColor("#94A3B8")
    };
    private Map<String, Integer> colorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Handle system bar insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize color mapping for UI list bullets
        colorMap = new HashMap<>();
        for (int i = 0; i < ACTIVITIES.length; i++) colorMap.put(ACTIVITIES[i], ACTIVITY_COLORS[i]);

        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvNoActivities = findViewById(R.id.tvNoActivities);
        btnAddActivity = findViewById(R.id.btnAddActivity);
        btnEditActivity = findViewById(R.id.btnEditActivity);
        llActivitiesList = findViewById(R.id.llActivitiesList);
        barChart = findViewById(R.id.barChart);

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(currentDate);

        myDb = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        ivUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
        });

        btnAddActivity.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, add_activites.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
        });

        btnEditActivity.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, add_activites.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh all data when returning to the dashboard
        if (userEmail != null) {
            loadUserDataAndCalculateBMI();
            loadActivitiesForToday();
            loadWeeklyActivityChart();
        } else {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserDataAndCalculateBMI() {
        Cursor res = myDb.getUserDetails(userEmail);
        if (res != null && res.moveToFirst()) {
            String heightStr = res.getString(res.getColumnIndexOrThrow("HEIGHT"));
            String weightStr = res.getString(res.getColumnIndexOrThrow("WEIGHT"));
            try {
                if (heightStr != null && weightStr != null) {
                    float heightMeters = Float.parseFloat(heightStr) / 100;
                    float weightKg = Float.parseFloat(weightStr);
                    if (heightMeters > 0) {
                        float bmi = weightKg / (heightMeters * heightMeters);
                        tvBMIValue.setText(String.format("%.1f", bmi));

                        if (bmi < 18.5) {
                            tvBMIStatus.setText("Underweight");
                            tvBMIStatus.setTextColor(Color.parseColor("#3B82F6"));
                        } else if (bmi < 24.9) {
                            tvBMIStatus.setText("Healthy Weight");
                            tvBMIStatus.setTextColor(Color.parseColor("#10B981"));
                        } else if (bmi < 29.9) {
                            tvBMIStatus.setText("Overweight");
                            tvBMIStatus.setTextColor(Color.parseColor("#F59E0B"));
                        } else {
                            tvBMIStatus.setText("Obese");
                            tvBMIStatus.setTextColor(Color.parseColor("#EF4444"));
                        }
                    }
                }
            } catch (Exception e) {
                tvBMIValue.setText("0.0");
                tvBMIStatus.setText("Complete Profile");
            }
            res.close();
        }
    }

    private void loadActivitiesForToday() {
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        Cursor cursor = myDb.getActivitiesForDate(userEmail, currentDate);
        llActivitiesList.removeAllViews();
        
        if (cursor != null && cursor.getCount() > 0) {
            tvNoActivities.setVisibility(View.GONE);
            llActivitiesList.setVisibility(View.VISIBLE);
            if (cursor.moveToFirst()) {
                do {
                    String activityName = cursor.getString(cursor.getColumnIndexOrThrow("ACTIVITY_NAME"));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow("DURATION"));
                    int color = colorMap.getOrDefault(activityName, Color.BLACK);
                    
                    // Create bullet point with activity-specific color
                    TextView tv = new TextView(this);
                    String bulletText = "● " + activityName + " (" + duration + ")";
                    SpannableString ss = new SpannableString(bulletText);
                    ss.setSpan(new ForegroundColorSpan(color), 0, 1, 0);
                    
                    tv.setText(ss);
                    tv.setTextColor(Color.parseColor("#1F2937"));
                    tv.setTextSize(16);
                    tv.setPadding(0, 12, 0, 12);
                    llActivitiesList.addView(tv);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            tvNoActivities.setVisibility(View.VISIBLE);
            llActivitiesList.setVisibility(View.GONE);
        }
    }

    private void loadWeeklyActivityChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        
        // Calculate the starting Monday of the current week
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int diffToMonday = (dayOfWeek == java.util.Calendar.SUNDAY) ? -6 : (java.util.Calendar.MONDAY - dayOfWeek);
        cal.add(java.util.Calendar.DAY_OF_MONTH, diffToMonday);
        
        // Fetch data for each day from Mon to Sun
        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            String dateStr = sdf.format(date);
            labels.add(labelFormat.format(date));
            Cursor cursor = myDb.getActivitiesForDate(userEmail, dateStr);
            
            float[] dayValues = new float[ACTIVITIES.length];
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("ACTIVITY_NAME"));
                    float hours = parseDurationToHours(cursor.getString(cursor.getColumnIndexOrThrow("DURATION")));
                    for (int j = 0; j < ACTIVITIES.length; j++) {
                        if (ACTIVITIES[j].equalsIgnoreCase(name)) { dayValues[j] += hours; break; }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            entries.add(new BarEntry(i, dayValues));
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        
        // Chart configuration
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ACTIVITY_COLORS);
        dataSet.setDrawValues(false);
        
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        
        // X-Axis (Days of week)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });

        // Y-Axis (Hours)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (value == 0) ? "" : String.valueOf((int) value);
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate(); // Refresh chart
    }

    private float parseDurationToHours(String duration) {
        if (duration == null) return 0f;
        String lower = duration.toLowerCase();
        try {
            if (lower.contains("hour")) return Float.parseFloat(lower.split(" ")[0]);
            if (lower.contains("min")) return Float.parseFloat(lower.split(" ")[0]) / 60f;
        } catch (Exception e) { return 0f; }
        return 0f;
    }
}
