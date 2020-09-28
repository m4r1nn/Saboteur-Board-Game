package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saboteur.utils.Sound;
import com.example.saboteur.utils.engine.cards.Card;
import com.example.saboteur.utils.engine.cards.CardType;
import com.example.saboteur.utils.engine.cards.Deck;
import com.example.saboteur.utils.engine.cards.Directions;
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
import java.util.stream.Collectors;

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
    private int selectedCardIndex = -1;

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
        // hardcoded values, need revision
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
        setImageResourceAndTag(cards.get(3).get(1), R.drawable.card_road_start);
        setImageResourceAndTag(cards.get(1).get(9), R.drawable.card_back_end);
        setImageResourceAndTag(cards.get(3).get(9), R.drawable.card_back_end);
        setImageResourceAndTag(cards.get(5).get(9), R.drawable.card_back_end);

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
            setImageResourceAndTag(handView.get(i), Deck.getInstance().getType2Id().get(hand.get(i).getCard()));
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
        selectedCardIndex = -1;
        for (int i = 0; i < handView.size(); i++) {
            if (handView.get(i).getId() == cardView.getId()) {
                selectedCard = handView.get(i);
                selectedCardIndex = i;
                break;
            }
        }
        if (selectedCardIndex < cards.size()) {
            // check condition
            if (selectedCard != null) {
                selectedCard.setBackgroundColor(Color.YELLOW);
                ImageView currentCardView = handView.get(selectedCardIndex);
                if (currentCardView.getId() == oldSelectedId && !(hand.get(selectedCardIndex).getCard() instanceof CardType.ActionType)) {
                    hand.get(selectedCardIndex).changeRotation();
                    currentCardView.setRotation(((int) (currentCardView.getRotation() + 180)) % 360);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectMapPlace(View view) {
        ImageView cardView = (ImageView) view;
        String temp = getResources().getResourceEntryName(cardView.getId());
        String[] split = temp.split("_");
        int lin = Integer.parseInt(split[2]);
        int col = Integer.parseInt(split[1]);
        Log.d(LOG_TAG, "line: " + lin);
        Log.d(LOG_TAG, "column: " + col);
        if (canMakeMove()) {
            Log.d(LOG_TAG, "" + checkPlace(lin, col));
        } else {
            Toast.makeText(this, "Invalid move", Toast.LENGTH_LONG).show();
        }
    }

    // TODO verifica daca jucatorul poate face mutarea (e tura lui si a selectat deja o carte de drum)
    public boolean canMakeMove() {
        return selectedCard != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean checkPlace(int lin, int col) {
        Deck deck = Deck.getInstance();
        ImageView card = cards.get(lin).get(col); // imageview selected from map (possibly empty)
        Log.d(LOG_TAG, "id: " + card.getId());
        boolean rotated = hand.get(selectedCardIndex).getRotated();
        if (card.getDrawable() != null) {
            return false;
        }
        if (lin != 0) {
            if (cards.get(lin - 1).get(col).getDrawable() != null) {
//                Log.d(LOG_TAG, "hashmap: " + deck.getType2Id().inverse());
//                Log.d(LOG_TAG, "type: " + deck.getType2Id().inverse().get(selectedCard.getId()));
//                Log.d(LOG_TAG, "id: " + selectedCard.getId());
//                Log.d(LOG_TAG, "" + handView.stream().map(image -> image.getTag()).collect(Collectors.toList()));
                if (!hasConnection(selectedCard, cards.get(lin - 1).get(col), Directions.WEST, rotated)) {
                    return false;
                }
//                return hasConnection(selectedCard, cards.get(lin - 1).get(col), Directions.WEST);
            }
        }
        if (lin != 6) {
            if (cards.get(lin + 1).get(col).getDrawable() != null) {
                if (!hasConnection(selectedCard, cards.get(lin + 1).get(col), Directions.EAST, rotated)) {
                    return false;
                }
//                return hasConnection(selectedCard, cards.get(lin + 1).get(col), Directions.EAST);
            }
        }
        if (col != 0) {
            if (cards.get(lin).get(col - 1).getDrawable() != null) {
                if (!hasConnection(selectedCard, cards.get(lin).get(col - 1), Directions.SOUTH, rotated)) {
                    return false;
                }
//                return hasConnection(selectedCard, cards.get(lin).get(col - 1), Directions.SOUTH);
            }
        }
        if (col != 10) {
            if (cards.get(lin).get(col + 1).getDrawable() != null) {
                if (!hasConnection(selectedCard, cards.get(lin).get(col + 1), Directions.NORTH, rotated)) {
                    return false;
                }
//                return hasConnection(selectedCard, cards.get(lin).get(col + 1), Directions.NORTH);
            }
        }
        return true;
    }

    // verifica daca first se conecteaza cu second pe directia direction
    private boolean hasConnection(ImageView first, ImageView second, Directions direction, boolean rotated) {
        Deck deck = Deck.getInstance();
        List<Directions> firstDirections = deck.getType2Id().inverse().get(first.getTag()).getCardDirections(rotated);
        List<Directions> secondDirections = deck.getType2Id().inverse().get(second.getTag()).getCardDirections(false);
        switch (direction) {
            case NORTH:
                if (firstDirections.contains(Directions.NORTH) && !secondDirections.contains(Directions.SOUTH)) {
                    return false;
                }
                if (!firstDirections.contains(Directions.NORTH) && secondDirections.contains(Directions.SOUTH)) {
                    return false;
                }
                break;
            case SOUTH:
                if (firstDirections.contains(Directions.SOUTH) && !secondDirections.contains(Directions.NORTH)) {
                    return false;
                }
                if (!firstDirections.contains(Directions.SOUTH) && secondDirections.contains(Directions.NORTH)) {
                    return false;
                }
                break;
            case WEST:
                if (firstDirections.contains(Directions.WEST) && !secondDirections.contains(Directions.EAST)) {
                    return false;
                }
                if (!firstDirections.contains(Directions.WEST) && secondDirections.contains(Directions.EAST)) {
                    return false;
                }
                break;
            case EAST:
                if (firstDirections.contains(Directions.EAST) && !secondDirections.contains(Directions.WEST)) {
                    return false;
                }
                if (!firstDirections.contains(Directions.EAST) && secondDirections.contains(Directions.WEST)) {
                    return false;
                }
                break;
        }
        return true;
    }

    private void setImageResourceAndTag(ImageView imageView, int id) {
        imageView.setImageResource(id);
        imageView.setTag(id);
    }
}
