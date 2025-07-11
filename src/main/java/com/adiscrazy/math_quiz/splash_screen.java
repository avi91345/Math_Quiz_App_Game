package com.adiscrazy.math_quiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide status and navigation bars for fullscreen experience
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Find the VideoView by ID
        VideoView videoView = findViewById(R.id.videoView);

        // Set the path to the video file
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro; // Replace 'intro' with your video file name
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Add media controller for video playback (optional)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setMediaController(null);

        // Play the video
        videoView.start();

        // Prepare to play background music
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.introsong); // Replace 'intro_music' with your music file name
        mediaPlayer.start(); // Start playing the audio

        // Ensure splash screen lasts at least 7 seconds
        new Handler().postDelayed(() -> {
            // If the video is still playing, stop it
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            // Stop the audio
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            // Start the main activity
            Intent intent = new Intent(splash_screen.this, MainActivity.class);
            startActivity(intent);

            // Apply a transition animation for smooth movement (optional)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Close the splash screen activity
            finish();
        }, 5200); // 5 seconds delay (adjust this according to your needs)

        // Add a listener to detect when the video finishes
        videoView.setOnCompletionListener(mediaPlayer1 -> {
            // Stop audio if video finishes before the 7-second delay
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        });
    }
}
