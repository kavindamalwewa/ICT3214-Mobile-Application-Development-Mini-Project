package com.example.ict3214_mobile_application_development_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class UserProfileActivity extends AppCompatActivity {

    TextView tvProfileName, tvProfileEmail, tvProfileHeight, tvProfileWeight;
    ImageButton btnBack;
    TextView btnLogout;
    MaterialButton btnEditProfile;
    DatabaseHelper myDb;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI elements
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileHeight = findViewById(R.id.tvProfileHeight);
        tvProfileWeight = findViewById(R.id.tvProfileWeight);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        myDb = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("LOGGED_IN_EMAIL");

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());

        // Logout functionality
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Edit Profile functionality
        btnEditProfile.setOnClickListener(v -> showEditDialog());

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
            tvProfileHeight.setText(height + " cm");
            tvProfileWeight.setText(weight + " kg");

            res.close();
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Health Details");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        EditText etHeight = view.findViewById(R.id.etEditHeight);
        EditText etWeight = view.findViewById(R.id.etEditWeight);

        // Pre-fill with current data
        String currentHeight = tvProfileHeight.getText().toString().replace(" cm", "");
        String currentWeight = tvProfileWeight.getText().toString().replace(" kg", "");
        etHeight.setText(currentHeight);
        etWeight.setText(currentWeight);

        builder.setView(view);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newHeight = etHeight.getText().toString().trim();
            String newWeight = etWeight.getText().toString().trim();

            if (!newHeight.isEmpty() && !newWeight.isEmpty()) {
                boolean isUpdated = myDb.updateUserDetails(userEmail, newHeight, newWeight);
                if (isUpdated) {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // Refresh the screen
                } else {
                    Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter both values", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
