package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saboteur.utils.Sound;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HostActivity extends AppCompatActivity {

    private final int MAX_PLAYERS = 10;
    private final String LOG_TAG = HostActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
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
            String codeRoom = generateRandomString(hostCode);
            codeRoomView.setText(codeRoom.replaceAll(".(?!$)", "$0\n"));
            codeRoomView.setVisibility(View.VISIBLE);

            addHostToDB(username, codeRoom);
            waitForPlayers(codeRoom);
        }
    }

    public Intent prepareIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("roomCode", codeRoomView.getText().toString().replaceAll("\n", ""));
        bundle.putString("username", usernameView.getText().toString());
        intent.putExtras(bundle);
        return intent;
    }

    public void playGame(View view) {
        buttonSound.initSound();
        buttonSound.start();
        // TODO send to other players message to start game
        startActivity(prepareIntent(new Intent(this, GameActivity.class)));
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