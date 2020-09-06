package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class HostActivity extends AppCompatActivity {

    private final int HostCode = 4;
    private final String LOG_TAG = HostActivity.class.getSimpleName();
    private final String DATABASE_NAME = "users";
    private final String COLLECTION_NAME = "test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView hostUserView;
    TextView codeRoomView;
    EditText usernameView;
    Button createRoomButton;
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        hostUserView = findViewById(R.id.room_code_view);
        usernameView = findViewById(R.id.host_text_view);
        createRoomButton = findViewById(R.id.host_button);
        codeRoomView = findViewById(R.id.room_code_text);
        playButton = findViewById(R.id.play_button);
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
        db.collection(DATABASE_NAME).document(codeRoom).collection(COLLECTION_NAME).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(LOG_TAG, "Listen failed.", error);
                    return;
                } else {
                    for (DocumentChange document : value.getDocumentChanges()) {
                        Log.d(LOG_TAG, String.valueOf(document.getDocument().getData()));
                        // TODO : call the function to add player to vector
                        Map <String, Object> user = document.getDocument().getData();
                      //  Log.d(LOG_TAG, (String) Objects.requireNonNull(user.get("user")));
                    }
                }
            }
        });
    }

    public void createRoom(View view) {

        String username = usernameView.getText().toString();

        if (!username.equals("")) {
            hostUserView.setText(username);
            hostUserView.setVisibility(View.VISIBLE);
            createRoomButton.setVisibility(View.INVISIBLE);
            usernameView.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            String codeRoom = generateRandomString(HostCode);
            codeRoomView.setText(codeRoom.replaceAll(".(?!$)", "$0\n"));
            codeRoomView.setVisibility(View.VISIBLE);

            addHostToDB(username, codeRoom);
            waitForPlayers(codeRoom);
        }
    }

    public void playGame(View view) {
        // TODO
    }
}