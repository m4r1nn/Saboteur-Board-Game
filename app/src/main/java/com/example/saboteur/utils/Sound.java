package com.example.saboteur.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.saboteur.R;

public class Sound {
    private MediaPlayer sound = null;
    private Context context;
    private Uri uri;

    public Sound(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    // context = this from current activity/class
    public void initSound() {
        if (sound == null) {
            sound = MediaPlayer.create(context, uri);
            sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSound();
                }
            });
        }
    }

    // to call in onStop method from activity
    public void stopSound() {
        if (sound != null) {
            sound.release();
            sound = null;
        }
    }

    public void start() {
        sound.start();
    }
}
