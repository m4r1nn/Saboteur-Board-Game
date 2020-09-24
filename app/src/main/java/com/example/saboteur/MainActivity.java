package com.example.saboteur;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.saboteur.utils.Sound;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Sound buttonSound = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSound = new Sound(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.button_sound));
    }

    public void exitApp(View view) {
        Log.d(LOG_TAG, "exitApp");
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
                        System.exit(0);
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

    public void hostApp(View view) {
        Toast.makeText(this, "host", Toast.LENGTH_SHORT).show();
        buttonSound.initSound();
        buttonSound.start();
        Intent intent = new Intent(this, HostActivity.class);
        finish();
        startActivity(intent);
    }

    public void joinApp(View view) {
        Toast.makeText(this, "join", Toast.LENGTH_SHORT).show();
        buttonSound.initSound();
        buttonSound.start();
        Intent intent = new Intent(this, JoinActivity.class);
        finish();
        startActivity(intent);
    }
}
