package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saboteur.utils.Sound;
import com.example.saboteur.utils.engine.cards.Card;
import com.example.saboteur.utils.engine.cards.CardType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private Sound buttonSound = null;
    private static final int MAX_PLAYERS = 10;

    private final String LOG_TAG = GameActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    private final String DECK_PATH = "deck";
    ArrayList<Integer> finishCardIds;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String username;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> icons = new ArrayList<>();
    ArrayList<ImageView> images = new ArrayList<>();
    ArrayList<TextView> texts = new ArrayList<>();
    ArrayList<ArrayList<ImageView>> cards = new ArrayList<>();
    ArrayList<Card> hand;
    private String roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));

        setMapDimension();

        getPlayersInfo();

        buildMap();

        showCardNumber();

        getHand();
    }

    private void setMapDimension() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        GridLayout mapLayout = findViewById(R.id.map_layout);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapLayout.getLayoutParams();
        params.height = size.y - 96;
        params.leftMargin = params.rightMargin = (size.x - 2 * (85 + 8) - params.height * 11 / 7) / 2;
        mapLayout.setLayoutParams(params);
    }

    private void getPlayersInfo() {
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            images.add((ImageView) findViewById(getResources().getIdentifier("icon_" + i, "id", getPackageName())));
            texts.add((TextView) findViewById(getResources().getIdentifier("name_" + i, "id", getPackageName())));
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        roomCode = bundle.getString("roomCode");
        username = bundle.getString("username");
        assert roomCode != null;
        Log.d(LOG_TAG, roomCode);


        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                Log.d(LOG_TAG, String.valueOf(documentSnapshot.getDocument().getData()));
                names.add(Objects.requireNonNull(documentSnapshot.getDocument().get("user")).toString());
                icons.add(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getDocument().get("photo")).toString()));
            }
            fillPlayersNames();
        });
    }

    private void buildMap() {
        for (int i = 0; i < 7; i++) {
            ArrayList<ImageView> temp = new ArrayList<>();
            for (int j = 0; j < 11; j++) {
                temp.add((ImageView) findViewById(getResources().getIdentifier("card_" + j + "_" + i, "id", getPackageName())));
            }
            cards.add(temp);
        }
        cards.get(3).get(1).setImageResource(R.drawable.card_road_start);
        // TODO transfer arraylist via database (same RANDOM anywhere)
        finishCardIds = new ArrayList<>();
        finishCardIds.add(R.drawable.card_end_turn_left);
        finishCardIds.add(R.drawable.card_end_turn_right);
        finishCardIds.add(R.drawable.card_end_win);
        Collections.shuffle(finishCardIds);
        cards.get(1).get(9).setImageResource(R.drawable.card_back_end);
        cards.get(3).get(9).setImageResource(R.drawable.card_back_end);
        cards.get(5).get(9).setImageResource(R.drawable.card_back_end);
    }

    private void showCardNumber() {
        TextView cardNumberText = findViewById(R.id.cardNumberText);
        db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).document("Available")
                .collection("Cards").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                cardNumberText.setText(String.valueOf(value.getDocuments().size()));
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

    private void getHand() {
        db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).
                document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(LOG_TAG, Objects.requireNonNull(documentSnapshot.get("cards")).toString());
                List<String> temp = (List<String>) documentSnapshot.get("cards");
                for (int i = 0; i < temp.size(); i++) {
                    // TODO map betweeen card type and string (name) and get and show hand
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "fail listen", e);
            }
        });
    }

    public void exitGame(View view) {
        // TODO Radu :)
        showExitDialog();
    }

    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonSound.initSound();
                        buttonSound.start();
                        finish();
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonSound.initSound();
                        buttonSound.start();
                        dialogInterface.cancel();
                    }
                })
                .create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.bgd_dialog);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
        buttonSound.initSound();
        buttonSound.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        buttonSound.stopSound();
    }
}
