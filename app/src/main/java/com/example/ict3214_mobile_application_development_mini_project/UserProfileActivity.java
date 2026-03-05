package com.example.ict3214_mobile_application_development_mini_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    TextView tvProfileName, tvProfileEmail, tvProfileHeight, tvProfileWeight;
    DatabaseHelper myDb;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileHeight = findViewById(R.id.tvProfileHeight);
        tvProfileWeight = findViewById(R.id.tvProfileWeight);

        myDb = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        if (userEmail != null) {
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        Cursor res = myDb.getUserDetails(userEmail);

        if (res != null && res.moveToFirst()) {
            String name = res.getString(0);
            String height = res.getString(1);
            String weight = res.getString(2);

            tvProfileName.setText(name);
            tvProfileEmail.setText(userEmail);
            tvProfileHeight.setText("Height: " + height + " cm");
            tvProfileWeight.setText("Weight: " + weight + " kg");

            res.close();
        }
    }
}