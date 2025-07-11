package com.adiscrazy.math_quiz;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Optionally, send the token to your server for future communication
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Extract the notification data
        String title = null;
        String body = null;

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            Log.d("FCM", "Title: " + title + " Body: " + body);
        }

        // Handle the notification based on whether the app is in the foreground or background
        if (isAppInForeground()) {
            // App is in the foreground, handle the notification (you can show a dialog or notification)
            if (title != null && body != null) {
                sendNotification(title, body);
            }
        } else {
            // App is in the background, show the notification in the notification shade
            if (title != null && body != null) {
                sendNotification(title, body);
            }
        }
    }

    private boolean isAppInForeground() {
        // Simple check to see if app is in foreground
        return ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED);
    }

    private void sendNotification(String title, String body) {
        String channelId = "quiz_channel"; // Unique channel ID for your quiz app
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android 8.0 (API 26+) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Quiz App Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Check if the channel already exists
            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create Intent to open MainActivity when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class); // Adjust to your main activity

        // Use TaskStackBuilder to handle the back stack properly if the app is in the foreground
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);



        // Create the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_stat_name) // Replace with your notification icon
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent); // Set the intent for notification tap

        // Display the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
