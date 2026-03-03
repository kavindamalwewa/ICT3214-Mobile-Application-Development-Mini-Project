package com.example.ict3214_mobile_application_development_mini_project;

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

public class loginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLoginSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //bind views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);

        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Email/Password validation check add karnna one...

                // Type karala thiyena akuru tika allaganna (String walata ganna)
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Email eka hari password eka hari his nam error ekak pennanna
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(loginActivity.this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
                } // Dekama gahala nam, database eken balanawa details harida kiyala
                else {
                    DatabaseHelper myDb = new DatabaseHelper(loginActivity.this);

                    // Email/Password DB eke thiyenawada check karanawa
                    boolean isValid = myDb.checkUser(email, password);

                    if (isValid) {
                        Toast.makeText(loginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // TODO: Methanadi thamai api app eke Home Screen ekata yanna Intent eka liyanne

                    } else {
                        Toast.makeText(loginActivity.this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}