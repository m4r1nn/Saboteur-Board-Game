package com.example.saboteur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class JoinActivity extends AppCompatActivity {

    EditText joinUserView;
    EditText joinCodeVIew;
    Button joinButton;

    final private String DATABASE_NAME = "users";
    final private String COLLECTION_NAME = "test";
    private final String LOG_TAG = JoinActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        joinUserView = findViewById(R.id.join_text_user_view);
        joinCodeVIew = findViewById(R.id.join_text_code_value);
        joinButton = findViewById(R.id.join_button);
    }



    public void joinRoom(View view) {
        String username = joinUserView.getText().toString();
        String code = joinCodeVIew.getText().toString();

        final Map<String, Object> join_user = new HashMap<>();
        join_user.put("user", username);


        // get a reference to database and document
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(LOG_TAG, code);
        final DocumentReference docRef = db.collection(DATABASE_NAME).document(code);

        // TODO : maybe refactor this in the future
        docRef.collection(COLLECTION_NAME).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() > 0) {
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
                    Toast.makeText(JoinActivity.this, "Codul nu exista",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}