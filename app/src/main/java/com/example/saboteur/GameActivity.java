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

    private static final int MAX_PLAYERS = 10;

    private final String LOG_TAG = GameActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String username;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> icons = new ArrayList<>();
    ArrayList<ImageView> images = new ArrayList<>();
    ArrayList<TextView> texts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            images.add((ImageView) findViewById(getResources().getIdentifier("icon_" + i, "id", getPackageName())));
            texts.add((TextView) findViewById(getResources().getIdentifier("name_" + i, "id", getPackageName())));
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String roomCode = bundle.getString("roomCode");
        username = bundle.getString("username");
        assert roomCode != null;
        Log.d(LOG_TAG, roomCode);


        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    Log.d(LOG_TAG, String.valueOf(documentSnapshot.getDocument().getData()));
                    names.add(Objects.requireNonNull(documentSnapshot.getDocument().get("user")).toString());
                    icons.add(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getDocument().get("photo")).toString()));
                }
                fillPlayersNames();
            }
        });
    }

    private void fillPlayersNames() {
        Log.d(LOG_TAG, "size " + names.size());
        for (int i = 0; i < names.size(); i++) {
            Log.d(LOG_TAG, i + " " + icons.get(i) + " " + names.get(i));
            ImageView image = images.get(i);
            image.setImageResource(icons.get(i));

            TextView text = texts.get(i);
            text.setText(names.get(i));
        }
    }

    public void exitGame(View view) {
        // TODO Radu :)
    }
}
