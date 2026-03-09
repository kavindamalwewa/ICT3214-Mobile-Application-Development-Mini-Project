package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcomeName, tvBMIValue, tvBMIStatus, tvCurrentDate;
    ImageView ivUserProfile;
    FloatingActionButton floatingActionButton;
    DatabaseHelper myDb;
    String userEmail;

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

        // Set current system date
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(currentDate);

        myDb = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        if (userEmail != null) {
            loadUserDataAndCalculateBMI();
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
}
