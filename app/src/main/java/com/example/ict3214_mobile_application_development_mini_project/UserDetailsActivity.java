package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserDetailsActivity extends AppCompatActivity {
    EditText etHeight, etWeight;
    Button btnComplete;
    String userEmail; // Kalin screen eken ena email eka daganna variable ekak

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //bind views
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnComplete = findViewById(R.id.btnComplete);

        // Kalin screen eken ewapu Email eka allagannawa
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = etHeight.getText().toString().trim();
                String weight = etWeight.getText().toString().trim();

                if (height.isEmpty() || weight.isEmpty()) {
                    Toast.makeText(UserDetailsActivity.this, "Please enter Height and Weight", Toast.LENGTH_SHORT).show();
                } else {
                    // Data tika update karanawa
                    DatabaseHelper myDb = new DatabaseHelper(UserDetailsActivity.this);
                    boolean isUpdated = myDb.updateUserDetails(userEmail, height, weight);

                    if (isUpdated) {
                        Toast.makeText(UserDetailsActivity.this, "Registration Fully Completed!", Toast.LENGTH_LONG).show();

                        // Okkoma iwarai, dan Login ekata yanawa
                        Intent intent = new Intent(UserDetailsActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}