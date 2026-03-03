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

public class signupActivity extends AppCompatActivity {
    //variables
    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button btnSignupSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //bind views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignupSubmit = findViewById(R.id.btnSignupSubmit);

        //register btn
        btnSignupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Type karapu details ganna
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                //Todo: Email/Password validation check add karnna one...

                // Kotu hisda kiyala balanna
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(signupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                // Password dekama samanada balanna
                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(signupActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
                // Okkoma hari nam database ekata save karanna
                else {
                    // DatabaseHelper eka adagahanawa
                    DatabaseHelper myDb = new DatabaseHelper(signupActivity.this);

                    // Data tika DB ekata insert karanawa
                    boolean isInserted = myDb.insertData(name, email, password);

                    if (isInserted) {
                        Toast.makeText(signupActivity.this, "Account Created successfully!", Toast.LENGTH_LONG).show();

                        // Kotu tika his karanawa
                        etName.setText("");
                        etEmail.setText("");
                        etPassword.setText("");
                        etConfirmPassword.setText("");

                        // Login ekata yanawa
                        Intent intent = new Intent(signupActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(signupActivity.this, "Error: Data not saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}