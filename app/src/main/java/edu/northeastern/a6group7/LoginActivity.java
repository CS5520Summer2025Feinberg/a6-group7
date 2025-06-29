package edu.northeastern.a6group7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText userInput;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check SharedPreferences to see if user is already logged in
        SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
        String savedUser = sp.getString("username", "");

        if (!savedUser.equals("")) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }

        // Set login view
        setContentView(R.layout.activity_login);

        userInput = findViewById(R.id.usernameInput);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userInput.getText().toString().trim();

                if (!user.isEmpty()) {
                    // Save username locally
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", user);
                    editor.apply();

                    // Save username and data to Firebase RTDB
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    DatabaseReference currentUserRef = usersRef.child(user);

                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("joined", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date()));
                    userData.put("stickersSent", 0);
                    userData.put("stickersReceived", 0);

                    currentUserRef.setValue(userData);

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Enter username first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
