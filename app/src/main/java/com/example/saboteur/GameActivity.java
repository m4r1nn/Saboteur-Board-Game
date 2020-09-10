package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private final String LOG_TAG = GameActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String roomCode = bundle.getString("roomCode");
        username = bundle.getString("username");
        assert roomCode != null;
        Log.d(LOG_TAG, roomCode);
        boolean existIcons = bundle.getBoolean("existIcons");
        if (existIcons) {
            ArrayList<Uri> icons = bundle.getParcelableArrayList("icons");
            assert icons != null;
            ImageView image = new ImageView(this);
            image.setImageResource(getResources().getIdentifier(icons.get(0).toString(), "drawable", getPackageName()));
            image.setVisibility(View.VISIBLE);
        }

        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Log.d(LOG_TAG, Objects.requireNonNull(documentSnapshot.get("user")).toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Error", e);
            }
        });
    }
}