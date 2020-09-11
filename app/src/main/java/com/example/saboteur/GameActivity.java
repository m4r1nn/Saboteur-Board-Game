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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
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
    ArrayList<String> names = new ArrayList<>();

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

//        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                    Log.d(LOG_TAG, String.valueOf(documentSnapshot.get("user")));
//                    Log.d(LOG_TAG, String.valueOf(documentSnapshot.get("photo")));
//                   // Log.d(LOG_TAG, Objects.requireNonNull(documentSnapshot.get("photo")).toString());
//                   // names.add(Objects.requireNonNull(documentSnapshot.get("user")).toString());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(LOG_TAG, "Error", e);
//            }
//        });

        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    Log.d(LOG_TAG, String.valueOf(documentSnapshot.getDocument().getData()));
                }
            }
        });

        ArrayList<Integer> icons = new ArrayList<>();

        LinearLayout name_layout_2 = findViewById(R.id.name_layout_2);

        ImageView image = new ImageView(this);
//        image.setImageResource(icons.get(0));
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        name_layout_2.addView(image);

        TextView text = new TextView(this);
        Log.d(LOG_TAG, String.valueOf(names.size()));
//            text.setText(names.get(0));
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        name_layout_2.addView(text);
    }
}