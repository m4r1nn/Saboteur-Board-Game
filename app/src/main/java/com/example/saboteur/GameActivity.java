package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
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
import com.example.saboteur.utils.engine.cards.Deck;
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
    private TextView roleText = null;
    private static final int MAX_PLAYERS = 10;
    private static final int MAX_CARDS = 6;
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
    ArrayList<ImageView> handView;
    private String roomCode;
    private ImageView selectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));
        roleText = findViewById(R.id.role_text);

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
        for (int i = 0; i < names.size(); i++) {
            ImageView image = images.get(i);
            image.setImageResource(icons.get(i));

            TextView text = texts.get(i);
            text.setText(names.get(i));
        }
    }

    private void showHand() {
        for (int i = 0; i < hand.size(); i++) {
            handView.get(i).setImageResource(Deck.getInstance().getType2Id().get(hand.get(i).getCard()));
        }
    }

    private void getHand() {

        handView = new ArrayList<>();
        hand = new ArrayList<>();
        for (int i = 0; i < MAX_CARDS; i++) {
            handView.add((ImageView) findViewById(getResources().getIdentifier("card_in_hand_" + i, "id", getPackageName())));
        }

        db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).
                document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> temp = (List<String>) documentSnapshot.get("cards");
                String role = (String) documentSnapshot.get("role");
                roleText.setText(role);
                assert role != null;
                if (role.equals("Saboteur")) {
                    roleText.setTextColor(Color.RED);
                } else {
                    roleText.setTextColor(Color.BLUE);
                }
                assert temp != null;
                for (int i = 0; i < temp.size(); i++) {
                    hand.add(new Card(Deck.getInstance().getType2String().inverse().get(temp.get(i))));
                }
                showHand();

                temp = (List<String>) documentSnapshot.get("endCards");
                finishCardIds = new ArrayList<>();
                for (int i = 0; i < temp.size(); i++) {
                    finishCardIds.add(Deck.getInstance().getType2Id().get(Deck.getInstance().getType2String().inverse().get(temp.get(i))));
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
        // TODO               \              /
        // TODO                \           /
        // TODO Radu   >:()--------------<
        // TODO                /          \
        // TODO              /             \
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

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    public void selectCard(View view) {
        ImageView cardView = (ImageView) view;
        int oldSelectedId = -1;
        if (selectedCard != null) {
            selectedCard.setBackgroundColor(Color.TRANSPARENT);
            oldSelectedId = selectedCard.getId();
            selectedCard = null;
        }
        int index = -1;
        for (int i = 0; i < handView.size(); i++) {
            if (handView.get(i).getId() == cardView.getId()) {
                selectedCard = handView.get(i);
                index = i;
                break;
            }
        }
        if (index < cards.size()) {
            if (selectedCard != null) {
                selectedCard.setBackgroundColor(Color.YELLOW);
                ImageView currentCardView = handView.get(index);
                if (currentCardView.getId() == oldSelectedId && !(hand.get(index).getCard() instanceof CardType.ActionType)) {
                    hand.get(index).changeRotation();
                    currentCardView.setRotation(((int) (currentCardView.getRotation() + 180)) % 360);
                }
            }
        }
    }

    public void selectMapPlace(View view) {

    }
}
