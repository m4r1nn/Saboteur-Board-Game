package com.example.saboteur;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
    }

    public void hostApp(View view) {
        Toast.makeText(this, "host", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HostActivity.class);
        startActivity(intent);
    }

    public void joinApp(View view) {
        Toast.makeText(this, "join", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}
