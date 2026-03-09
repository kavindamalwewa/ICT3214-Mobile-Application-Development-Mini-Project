package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcomeName, tvBMIValue, tvBMIStatus, tvCurrentDate;
    ImageView ivUserProfile;
    FloatingActionButton floatingActionButton;
    DatabaseHelper myDb;
    String userEmail;
    private LinearLayout llActivitiesList;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        llActivitiesList = findViewById(R.id.llActivitiesList);
        barChart = findViewById(R.id.barChart);

        // Set current system date
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(currentDate);

        myDb = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        if (userEmail != null) {
            loadUserDataAndCalculateBMI();
            loadActivitiesForToday();
            loadWeeklyActivityChart();
        } else {
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
        }

        ivUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
        });

        // Link to Add Activities page
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, add_activites.class);
            intent.putExtra("LOGGED_IN_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    private void loadUserDataAndCalculateBMI() {
        Cursor res = myDb.getUserDetails(userEmail);
        if (res != null && res.moveToFirst()) {
            String name = res.getString(0);
            String heightStr = res.getString(1);
            String weightStr = res.getString(2);
            tvWelcomeName.setText("Welcome, " + name + "!");
            try {
                float heightMeters = Float.parseFloat(heightStr) / 100;
                float weightKg = Float.parseFloat(weightStr);
                float bmi = weightKg / (heightMeters * heightMeters);
                tvBMIValue.setText(String.format("%.1f", bmi));
                if (bmi < 18.5) {
                    tvBMIStatus.setText("Underweight");
                    tvBMIStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                } else if (bmi >= 18.5 && bmi < 24.9) {
                    tvBMIStatus.setText("Normal Weight");
                    tvBMIStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else if (bmi >= 25 && bmi < 29.9) {
                    tvBMIStatus.setText("Overweight");
                    tvBMIStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    tvBMIStatus.setText("Obese");
                    tvBMIStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error calculating BMI. Check profile details.", Toast.LENGTH_SHORT).show();
            }
            res.close();
        }
    }

    private void loadActivitiesForToday() {
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        Cursor cursor = myDb.getActivitiesForDate(userEmail, currentDate);
        llActivitiesList.removeAllViews();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String activityName = cursor.getString(cursor.getColumnIndexOrThrow("ACTIVITY_NAME"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("DURATION"));
                TextView tv = new TextView(this);
                tv.setText(activityName + " - " + duration);
                tv.setTextSize(18);
                tv.setPadding(0, 8, 0, 8);
                llActivitiesList.addView(tv);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TextView tv = new TextView(this);
            tv.setText("No activities for today.");
            tv.setTextSize(16);
            tv.setPadding(0, 8, 0, 8);
            llActivitiesList.addView(tv);
        }
    }

    private void loadWeeklyActivityChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String userEmail = this.userEmail;
        DatabaseHelper db = myDb;
        String selectedDateStr = tvCurrentDate.getText().toString();
        Date selectedDate;
        try {
            selectedDate = sdf.parse(selectedDateStr);
        } catch (ParseException e) {
            selectedDate = new Date();
        }
        // Find Monday of the week for selectedDate
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(selectedDate);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int diffToMonday = (dayOfWeek == java.util.Calendar.SUNDAY) ? -6 : (java.util.Calendar.MONDAY - dayOfWeek);
        cal.add(java.util.Calendar.DAY_OF_MONTH, diffToMonday);
        // Loop for 7 days from Monday
        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            String dateStr = sdf.format(date);
            String label = labelFormat.format(date);
            Cursor cursor = db.getActivitiesForDate(userEmail, dateStr);
            int count = (cursor != null) ? cursor.getCount() : 0;
            entries.add(new BarEntry(i, count));
            labels.add(label);
            if (cursor != null) cursor.close();
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        BarDataSet dataSet = new BarDataSet(entries, "Activities per Day");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
        });
        barChart.invalidate();
    }
}
