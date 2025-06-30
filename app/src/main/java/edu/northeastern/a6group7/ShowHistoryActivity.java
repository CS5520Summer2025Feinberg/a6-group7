package edu.northeastern.a6group7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StickerHistoryAdapter adapter;
    private List<Sticker> stickerList = new ArrayList<>();
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_history);

        recyclerView = findViewById(R.id.stickerHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("AppData", MODE_PRIVATE);
        currentUser = sp.getString("username", "");
        currentUser = sp.getString("username", "");
        Toast.makeText(this, "Current user: " + currentUser, Toast.LENGTH_SHORT).show();

        stickerList.add(new Sticker("alice", "you", "sticker_heart", "2024-06-01T10:00:00Z"));
        stickerList.add(new Sticker("bob", "you", "sticker_star", "2024-06-02T12:30:00Z"));
        stickerList.add(new Sticker("carol", "you", "sticker_fire", "2024-06-03T14:45:00Z"));


        stickerList.add(new Sticker("you", "Mike", "sticker_fire", "2024-06-03T14:45:00Z"));

        stickerList.add(new Sticker("you", "Bob", "sticker_fire", "2024-06-03T14:45:00Z"));



        adapter = new StickerHistoryAdapter(this, stickerList, currentUser);
        recyclerView.setAdapter(adapter);

    }
}