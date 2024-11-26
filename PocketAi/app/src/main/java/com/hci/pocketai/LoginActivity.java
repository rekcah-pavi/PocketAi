package com.hci.pocketai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.hci.pocketai.helpers.CustomHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hci.pocketai.helpers.UserHelper;


public class LoginActivity extends AppCompatActivity {

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

        TextView signup = findViewById(R.id.signup_t);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish(); // close the current activity


            }
        });


        Button login_button = findViewById(R.id.login_btn);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    TextInputEditText emailbox = findViewById(R.id.email);
                    TextInputEditText passbox = findViewById(R.id.passbox);

                    String email = emailbox.getText().toString().trim();
                    String pass = passbox.getText().toString().trim();

                    if (email.isEmpty() || pass.isEmpty()) {
                        CustomHelper.showAlert(LoginActivity.this, "Failed", "Email or password cannot be empty");
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        CustomHelper.showAlert(LoginActivity.this, "Failed", "Invalid email address format");
                        return;
                    }

                    // Get reference to Firebase Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pjrt-app-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference reference = database.getReference("users");

                    // Use email to find the user in the database
                    String username = email.split("@")[0];  // assuming you store users based on email prefix

                    reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User found in the database
                                UserHelper user = dataSnapshot.getValue(UserHelper.class);

                                if (user != null && user.getPassword().equals(pass)) {
                                    // Password matches, login success
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                    // Save login state in SharedPreferences
                                    String namee = user.getName();

                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putString("email", email);
                                    editor.putString("name", namee);
                                    editor.apply();

                                    // Navigate to HomeActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // close the current activity
                                } else {
                                    // Incorrect password
                                    CustomHelper.showAlert(LoginActivity.this, "Failed", "Incorrect password");
                                }
                            } else {
                                // User not found
                                CustomHelper.showAlert(LoginActivity.this, "Failed", "User not found");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            CustomHelper.showAlert(LoginActivity.this, "Failed", "An error occurred: " + databaseError.getMessage());
                        }
                    });
                } catch (Exception e) {
                    CustomHelper.showAlert(LoginActivity.this, "Failed", "An error occurred: " + e.getMessage());
                }
            }
        });

    }
}