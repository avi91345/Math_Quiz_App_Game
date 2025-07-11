package com.adiscrazy.math_quiz;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;



import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "prefs";
    private static final String LAST_CHECK_TIMESTAMP = "last_check_timestamp";
    TextView message;

    private static final String TAG = "FirebaseConnectionCheck";
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 101;

    private SharedPreferences sharedPreferencess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FirebaseApp.initializeApp(this); // Explicitly initialize Firebase
            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            Log.d(TAG, "Firebase is connected: " + firebaseApp.toString());
        } catch (Exception e) {
            Log.e(TAG, "Firebase is NOT connected: " + e.getMessage());
        }

        sharedPreferencess = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Set the minimum fetch interval to 12 hours (43200 seconds)
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(43200)  // 12 hours
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        checkForAppUpdate();




        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        message = findViewById(R.id.message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13 (API 33)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }



        // Check for internet connectivity
        if (!isInternetAvailable()) {
            showNoInternetDialog();
            return; // Stop further execution if no internet
        }

        // Check if the name is already stored in SharedPreferences
        String userName = sharedPreferences.getString("userName", null);
        if (userName == null) {
            // Show the dialog to ask for the user's name
            showNameDialog();
        }
        else{
            message.setText("Welcome, " + userName + "!\n Please select a topic to start quiz...");
            animateText(message);

        }

        // Hide the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Initialize all buttons
        Button btnAddition = findViewById(R.id.btnAddition);
        Button btnSubtraction = findViewById(R.id.btnSubtraction);
        Button btnMultiplication = findViewById(R.id.btnMultiplication);
        Button btnDivision = findViewById(R.id.btnDivision);
        Button btnWordProblems = findViewById(R.id.btnWordProblems);
        Button btnFractions = findViewById(R.id.btnFractions);
        Button btnPatternsSequences = findViewById(R.id.btnPatternsSequences);
        Button btnMathPuzzles = findViewById(R.id.btnMathPuzzles);


        setButtonEffect(btnAddition);
        setButtonEffect(btnSubtraction);
        setButtonEffect(btnMultiplication);
        setButtonEffect(btnDivision);
        setButtonEffect(btnWordProblems);
        setButtonEffect(btnFractions);
        setButtonEffect(btnPatternsSequences);
        setButtonEffect(btnMathPuzzles);

        // Set onClickListener for each button
        btnAddition.setOnClickListener(v -> showDifficultyDialog("Addition"));
        btnSubtraction.setOnClickListener(v -> showDifficultyDialog("Subtraction"));
        btnMultiplication.setOnClickListener(v -> showDifficultyDialog("Multiplication"));
        btnDivision.setOnClickListener(v -> showDifficultyDialog("Division"));
        btnWordProblems.setOnClickListener(v -> showDifficultyDialog("Word Problems"));
        btnFractions.setOnClickListener(v -> showDifficultyDialog("Fractions"));
        btnPatternsSequences.setOnClickListener(v -> showDifficultyDialog("Patterns & Sequences"));
        btnMathPuzzles.setOnClickListener(v -> showDifficultyDialog("Math Puzzles"));



    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please turn on your internet connection to proceed.");
        builder.setCancelable(false); // Prevent dismissing by clicking outside
        builder.setPositiveButton("Retry", (dialog, which) -> {
            // Check again for internet connectivity
            if (isInternetAvailable()) {
                recreate(); // Reload the activity
            } else {
                showNoInternetDialog(); // Show the dialog again if no internet
            }
        });

        builder.setNegativeButton("Exit", (dialog, which) -> finish()); // Exit the app if the user doesn't enable internet
        builder.create().show();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View customView = getLayoutInflater().inflate(R.layout.dialog_name_input, null);
        builder.setView(customView);

        // Get references to UI elements
        EditText input = customView.findViewById(R.id.input_name);
        Button submitButton = customView.findViewById(R.id.btn_submit);

        // Create the dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Handle Submit button click
        submitButton.setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please let me know your name to proceed", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", name);
                editor.apply();
                message.setText("Welcome, " + name + "!\n Please select a topic to start quiz...");
                animateText(message);
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }


    private void showDifficultyDialog(String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);

        // Inflate the custom layout
        View customView = getLayoutInflater().inflate(R.layout.dialog_difficulty_selection, null);
        builder.setView(customView);

        // Get references to UI elements
        Button btnEasy = customView.findViewById(R.id.btn_easy);
        Button btnMedium = customView.findViewById(R.id.btn_medium);
        Button btnHard = customView.findViewById(R.id.btn_hard);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set click listeners for difficulty buttons
        btnEasy.setOnClickListener(v -> {
            navigateToQuizPage(category, "Easy");
            dialog.dismiss();
        });

        btnMedium.setOnClickListener(v -> {
            navigateToQuizPage(category, "Medium");
            dialog.dismiss();
        });

        btnHard.setOnClickListener(v -> {
            navigateToQuizPage(category, "Hard");
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }


    private void navigateToQuizPage(String category, String difficulty) {
        Intent intent = new Intent(MainActivity.this, MainQuizPage.class);
        intent.putExtra("category", category);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
    private void animateText(final TextView message) {
        // Create a fade-in animation (Alpha)
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f); // Fade from transparent to visible
        fadeIn.setDuration(2000); // Duration for fade-in (1 second)

        // Start the fade-in animation
        message.startAnimation(fadeIn);

    }
    @Override
    protected void onStop() {
        super.onStop();

         stopService(new Intent(this, MusicService.class));
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Start the music service again when the activity resumes
        startService(new Intent(this, MusicService.class));
    }

    private void setButtonEffect(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // When button is pressed
                    v.setBackgroundColor(Color.parseColor("#FFA726")); // Change to pressed color
                    v.setScaleX(0.95f); // Slightly reduce size
                    v.setScaleY(0.95f);
                    break;

                case MotionEvent.ACTION_UP: // When button is released
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundColor(Color.parseColor("#194BDD")); // Restore original color
                    v.setScaleX(1f); // Restore size
                    v.setScaleY(1f);
                    break;
            }
            return false; // Return false to allow other events like onClick
        });
    }

    private void checkForAppUpdate() {
        long lastCheckTime = sharedPreferencess.getLong(LAST_CHECK_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        // Check if 12 hours have passed since the last check
        if (currentTime - lastCheckTime >= 43200000L) { // 12 hours in milliseconds
            fetchRemoteConfig();
            sharedPreferencess.edit().putLong(LAST_CHECK_TIMESTAMP, currentTime).apply();
        } else {
            Log.d("Firebase", "12 hours not passed, no need to check for update.");
        }
    }

    private void fetchRemoteConfig() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String remoteVersion = mFirebaseRemoteConfig.getString("app_version");
                        String currentVersion = getCurrentAppVersion();

                        Log.d("Firebase", "Remote version: " + remoteVersion);
                        Log.d("Firebase", "Current version: " + currentVersion);

                        if (!remoteVersion.equals(currentVersion)) {
                            showUpdateDialog();
                        }
                    } else {
                        Log.e("Firebase", "Fetch failed");
                    }
                });
    }

    private String getCurrentAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void showUpdateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("New Update Available")
                .setMessage("A new version of the app is available. Please update to continue.")
                .setCancelable(false)  // Prevents dismissing the dialog by tapping outside
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Redirect to Play Store or app update page
                        openPlayStore();
                    }
                })
                .show();  // Only "Update" button, no "Cancel"
    }


    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
