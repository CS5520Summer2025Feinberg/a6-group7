package edu.northeastern.a6group7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SendStickerActivity extends AppCompatActivity {

    private EditText recipientInput;
    private Button sendButton;
    private String selectedSticker = "sticker_heart"; // default
    private String currentUser;
    private Sticker sticker;

    private ImageView heartSticker, starSticker, fireSticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker);

        recipientInput = findViewById(R.id.recipientInput);
        sendButton = findViewById(R.id.sendButton);
        heartSticker = findViewById(R.id.stickerHeart);
        starSticker = findViewById(R.id.stickerStar);
        fireSticker = findViewById(R.id.stickerFire);

        SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
        currentUser = sp.getString("username", "");

        // Sticker selection logic
        heartSticker.setOnClickListener(v -> {
            selectedSticker = "sticker_heart";
            highlightSelected(heartSticker);
        });

        starSticker.setOnClickListener(v -> {
            selectedSticker = "sticker_star";
            highlightSelected(starSticker);
        });

        fireSticker.setOnClickListener(v -> {
            selectedSticker = "sticker_fire";
            highlightSelected(fireSticker);
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientInput.getText().toString().trim();

                if (recipient.isEmpty()) {
                    Toast.makeText(SendStickerActivity.this, "Enter a recipient username", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendStickerToUser(currentUser, recipient, selectedSticker);
            }
        });
    }

    private void highlightSelected(ImageView selected) {
        // Optional: reset other backgrounds and highlight selected
        heartSticker.setBackgroundColor(0x00000000);
        starSticker.setBackgroundColor(0x00000000);
        fireSticker.setBackgroundColor(0x00000000);
        selected.setBackgroundColor(0xFFCCE5FF); // light blue
    }

    private void sendStickerToUser(String from, String to, String stickerId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");

        // Add to receiver's history
        DatabaseReference receiverRef = db.child(to).child("stickersReceived");
        String key = receiverRef.push().getKey();
        if (key == null) {
            Log.e("SendSticker", "Failed to generate key for sticker");
            return;
        }

        //create Sticker with current time
        String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
        sticker = new Sticker(from, to, stickerId, currentTime);

        receiverRef.child(key).setValue(sticker);

        // Update sender’s sent count
        DatabaseReference senderRef = db.child(from).child("stickersSentCount");
        senderRef.get().addOnSuccessListener(snapshot -> {
            long current = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            senderRef.setValue(current + 1);
            Toast.makeText(SendStickerActivity.this, "Sticker sent!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(SendStickerActivity.this, "Failed to send sticker", Toast.LENGTH_SHORT).show();
        });

        // Update received count
        DatabaseReference receivedCountRef = db.child(to).child("stickersReceivedCount");
        receivedCountRef.get().addOnSuccessListener(snapshot -> {
            long current = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            receivedCountRef.setValue(current + 1);
        }).addOnFailureListener(e -> {
            Toast.makeText(SendStickerActivity.this, "Failed to update received count", Toast.LENGTH_SHORT).show();
        });


    }
}
