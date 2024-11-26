package com.hci.pocketai;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hci.pocketai.helpers.CustomHelper;
import com.hci.pocketai.helpers.UserHelper;

public class SignupActivity extends AppCompatActivity {

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

        TextView login = findViewById(R.id.login_t);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // close the current activity


            }
        });

        Button signup_button = findViewById(R.id.create_btn);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pjrt-app-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference myRef = database.getReference("message");


                    TextInputEditText namebox = findViewById(R.id.name2box);
                    TextInputEditText emailbox = findViewById(R.id.emailbox);
                    TextInputEditText passbox = findViewById(R.id.passbox);
                    TextInputEditText cpassbox = findViewById(R.id.cpassbox);

                    String name = namebox.getText().toString().trim();
                    String email = emailbox.getText().toString().trim();

                    String pass = passbox.getText().toString().trim();
                    String cpass = cpassbox.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
                        CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                        return;
                    }

                    if (!pass.equals(cpass)) {
                        CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                        return;
                    }

                    UserHelper user1 = new UserHelper(name, email, pass);

                    DatabaseReference reference = database.getReference("users");

                    String username = email.split("@")[0];

                    reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                            } else {



                                reference.child(username).setValue(user1);
                                Toast.makeText(SignupActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                        }
                    });



                } catch (Exception e) {
                    CustomHelper.showAlert(SignupActivity.this, "Failed", "Invalid email address");
                }



            }
        });







    }
}