package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saboteur.utils.Sound;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class JoinActivity extends AppCompatActivity {

    EditText joinUserView;
    EditText joinCodeVIew;
    Button joinButton;

    final private int MAX_PLAYERS = 10;
    final private String COLLECTION_NAME = "test";
    final private String DATABASE_NAME = "users";
    private final String LOG_TAG = JoinActivity.class.getSimpleName();

    private Sound buttonSound = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        joinUserView = findViewById(R.id.join_text_user_view);
        joinCodeVIew = findViewById(R.id.join_text_code_value);
        joinButton = findViewById(R.id.join_button);

        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));
    }



    public void joinRoom(View view) {
        buttonSound.initSound();
        buttonSound.start();

        String username = joinUserView.getText().toString();
        String code = joinCodeVIew.getText().toString();

        final Map<String, Object> join_user = new HashMap<>();
        join_user.put("user", username);


        // get a reference to database and document
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(LOG_TAG, code);

        final DocumentReference docRef = db.collection(DATABASE_NAME).document(code);

        // TODO : maybe refactor this in the future
        docRef.collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(LOG_TAG, String.valueOf(task.getResult().size()));
                if (task.getResult().size() > 0 && task.getResult().size() < MAX_PLAYERS) {
                    docRef.collection(COLLECTION_NAME).add(join_user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(LOG_TAG, "User adaugat cu success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(LOG_TAG, "Eroare la adaugare user", e);
                        }
                    });
                } else {
                    Toast.makeText(JoinActivity.this, "Codul nu exista", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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