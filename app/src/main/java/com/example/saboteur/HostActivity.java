package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saboteur.utils.Sound;
import com.example.saboteur.utils.engine.cards.Card;
import com.example.saboteur.utils.engine.cards.Deck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class HostActivity extends AppCompatActivity {

    private final int MAX_PLAYERS = 10;
    private final String LOG_TAG = HostActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    private final String START_PATH = "start";
    private final String DECK_PATH = "deck";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView hostUserView;
    TextView codeRoomView;
    EditText usernameView;
    Button createRoomButton;
    Button playButton;
    private Sound buttonSound = null;
    private ListenerRegistration listener = null;

    private ArrayList<TextView> playerNames;
    private int playersCount = 0; // increment every time a player joins
    private boolean hostNameRemoved = false;

    private String roomCode;

    private ArrayList<Integer> icons;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));

        hostUserView = findViewById(R.id.room_code_view);
        usernameView = findViewById(R.id.host_text_view);
        createRoomButton = findViewById(R.id.host_button);
        codeRoomView = findViewById(R.id.room_code_text);
        playButton = findViewById(R.id.play_button);

        playerNames = new ArrayList<>();
        playerNames.add((TextView) findViewById(R.id.player1_view));
        playerNames.add((TextView) findViewById(R.id.player2_view));
        playerNames.add((TextView) findViewById(R.id.player3_view));
        playerNames.add((TextView) findViewById(R.id.player4_view));
        playerNames.add((TextView) findViewById(R.id.player5_view));
        playerNames.add((TextView) findViewById(R.id.player6_view));
        playerNames.add((TextView) findViewById(R.id.player7_view));
        playerNames.add((TextView) findViewById(R.id.player8_view));
        playerNames.add((TextView) findViewById(R.id.player9_view));
    }

    public static String generateRandomString(int length) {
        String data = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(data.charAt(random.nextInt(data.length())));
        }

        return sb.toString();
    }

    public void addHostToDB(String username, String codeRoom) {
        Map<String, Object> host_user = new HashMap<>();
        host_user.put("user", username);
        // create a room (document) for the host on the Cloud Firestore
        db.collection(DATABASE_NAME).document(codeRoom).collection(COLLECTION_NAME).add(host_user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(LOG_TAG, "Document written with id:" + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Exception occured", e);
            }
        });
    }

    public void waitForPlayers(String codeRoom) {
        // event listener to wait for the rest of the players. When there's a new player, data will come
        listener = db.collection(DATABASE_NAME).document(codeRoom).collection(COLLECTION_NAME).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(LOG_TAG, "Listen failed.", error);
                } else {
                    assert value != null;
                    for (DocumentChange document : value.getDocumentChanges()) {
                        Log.d(LOG_TAG, String.valueOf(document.getDocument().getData()));
                        // TODO : call the function to add player to vector
                        Map<String, Object> user = document.getDocument().getData();
                        if (!hostNameRemoved) {
                            hostNameRemoved = true;
                            return;
                        }
                        if (playersCount == MAX_PLAYERS - 1) {
                            Log.d(LOG_TAG, "No room");
                            return;
                        }
                        playerNames.get(playersCount++).setText(Objects.requireNonNull(user.get("user")).toString());
                    }
                }
            }
        });
    }

    public void createRoom(View view) {

        String username = usernameView.getText().toString();
        buttonSound.initSound();
        buttonSound.start();

        if (!username.equals("")) {
            hostUserView.setText(username);
            hostUserView.setVisibility(View.VISIBLE);
            createRoomButton.setVisibility(View.INVISIBLE);
            usernameView.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            int hostCode = 4;
            roomCode = generateRandomString(hostCode);
            codeRoomView.setText(roomCode.replaceAll(".(?!$)", "$0\n"));
            codeRoomView.setVisibility(View.VISIBLE);

            addHostToDB(username, roomCode);
            waitForPlayers(roomCode);
        }
    }

    private ArrayList<Integer> prepareIcons() {
        ArrayList<String> fileNames = new ArrayList<>();
        icons = new ArrayList<>();
        for (int i = 1; i <= 49; i++) {
            fileNames.add("icon_" + i);
        }
        Collections.shuffle(fileNames);
        for (int i = 0; i <= playersCount; i++) {
            Log.d(LOG_TAG, fileNames.get(i));
            icons.add(getResources().getIdentifier(fileNames.get(i), "drawable", getPackageName()));
        }
        return icons;
    }

    public Intent prepareIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("roomCode", codeRoomView.getText().toString().replaceAll("\n", ""));
        bundle.putString("username", usernameView.getText().toString());
        intent.putExtras(bundle);
        return intent;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addCardsToDb() {
        Deck deck = Deck.getInstance();
        List<String> names = playerNames.stream().filter((TextView tv) -> !tv.getText().toString().equals("")).collect(Collectors.toList()).stream().map(((TextView tv) -> tv.getText().toString())).collect(Collectors.toList());
        names.add(hostUserView.getText().toString());
        int numberCards;
        if (playersCount < 5) {
            numberCards = 6;
        } else if (playersCount < 7) {
            numberCards = 5;
        } else {
            numberCards = 4;
        }
        for (int i = 0; i < names.size(); i++) {
            ArrayList<String> cardTypes = new ArrayList<>();
            for (int j = 0; j < numberCards; j++) {
                Card c = deck.draw();
                if (c != null) {
                    cardTypes.add(c.getCard().getName());
                }
            }
            Map<String, Object> docData = new HashMap<>();
            docData.put("cards", cardTypes);
            db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).document(names.get(i)).set(docData);
        }


        Card c = deck.draw();
        while (c != null) {
            Map<String, Object> docData = new HashMap<>();
            docData.put("card", c.getCard().getName());
            db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).document("Available").collection("Cards").add(docData);
            c = deck.draw();
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("cards", deck.getEndCards());
        db.collection(DATABASE_NAME).document(roomCode).collection(DECK_PATH).document("Finish").set(docData);

    }

    public void addRolesToDB() {
        ArrayList<String> roles = new ArrayList<>();
        int saboteursCount = 1;
        if (playersCount > 3) {
            saboteursCount++;
        }
        if (playersCount > 5) {
            saboteursCount++;
        }
        if (playersCount > 8) {
            saboteursCount++;
        }
        for (int i = 0; i < saboteursCount; i++) {
            roles.add("Saboteur");
        }
        for (int i = saboteursCount; i <= playersCount; i++) {
            roles.add("Dwarf");
        }
        Collections.shuffle(roles);
        // TODO Radu --- trimite vectorul si fa ca fiecare jucator sa extraga cate o carte din el :)
    }

    public void playGame(View view) {
        buttonSound.initSound();
        buttonSound.start();
        Log.d(LOG_TAG, roomCode);
//        if (playersCount < 3) {
//            Toast.makeText(this, "Too few players", Toast.LENGTH_LONG).show();
//            return;
//        }
        listener.remove();
        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                icons = prepareIcons();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String docId = documentSnapshot.getId();
                    Log.d(LOG_TAG, String.valueOf(index));
                    db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).document(docId).update("photo", String.valueOf(icons.get(index++))).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(LOG_TAG, "Updated the photo in the database");
                        }
                    }).addOnFailureListener(e -> Log.d(LOG_TAG, String.format("Problem while updating the photo in the db for player %d", index), e));
                }

                Map <String, Object> start = new HashMap<>();
                start.put("start", 1);

                db.collection(DATABASE_NAME).document(roomCode).collection(START_PATH).add(start).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        // TODO : MAYBE A REFACTOR?
                        new Thread(HostActivity.this::addCardsToDb).start();
                        Map <String, Object> start = new HashMap<>();
                        start.put("start", 1);
                        db.collection(DATABASE_NAME).document(roomCode).collection(START_PATH).add(start).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.d(LOG_TAG, "players number: " + playersCount);
                                finish();
                                startActivity(prepareIntent(new Intent(HostActivity.this, GameActivity.class)));
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Problem while retrieving data from roomCode", e);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (usernameView.getVisibility() == View.INVISIBLE) {
            cancelHost();
        } else {
            super.onBackPressed();
        }
        buttonSound.initSound();
        buttonSound.start();
    }

    public void removeFromDB(final String roomCode) {
        db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Log.d(LOG_TAG, "Delete document with id:" + documentSnapshot.getId() + "\n") ;
                    db.collection(DATABASE_NAME).document(roomCode).collection(COLLECTION_NAME).document(documentSnapshot.getId()).delete();
                }
                Log.d(LOG_TAG, "empty db");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "fail empty db");
            }
        });
    }

    public void cancelHost() {
        for (TextView player : playerNames) {
            player.setText("");
        }
        playersCount = 0;
        hostNameRemoved = false;
        removeFromDB(codeRoomView.getText().toString().replaceAll("\n", ""));
        listener.remove();

        hostUserView.setVisibility(View.INVISIBLE);
        createRoomButton.setVisibility(View.VISIBLE);
        usernameView.setText("");
        usernameView.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        codeRoomView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        buttonSound.stopSound();
    }
}