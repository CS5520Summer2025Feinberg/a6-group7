package edu.northeastern.a6group7;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.annotation.SuppressLint; // ADDED: Import for @SuppressLint

public class NotificationActivity extends AppCompatActivity {

    private DatabaseReference receivedHistoryRef;
    private ChildEventListener childEventListener;
    private String currentUsername;

    private static final String CHANNEL_ID = "sticker_notifications_channel";
    private static final String CHANNEL_NAME = "Sticker Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for new stickers received";
    private static int notificationID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
        currentUsername = sp.getString("username", "");

        if (currentUsername.isEmpty()) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        receivedHistoryRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUsername)
                .child("stickersReceivedHistory");

        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (receivedHistoryRef != null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Sticker receivedSticker = snapshot.getValue(Sticker.class);
                    if (receivedSticker != null) {
                        Log.d("NotificationActivity", "New sticker detected: " + receivedSticker.getStickerId() + " from " + receivedSticker.getSender());
                        notificationID++;
                        showNotification(receivedSticker);
                    }
                }

                @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("NotificationActivity", "Firebase listener cancelled: " + error.getMessage());
                    Toast.makeText(NotificationActivity.this, "Failed to load stickers.", Toast.LENGTH_LONG).show();
                }
            };
            receivedHistoryRef.addChildEventListener(childEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receivedHistoryRef != null && childEventListener != null) {
            receivedHistoryRef.removeEventListener(childEventListener);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.channel_id),
                    name,
                    importance
            );
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission") // ADDED: This annotation suppresses the compile-time warning.
    private void showNotification(Sticker receivedSticker) {
        // WARNING: This code does NOT check for POST_NOTIFICATIONS permission at runtime.
        // On Android 13+ devices, your app WILL CRASH if the user has not manually granted
        // the Notifications permission in settings. This simplification is by your explicit request.

        notificationID++;

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        int stickerDrawableResId = getResources().getIdentifier(
                receivedSticker.getStickerId(),
                "drawable",
                getPackageName()
        );

        if (stickerDrawableResId == 0) {
            stickerDrawableResId = R.drawable.ic_launcher_foreground;
        }

        Bitmap stickerBitmap = BitmapFactory.decodeResource(getResources(), stickerDrawableResId);

        String channelId = getString(R.string.channel_id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Sticker!")
                .setContentText("You received a sticker from " + receivedSticker.getSender() + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (stickerBitmap != null) {
            builder.setLargeIcon(stickerBitmap);
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(stickerBitmap)
                    .setSummaryText("Tap to view sticker!"));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, builder.build());
    }
}