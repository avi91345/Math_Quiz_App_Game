package com.adiscrazy.math_quiz;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize MediaPlayer with the music resource
        mediaPlayer = MediaPlayer.create(this, R.raw.music1); // Replace with your music file
        mediaPlayer.setLooping(true); // Enable looping to restart when finished
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start playing music when the service is started
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        // If music is finished, restart it
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());

        // Return sticky so the service remains running
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used in this example as we're not binding to the service
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the media player when service is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}

