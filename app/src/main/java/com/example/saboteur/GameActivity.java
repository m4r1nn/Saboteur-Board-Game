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
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public class GameActivity extends AppCompatActivity {

    private static final int MAX_PLAYERS = 10;
    private static final int MAX_CARDS = 6;
    private final String LOG_TAG = GameActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    private final String DECK_PATH = "deck";
    ArrayList<Integer> finishCardIds;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    volatile ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> icons = new ArrayList<>();
    ArrayList<ImageView> images = new ArrayList<>();
    ArrayList<TextView> texts = new ArrayList<>();
    ArrayList<ArrayList<ImageView>> cards = new ArrayList<>();
    ArrayList<Card> hand;
    ArrayList<ImageView> handView;
    ArrayList<String> blockTypes = new ArrayList<>();
    ArrayList<ImageView> pickaxes = new ArrayList<>();
    ArrayList<ImageView> lamps = new ArrayList<>();
    ArrayList<ImageView> carts = new ArrayList<>();
    private Sound buttonSound = null;
    private TextView roleText = null;
    private String username;
    private int moveCounter = 0;
    private String roomCode;
    volatile public ImageView selectedCard = null;
    volatile public int selectedCardIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));
        roleText = findViewById(R.id.role_text);
        Log.d(LOG_TAG, "on_create");



        synchronized (this) {
            setMapDimension();
            getPlayersInfo();
            buildMap();
            showCardNumber();
            getHand();
        }



        Log.d(LOG_TAG, "SIZE IN on create final" + names.size());

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

    synchronized private void getPlayersInfo() {
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            images.add(findViewById(getResources().getIdentifier("icon_" + i, "id", getPackageName())));
            texts.add(findViewById(getResources().getIdentifier("name_" + i, "id", getPackageName())));
            pickaxes.add(findViewById(getResources().getIdentifier("pickaxe_" + i, "id", getPackageName())));
            lamps.add(findViewById(getResources().getIdentifier("lamp_" + i, "id", getPackageName())));
            carts.add(findViewById(getResources().getIdentifier("cart_" + i, "id", getPackageName())));
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
            listenForMoves();
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
        texts.get(0).setTextColor(Color.YELLOW);
    }

    private void showHand() {
        for (int i = 0; i < hand.size(); i++) {
            setImageResourceAndTag(handView.get(i), Deck.getInstance().getType2Id().get(hand.get(i).getCard()));
        }
    }

    synchronized private void getHand() {
        Log.d(LOG_TAG, "SIZE IN get getHand" + names.size());
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
                assert role != null;
                roleText.setText(role);
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

    public boolean checkWin (int row, int column) {
        for (int i = 0; i < finishCardIds.size(); i++) {
            int x = 2 * i  + 1;
            int y = 9;
            if (finishCardIds.get(i) == R.drawable.card_end_win && x == row && y == column) {
                return true;
            }
        }
        return false;
    }


    public boolean checkCardConnectivity() {
        boolean [][] visited = new boolean[7][11];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 11; j++) {
                visited[i][j] = false;
            }
        }
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(3, 1));
        while (!q.isEmpty()) {
            Pair<Integer, Integer> top = q.peek();
            q.remove();
            assert top != null;
            int x = top.first;
            int y = top.second;
            Log.d(LOG_TAG, "" + x + " " + y);
            visited[x][y] = true;
            ImageView currentCard = cards.get(x).get(y);
            int imageId = (int) currentCard.getTag();
            if ((x == 1 || x == 3 ||  x == 5) && y == 9) {
                setImageResourceAndTag(cards.get(x).get(y), finishCardIds.get((x - 1) / 2));
                if (checkWin(x, y)) {
                    Toast.makeText(this, "Dwarves won!", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    // TODO : apeleaza functie care schimba cartea
                    if (cards.get(x - 1).get(y).getDrawable() != null && Deck.getInstance().getType2Id().inverse().get(cards.get(x - 1).get(y).getTag()) instanceof CardType.RoadType) {
                        if ((notConnected(cards.get(x - 1).get(y), currentCard, Directions.EAST) == 0)) {
                            if (imageId == R.drawable.card_end_turn_right) {
                                cards.get(x).get(y).setRotation(180);
                            }
                        }
                    }

                    if (cards.get(x + 1).get(y).getDrawable() != null && Deck.getInstance().getType2Id().inverse().get(cards.get(x + 1).get(y).getTag()) instanceof CardType.RoadType) {
                        if ((notConnected(cards.get(x + 1).get(y), currentCard, Directions.WEST) == 0)) {
                            if (imageId == R.drawable.card_end_turn_left) {
                                cards.get(x).get(y).setRotation(180);
                            }
                        }
                    }

                    Log.d(LOG_TAG, "inainte de if");
                    if (cards.get(x).get(y + 1).getDrawable() != null && Deck.getInstance().getType2Id().inverse().get(cards.get(x).get(y + 1).getTag()) instanceof CardType.RoadType) {
                        Log.d(LOG_TAG, "primu");
                        if ((notConnected(cards.get(x).get(y + 1), currentCard, Directions.SOUTH) == 0)) {
                            Log.d(LOG_TAG, "al doilea");
                            cards.get(x).get(y).setRotation(180);
                        }
                    }
                }
            }
            if (Deck.getInstance().getType2Id().inverse().get(imageId) instanceof CardType.RoadType || Deck.getInstance().getType2Id().inverse().get(imageId) instanceof CardType.StartType
                || Deck.getInstance().getType2Id().inverse().get(imageId) instanceof CardType.EndType) {
                if (x - 1 >= 0 && y >= 0 && x - 1 < 7 && y < 11 && cards.get(x - 1).get(y).getDrawable() != null && !visited[x - 1][y]
                        && notConnected(currentCard, cards.get(x - 1).get(y), Directions.WEST) == 0) {
                    q.add(new Pair<>(x - 1, y));
                }

                if (x + 1 >= 0 && y >= 0 && x + 1 < 7 && y < 11 && cards.get(x + 1).get(y).getDrawable() != null && !visited[x + 1][y]
                        && notConnected(currentCard, cards.get(x + 1).get(y), Directions.EAST) == 0) {
                    q.add(new Pair<>(x + 1, y));
                }

                if (x >= 0 && y - 1 >= 0 && x < 7 && y - 1 < 11 && cards.get(x).get(y - 1).getDrawable() != null && !visited[x][y - 1]
                        && notConnected(currentCard, cards.get(x).get(y - 1), Directions.SOUTH) == 0) {
                    q.add(new Pair<>(x, y - 1));
                }

                if (x >= 0 && y + 1 >= 0 && x  < 7 && y + 1 < 11 && cards.get(x).get(y + 1).getDrawable() != null && !visited[x][y + 1]
                        && notConnected(currentCard, cards.get(x).get(y + 1), Directions.NORTH) == 0) {
                    q.add(new Pair<>(x, y + 1));
                }
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "on_resume");
    }

    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonSound.initSound();
                        buttonSound.start();
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        finish();
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

    public void showFinishDialog(String winnerMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(winnerMessage)
                .setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonSound.initSound();
                        buttonSound.start();

                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonSound.initSound();
                        buttonSound.start();
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        finish();
                        dialogInterface.cancel();
                    }
                })
                .create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.bgd_dialog);
    }

    public boolean isPlayerTurn() {
        return username.equals(names.get(moveCounter % names.size()));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    synchronized public void selectCard(View view) {
        Log.d(LOG_TAG, "SIZE IN SELECTED CARD" + names.size());
        if (!isPlayerTurn()) {
            Toast.makeText(this, "Not your turn!", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView cardView = (ImageView) view;
        int oldSelectedId = -1;
        if (selectedCard != null && selectedCard.getDrawable() != null) {
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
        if (selectedCardIndex < hand.size() && selectedCardIndex != -1 && selectedCard.getDrawable() != null) {
            // check condition
            selectedCard.setBackgroundColor(Color.YELLOW);
            ImageView currentCardView = handView.get(selectedCardIndex);
            if (!(hand.get(selectedCardIndex).getCard() instanceof CardType.ActionType)) {
                if (currentCardView.getId() == oldSelectedId) {
                    hand.get(selectedCardIndex).changeRotation();
                    currentCardView.setRotation(((int) (currentCardView.getRotation() + 180)) % 360);
                }
            }
        }
        if (selectedCard.getDrawable() == null) {
            selectedCard = null;
            selectedCardIndex = -1;
        }
    }

    private boolean emptyHand() {
        for (ImageView image : handView) {
            if (image.getDrawable() != null) {
                return false;
            }
        }
        return true;
    }

    public void sendMoveToDb(int lin, int col, String type, boolean rotation, int index) {
        Log.d(LOG_TAG, "SIZE IN send" + names.size());
        Map<String, String> docData = new HashMap<>();
        docData.put("Line", String.valueOf(lin));
        docData.put("Column", String.valueOf(col));
        docData.put("Rotation", String.valueOf(rotation));
        docData.put("Type", type);
        docData.put("Index", String.valueOf(index));
        // code 1 - road/block road
        docData.put("Code", "1");
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").add(docData);
    }

    public void sendMoveToDb(int lin, int col, String code, int index) {
        Map<String, String> docData = new HashMap<>();
        docData.put("Line", String.valueOf(lin));
        docData.put("Column", String.valueOf(col));
        // code 2 - ACTION MAP
        // code 3 - AVALANCHE
        docData.put("Code", code);
        docData.put("Index", String.valueOf(index));
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").add(docData);
    }

    public void sendMoveToDb(String code, int index) {
        Map<String, String> docData = new HashMap<>();
        // code 4 - Burn card
        docData.put("Code", code);
        docData.put("Index", String.valueOf(index));
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").add(docData);
    }

    public void sendMoveToDb(String code, int index, int drawable, String user) {
        Map<String, String> docData = new HashMap<>();
        // code 5 - Block card
        // code 6 - Unblock card
        docData.put("Code", code);
        docData.put("Index", String.valueOf(index));
        docData.put("drawable",String.valueOf(drawable));
        docData.put("user", user);
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").add(docData);
    }

    public void sendMoveToDb(String code) {
        Map<String, String> docData = new HashMap<>();
        docData.put("Code", code);
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").add(docData);
    }

    private void doMove(Map<String, Object> info) {
        int code = Integer.parseInt((String) Objects.requireNonNull(info.get("Code")));
        int lin, col;
        String user;
        int drawable, index;
        Log.d(LOG_TAG, "SIZE IN get doMove3" + names.size());
        if (isPlayerTurn() && code != 0) {
            selectedCardIndex = Integer.parseInt((String) Objects.requireNonNull(info.get("Index")));
            selectedCard = handView.get(selectedCardIndex);
        }
        switch (code) {
            case 0:
                // saboteurs won!
                Toast.makeText(this, "Saboteurs won!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // build road
                lin = Integer.parseInt((String) Objects.requireNonNull(info.get("Line")));
                col = Integer.parseInt((String) Objects.requireNonNull(info.get("Column")));
                String type = (String) info.get("Type");
                boolean rotation = Boolean.parseBoolean((String) info.get("Rotation"));
                setImageResourceAndTag(cards.get(lin).get(col),
                        Deck.getInstance().getType2Id().get(Deck.getInstance().getType2String().inverse().get(type)));
                cards.get(lin).get(col).setRotation(rotation ? 180 : 0);
                if (checkCardConnectivity()) {
                    Log.d(LOG_TAG, "Finish");
                }
                break;
            case 2:
                // action map
                lin = Integer.parseInt((String) Objects.requireNonNull(info.get("Line")));
                col = Integer.parseInt((String) Objects.requireNonNull(info.get("Column")));
                if (!isPlayerTurn()) {
                    setImageResourceAndTag(cards.get(lin).get(col), R.drawable.card_action_map);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setImageResourceAndTag(cards.get(lin).get(col), R.drawable.card_back_end);
                        }
                    }, 5000);
                }
                break;
            case 3:
                // avalanche
                lin = Integer.parseInt((String) Objects.requireNonNull(info.get("Line")));
                col = Integer.parseInt((String) Objects.requireNonNull(info.get("Column")));
                cards.get(lin).get(col).setImageDrawable(null);
                cards.get(lin).get(col).setTag(null);
                break;
            case 4:
                if (!isPlayerTurn()) {
                    Toast.makeText(this, "Burn card", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                user = (String) Objects.requireNonNull(info.get("user"));
                drawable = Integer.parseInt((String) Objects.requireNonNull(info.get("drawable")));
                index = -1;
                for (int i = 0; i < texts.size(); i++) {
                    if (texts.get(i).getText().equals(user)) {
                        index = i;
                        break;
                    }
                }
                switch (drawable) {
                    case R.drawable.card_action_block_pickaxe:
                        setImageResourceAndTag(pickaxes.get(index), R.drawable.mini_pickaxe);
                        break;
                    case R.drawable.card_action_block_cart:
                        setImageResourceAndTag(carts.get(index), R.drawable.mini_cart);
                        break;
                    case R.drawable.card_action_block_lamp:
                        setImageResourceAndTag(lamps.get(index), R.drawable.mini_lamp);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + drawable);
                }
                break;
            case 6:
                user = (String) Objects.requireNonNull(info.get("user"));
                drawable = Integer.parseInt((String) Objects.requireNonNull(info.get("drawable")));
                index = -1;
                for (int i = 0; i < texts.size(); i++) {
                    if (texts.get(i).getText().equals(user)) {
                        index = i;
                        break;
                    }
                }
                switch (drawable) {
                    case R.drawable.card_action_unblock_pickaxe:
                        pickaxes.get(index).setImageDrawable(null);
                        pickaxes.get(index).setTag(null);
                        break;
                    case R.drawable.card_action_unblock_cart:
                        carts.get(index).setImageDrawable(null);
                        carts.get(index).setTag(null);
                        break;
                    case R.drawable.card_action_unblock_lamp:
                        lamps.get(index).setImageDrawable(null);
                        lamps.get(index).setTag(null);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + drawable);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + code);
        }

        if (isPlayerTurn()) {
            drawCardFromDeck();
        }
    }

    synchronized public void drawCardFromDeck() {
        TextView cardNumberText = findViewById(R.id.cardNumberText);
        if (Integer.parseInt(cardNumberText.getText().toString()) > 0) {
            db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).
                    document("Available").collection("Cards").
                    limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        Log.d(LOG_TAG, (String) Objects.requireNonNull(queryDocumentSnapshot.getData().get("card")));
                        db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).
                                document("Available").collection("Cards").
                                document(queryDocumentSnapshot.getId()).delete();
                        handView.get(selectedCardIndex).setRotation(0);
                        setImageResourceAndTag(handView.get(selectedCardIndex),
                                Objects.requireNonNull(Deck.getInstance().getType2Id().get(Deck.getInstance().getType2String().inverse().
                                        get(Objects.requireNonNull(queryDocumentSnapshot.getData().get("card"))))));
                        hand.set(selectedCardIndex, new Card(Deck.getInstance().getType2String().inverse().
                                get(Objects.requireNonNull(queryDocumentSnapshot.getData().get("card")))));
                        selectedCard.setBackgroundColor(Color.TRANSPARENT);
                        selectedCard = null;
                        selectedCardIndex = -1;
                    }
                }
            });
        } else {
            if (selectedCard != null) {
                selectedCard.setBackgroundColor(Color.TRANSPARENT);
                selectedCard.setImageDrawable(null);
                selectedCard.setTag(null);
                selectedCard = null;
                selectedCardIndex = -1;
            }
        }
    }

    synchronized private void listenForMoves() {
        Log.d(LOG_TAG, "SIZE IN get listen for move" + names.size());
        db.collection(DATABASE_NAME).document(roomCode).collection("moves").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            doMove(documentChange.getDocument().getData());
                            texts.get(moveCounter % names.size()).setTextColor(Color.BLACK);
                            moveCounter++;
                            texts.get(moveCounter % names.size()).setTextColor(Color.YELLOW);
                            if (isPlayerTurn() && emptyHand()) {
                                sendMoveToDb("0");
                            }
                        }
                    }
                });
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
        if (!isPlayerTurn()) {
            Toast.makeText(this, "Not your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCard == null) {
            Toast.makeText(this, "No card selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // if selected card is road
        if (!(hand.get(selectedCardIndex).getCard() instanceof CardType.ActionType)) {
            if (isBlocked()) {
                Toast.makeText(this, "You're blocked", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkPlace(lin, col)) {
                sendMoveToDb(lin, col, hand.get(selectedCardIndex).getCard().getName(), selectedCard.getRotation() != 0, selectedCardIndex);
            } else {
                Toast.makeText(this, "Invalid place", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // if selected card is action
        if (hand.get(selectedCardIndex).getCard() == CardType.ActionType.SpecialType.ACTION_MAP) {
            actionMapSelected(lin, col);
            return;
        }
        if (hand.get(selectedCardIndex).getCard() == CardType.ActionType.SpecialType.ACTION_AVALANCHE) {
            actionAvalancheSelected(lin, col);
        }
    }

    private void actionAvalancheSelected(int lin, int col) {
        ImageView cardView = cards.get(lin).get(col);
        if (cardView.getTag() == null || (Integer) cardView.getTag() == R.drawable.card_road_start
                || (Integer) cardView.getTag() == R.drawable.card_back_end) {
            Toast.makeText(this, "Invalid place", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMoveToDb(lin, col, "3", selectedCardIndex);
    }

    private void actionMapSelected(int lin, int col) {
        if (cards.get(lin).get(col).getTag() == null) {
            Toast.makeText(this, "Invalid place", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((Integer) cards.get(lin).get(col).getTag() == R.drawable.card_back_end) {
            Log.d(LOG_TAG, "selected back card");
            sendMoveToDb(lin, col, "2", selectedCardIndex);
            setImageResourceAndTag(cards.get(lin).get(col), finishCardIds.get((lin - 1) / 2));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setImageResourceAndTag(cards.get(lin).get(col), R.drawable.card_back_end);
                }
            }, 5000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean checkPlace(int lin, int col) {
        Deck deck = Deck.getInstance();
        ImageView card = cards.get(lin).get(col); // imageview selected from map (possibly empty)
//        Log.d(LOG_TAG, "id: " + card.getId());
        boolean rotated = hand.get(selectedCardIndex).getRotated();
        if (card.getDrawable() != null) {
            return false;
        }
        boolean roadConnection = false;
        if (lin != 0) {
            if (cards.get(lin - 1).get(col).getDrawable() != null) {
                int ok = notConnected(selectedCard, cards.get(lin - 1).get(col), Directions.WEST);
                if (ok == 1) {
                    return false;
                } else if (ok == 0) {
                    roadConnection = true;
                }
            }
        }
        if (lin != 6) {
            if (cards.get(lin + 1).get(col).getDrawable() != null) {
                int ok = notConnected(selectedCard, cards.get(lin + 1).get(col), Directions.EAST);
                if (ok == 1) {
                    return false;
                } else if (ok == 0) {
                    roadConnection = true;
                }
            }
        }
        if (col != 0) {
            if (cards.get(lin).get(col - 1).getDrawable() != null) {
                int ok = notConnected(selectedCard, cards.get(lin).get(col - 1), Directions.SOUTH);
                if (ok == 1) {
                    return false;
                } else if (ok == 0) {
                    roadConnection = true;
                }
            }
        }
        if (col != 10) {
            if (cards.get(lin).get(col + 1).getDrawable() != null) {
                int ok = notConnected(selectedCard, cards.get(lin).get(col + 1), Directions.NORTH);
                if (ok == 1) {
                    return false;
                } else if (ok == 0) {
                    roadConnection = true;
                }
            }
        }
        return roadConnection;
    }

    // verifica daca first se conecteaza cu second pe directia direction
    private int notConnected(ImageView first, ImageView second, Directions direction) {
        boolean rotated1, rotated2;
        rotated1 = first.getRotation() != 0;
        rotated2 = second.getRotation() != 0;
        Deck deck = Deck.getInstance();
        List<Directions> firstDirections = deck.getType2Id().inverse().get(first.getTag()).getCardDirections(rotated1);
        List<Directions> secondDirections = deck.getType2Id().inverse().get(second.getTag()).getCardDirections(rotated2);
        if (deck.getType2Id().inverse().get(second.getTag()) instanceof CardType.Back) {
            // TODO: flip connected FINISH CARDS + return code
            switch (direction) {
                case NORTH:
                    if (firstDirections.contains(Directions.NORTH)) {
                        return 0;
                    }
                    break;
                case SOUTH:
                    if (firstDirections.contains(Directions.SOUTH)) {
                        return 0;
                    }
                    break;
                case WEST:
                    if (firstDirections.contains(Directions.WEST)) {
                        return 0;
                    }
                    break;
                case EAST:
                    if (firstDirections.contains(Directions.EAST)) {
                        return 0;
                    }
                    break;
            }
            return 2;
        }
        int notRoad = 2;
        switch (direction) {
            case NORTH:
                if (firstDirections.contains(Directions.NORTH) && !secondDirections.contains(Directions.SOUTH)) {
                    return 1;
                }
                if (!firstDirections.contains(Directions.NORTH) && secondDirections.contains(Directions.SOUTH)) {
                    return 1;
                }
                if (firstDirections.contains(Directions.NORTH) && secondDirections.contains(Directions.SOUTH)) {
                    notRoad = 0;
                }
                break;
            case SOUTH:
                if (firstDirections.contains(Directions.SOUTH) && !secondDirections.contains(Directions.NORTH)) {
                    return 1;
                }
                if (!firstDirections.contains(Directions.SOUTH) && secondDirections.contains(Directions.NORTH)) {
                    return 1;
                }
                if (firstDirections.contains(Directions.SOUTH) && secondDirections.contains(Directions.NORTH)) {
                    notRoad = 0;
                }
                break;
            case WEST:
                if (firstDirections.contains(Directions.WEST) && !secondDirections.contains(Directions.EAST)) {
                    return 1;
                }
                if (!firstDirections.contains(Directions.WEST) && secondDirections.contains(Directions.EAST)) {
                    return 1;
                }
                if (firstDirections.contains(Directions.WEST) && secondDirections.contains(Directions.EAST)) {
                    notRoad = 0;
                }
                break;
            case EAST:
                if (firstDirections.contains(Directions.EAST) && !secondDirections.contains(Directions.WEST)) {
                    return 1;
                }
                if (!firstDirections.contains(Directions.EAST) && secondDirections.contains(Directions.WEST)) {
                    return 1;
                }
                if (firstDirections.contains(Directions.EAST) && secondDirections.contains(Directions.WEST)) {
                    notRoad = 0;
                }
                break;
        }
        return notRoad;
    }

    private void setImageResourceAndTag(ImageView imageView, int id) {
        imageView.setImageResource(id);
        imageView.setTag(id);
    }

    public void selectUser(View view) {
        TextView textView = (TextView) view;
        String user = textView.getText().toString();
        if (user.equals("")) {
            Toast.makeText(this, "No user selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isPlayerTurn()) {
            Toast.makeText(this, "Not your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        CardType selectedType = hand.get(selectedCardIndex).getCard();
        // type != (un)block
        if (!(selectedType instanceof CardType.ActionType) ||
                (selectedType instanceof CardType.ActionType.SpecialType)) {
            Toast.makeText(this, "Invalid place", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = -1;
        for (int i = 0; i < names.size(); ++i) {
            if (textView.getText().toString().equals(names.get(i))) {
                index = i;
                break;
            }
        }
        ImageView pickaxe = pickaxes.get(index);
        ImageView lamp = lamps.get(index);
        ImageView cart = carts.get(index);

        // type = block
        if (selectedType instanceof CardType.ActionType.BlockType) {
            CardType.ActionType.BlockType selectedBlock = (CardType.ActionType.BlockType) selectedType;
            switch (selectedBlock) {
                case ACTION_BLOCK_CART:
                    if (cart.getDrawable() != null) {
                        Toast.makeText(this, "Already blocked", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendMoveToDb("5", selectedCardIndex, R.drawable.card_action_block_cart, user);
                    break;
                case ACTION_BLOCK_LAMP:
                    if (lamp.getDrawable() != null) {
                        Toast.makeText(this, "Already blocked", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendMoveToDb("5", selectedCardIndex, R.drawable.card_action_block_lamp, user);
                    break;
                case ACTION_BLOCK_PICKAXE:
                    if (pickaxe.getDrawable() != null) {
                        Toast.makeText(this, "Already blocked", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendMoveToDb("5", selectedCardIndex, R.drawable.card_action_block_pickaxe, user);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedBlock);
            }
            return;
        }
        // type = unblock
        CardType.ActionType.UnblockType selectedUnblock = (CardType.ActionType.UnblockType) selectedType;
        switch (selectedUnblock) {
            case ACTION_UNBLOCK_CART:
                if (cart.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_cart, user);
                break;
            case ACTION_UNBLOCK_LAMP:
                if (lamp.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_lamp, user);
                break;
            case ACTION_UNBLOCK_PICKAXE:
                if (pickaxe.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_pickaxe, user);
                break;
            case ACTION_UNBLOCK_CART_LAMP:
                if (cart.getDrawable() == null && lamp.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cart.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_lamp, user);
                    return;
                }
                if (lamp.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_cart, user);
                    return;
                }
                showChoiceDialog("CART", "LAMP", user);
                break;
            case ACTION_UNBLOCK_PICKAXE_CART:
                if (pickaxe.getDrawable() == null && cart.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pickaxe.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_cart, user);
                    return;
                }
                if (cart.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_pickaxe, user);
                    return;
                }
                showChoiceDialog("PICKAXE", "CART", user);
                break;
            case ACTION_UNBLOCK_LAMP_PICKAXE:
                if (lamp.getDrawable() == null && pickaxe.getDrawable() == null) {
                    Toast.makeText(this, "Not blocked", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lamp.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_pickaxe, user);
                    return;
                }
                if (pickaxe.getDrawable() == null) {
                    sendMoveToDb("6", selectedCardIndex, R.drawable.card_action_unblock_lamp, user);
                    return;
                }
                showChoiceDialog("LAMP", "PICKAXE", user);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void showChoiceDialog(String option1, String option2, String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("")
                .setPositiveButton(option1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMoveToDb("6", selectedCardIndex, nameToId(option1), user);
                    }
                })
                .setNegativeButton(option2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMoveToDb("6", selectedCardIndex, nameToId(option2), user);
                    }
                })
                .create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.bgd_dialog);
    }

    int nameToId(String name) {
        switch (name) {
            case "PICKAXE":
                return R.drawable.card_action_unblock_pickaxe;
            case "CART":
                return R.drawable.card_action_unblock_cart;
            case "LAMP":
                return R.drawable.card_action_unblock_lamp;
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isBlocked() {
        int index = -1;
        for (int i = 0; i < texts.size(); i++) {
            if (username.equals(texts.get(i).getText().toString())) {
                index = i;
                break;
            }
        }
        Log.d(LOG_TAG, "pickaxe: " + pickaxes.get(index).getDrawable());
        Log.d(LOG_TAG, "cart: " + carts.get(index).getDrawable());
        Log.d(LOG_TAG, "lamp: " + lamps.get(index).getDrawable());
        if (pickaxes.get(index).getDrawable() != null) {
            return true;
        }
        if (lamps.get(index).getDrawable() != null) {
            return true;
        }
        if (carts.get(index).getDrawable() != null) {
            return true;
        }
        return false;
    }

    public void burnCard(View view) {
        if (!isPlayerTurn()) {
            Toast.makeText(this, "Not your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCard == null) {
            Toast.makeText(this, "No card selected", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMoveToDb("4", selectedCardIndex);
    }
}
