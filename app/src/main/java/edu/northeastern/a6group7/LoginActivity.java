package edu.northeastern.a6group7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText userInput;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
        String savedUser = sp.getString("username", "");

        if (!savedUser.equals("")) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        userInput = findViewById(R.id.usernameInput);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userInput.getText().toString();

                if (user.length() > 0) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", user);
                    editor.commit();

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