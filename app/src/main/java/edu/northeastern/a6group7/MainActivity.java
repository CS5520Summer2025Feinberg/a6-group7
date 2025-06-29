package edu.northeastern.a6group7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button aboutUs = findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        Button weatherBtn = findViewById(R.id.getWeather);
        weatherBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Weather.class);
            startActivity(intent);
        });

        Button sendStickerBtn = findViewById(R.id.sendStickerButton);
        sendStickerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SendStickerActivity.class);
            startActivity(intent);
        });

        // Test Firebase connection
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.child("testNode").setValue("Hello Firebase");

        Button viewStickersButton = findViewById(R.id.viewStickersButton);
        viewStickersButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }
}