package com.adiscrazy.math_quiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainQuizPage extends AppCompatActivity {
    private TextView tvTotalScore, tvSetNumber, tvDifficultyLevel, tvRemoveOptionChances, tvSkippedQuestions, tvQuestion;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnLock, btnRemoveWrongOptions, btnSkip, end;
    String category;
    String difficulty;
    int skipq=0;
    private String selectedOption = "";
    int correctAnswer;
    String correctAnswerr;
    Button buttonsel;
    int total_score = 0;
    int set = 1;
    int count = 1;
    private int skipsUsed = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_quiz_page);
        mediaPlayer = MediaPlayer.create(this, R.raw.quiz);


        // Set the music to loop infinitely
        mediaPlayer.setLooping(true);

        // Start playing the music when the page starts
        mediaPlayer.start();

        // Hide the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Initialize UI components
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvSetNumber = findViewById(R.id.tvSetNumber);
        tvDifficultyLevel = findViewById(R.id.tvDifficultyLevel);
        tvRemoveOptionChances = findViewById(R.id.tvRemoveOptionChances);
        tvSkippedQuestions = findViewById(R.id.tvSkippedQuestions);
        tvQuestion = findViewById(R.id.tvQuestion);

        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);
        btnLock = findViewById(R.id.btnLock);
        btnLock.setEnabled(false);
        btnRemoveWrongOptions = findViewById(R.id.btnRemoveWrongOptions);
        btnSkip = findViewById(R.id.btnSkip);
        end = findViewById(R.id.end);
        setButtonEffect(btnOptionA);
        setButtonEffect(btnOptionB);
        setButtonEffect(btnOptionC);
        setButtonEffect(btnOptionD);
        setButtonEffect(btnLock);
        setButtonEffect(btnRemoveWrongOptions);
        setButtonEffect(btnSkip);
        setButtonEffect(end);

        // Set category and difficulty level text from Intent
        category = getIntent().getStringExtra("category");
        difficulty = getIntent().getStringExtra("difficulty");
        tvDifficultyLevel.setText("Difficulty: " + difficulty);

        // Set initial UI text
        tvTotalScore.setText("Score: 0/10");
        tvSetNumber.setText("Set 1");
        tvRemoveOptionChances.setText("Remove Option Chances: 2");
        tvSkippedQuestions.setText("Skipped Questions: 0");
        btnLock.setText("Submit");
        btnRemoveWrongOptions.setText("Remove 2 Wrong Options");
        btnSkip.setText("Skip");

        // Initialize options
        btnOptionA.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionB.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionC.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionD.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnLock.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        btnRemoveWrongOptions.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        btnSkip.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        end.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Call the appropriate category method
        loadNewQuestion(difficulty);


        // Set button click listeners
        btnOptionA.setOnClickListener(v -> handleOptionClick("Option A", btnOptionA.getText().toString(),btnOptionA));
        btnOptionB.setOnClickListener(v -> handleOptionClick("Option B", btnOptionB.getText().toString(),btnOptionB));
        btnOptionC.setOnClickListener(v -> handleOptionClick("Option C", btnOptionC.getText().toString(), btnOptionC));
        btnOptionD.setOnClickListener(v -> handleOptionClick("Option D", btnOptionD.getText().toString(),btnOptionD));
        btnLock.setOnClickListener(v -> handleLockClick());
        btnRemoveWrongOptions.setOnClickListener(v -> handleRemoveWrongOptionsClick());
        btnSkip.setOnClickListener(v -> handleSkipClick());
        end.setOnClickListener(v -> {
            // Inflate the custom layout
            View customView = getLayoutInflater().inflate(R.layout.dialog_exit, null);

            // Create the AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(customView)
                    .setCancelable(false); // Prevent dismissal by tapping outside

            // Create and show the dialog
            AlertDialog alert = builder.create();

            // Set the custom background drawable
            alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

            // Find the buttons in the custom layout
            Button yesButton = customView.findViewById(R.id.btn_yes);
            Button noButton = customView.findViewById(R.id.btn_no);

            // Set onClickListener for Yes button
            yesButton.setOnClickListener(view -> {
                finish(); // Close the activity when Yes is clicked
                alert.dismiss(); // Dismiss the dialog
            });

            // Set onClickListener for No button
            noButton.setOnClickListener(view -> {
                alert.dismiss(); // Just dismiss the dialog when No is clicked
            });

            // Show the dialog
            alert.show();
        });


    }

    private void loadNewQuestion(String difficulty) {
        // Category logic: switch based on selected category and difficulty
        switch (category) {
            case "Addition":
                addition(difficulty);
                break;
            case "Subtraction":
                subtraction(difficulty);
                break;
            case "Multiplication":
                multiplication(difficulty);
                break;
            case "Division":
                division(difficulty);
                break;
            case "Fractions":
                fractions(difficulty);
                break;
            case "Word Problems":
                wordProblems(difficulty);
                break;
            case "Patterns & Sequences":
                pattern_sequence(difficulty);
                break;
            case "Math Puzzles":
                mathPuzzleGenerator(difficulty);
                break;
            default:
                Toast.makeText(this, "Invalid Category", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleOptionClick(String option, String data,Button buttonname) {
        btnLock.setEnabled(true);
        selectedOption = data;
        buttonsel=buttonname;
        resetOptionButtonColors();
        changeButtonColor(option);

    }

    private void resetOptionButtonColors() {
        btnOptionA.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionB.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionC.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
        btnOptionD.setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
    }

    private void changeButtonColor(String option) {
        switch (option) {
            case "Option A":
                btnOptionA.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
                break;
            case "Option B":
                btnOptionB.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
                break;
            case "Option C":
                btnOptionC.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
                break;
            case "Option D":
                btnOptionD.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
                break;
        }
    }

    private void handleLockClick() {
        btnLock.setEnabled(false);
        if(category.equals("Fractions")){
            if (selectedOption.substring(3, selectedOption.length()).equals(correctAnswerr)) {
                total_score += 1;
                tvTotalScore.setText("Score: " + total_score + "/10");
                MediaPlayer mediaPlayerrrr = MediaPlayer.create(this, R.raw.correct);
                mediaPlayerrrr.start();
                buttonsel.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                mediaPlayerrrr.setOnCompletionListener(mp -> mp.release());

            }
            else{
                colorcorrectf();
                MediaPlayer mediaPlayerrr = MediaPlayer.create(this, R.raw.wrong);
                mediaPlayerrr.start();
                buttonsel.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                mediaPlayerrr.setOnCompletionListener(mp -> mp.release());

            }

        }
        else {
            // Logic to check if selected option matches the correct answer
            if (selectedOption.substring(3, selectedOption.length()).equals(String.valueOf(correctAnswer))) {
                total_score += 1;
                tvTotalScore.setText("Score: " + total_score + "/10");
                MediaPlayer mediaPlayerrrr = MediaPlayer.create(this, R.raw.correct);
                mediaPlayerrrr.start();
                buttonsel.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                mediaPlayerrrr.setOnCompletionListener(mp -> mp.release());

            } else {
                colorcorrecti();
                MediaPlayer mediaPlayerrr = MediaPlayer.create(this, R.raw.wrong);
                mediaPlayerrr.start();
                buttonsel.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                mediaPlayerrr.setOnCompletionListener(mp -> mp.release());
            }
        }

        btnOptionA.setEnabled(false);
        btnOptionB.setEnabled(false);
        btnOptionC.setEnabled(false);
        btnOptionD.setEnabled(false);
        btnLock.setEnabled(false);
        btnRemoveWrongOptions.setEnabled(false);
        btnSkip.setText("Next");

    }

    private void handleRemoveWrongOptionsClick() {
        btnLock.setEnabled(false);
        btnRemoveWrongOptions.setEnabled(false);


        resetOptionButtonColors();

        if (skipsUsed < 2) {
            skipsUsed++;
            tvSkippedQuestions.setEnabled(false);

            tvRemoveOptionChances.setText("Remove Option Chances: " + (2 - skipsUsed));

            int removed = 0;
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(btnOptionA);
            buttons.add(btnOptionB);
            buttons.add(btnOptionC);
            buttons.add(btnOptionD);
            Collections.shuffle(buttons);


            if (category.equals("Fractions")) {

                removed = 0;


                for (Button button : buttons) {
                    if (removed < 2 && !button.getText().toString().substring(3).equals(correctAnswerr)) {
                        button.setEnabled(false);
                        button.setText("REMOVED");
                        removed++;
                    }
                    if (removed == 2) {
                        break; // Stop once 2 options are removed
                    }
                }
            }else{

                removed = 0;


                for (Button button : buttons) {
                    if (removed < 2 && !button.getText().toString().substring(3).equals(String.valueOf(correctAnswer))) {
                        button.setEnabled(false);
                        button.setText("REMOVED");
                        removed++;
                    }
                    if (removed == 2) {
                        break; // Stop once 2 options are removed
                    }
                }
                }
            }
        else {
            Toast.makeText(this, "No more Remove Option Chances!", Toast.LENGTH_SHORT).show();
        }

    }


    private void handleSkipClick() {
        if (!isInternetAvailable()) {
            showNoInternetDialog();
            return; // Stop further execution if no internet
        }
        if(btnSkip.getText().equals("Skip")){
            skipq++;
            tvSkippedQuestions.setText("Skipped Questions: "+skipq);
        }

        resetOptionButtonColors();
        count++;
        if (count == 10) {
            showScoreDialog(this,total_score,set);

            total_score = 0;
            count = 0;
            set++;
            skipsUsed=0;
            skipq=0;
            tvSkippedQuestions.setText("Skipped Questions: 0");
            tvSkippedQuestions.setEnabled(true);
            tvRemoveOptionChances.setText("Remove Option Chances: 2");
            tvTotalScore.setText("Score: " + total_score + "/10");
            tvSetNumber.setText("Set " + set);
        }
        btnOptionA.setEnabled(true);
        btnOptionB.setEnabled(true);
        btnOptionC.setEnabled(true);
        btnOptionD.setEnabled(true);
        btnRemoveWrongOptions.setEnabled(true);
        btnLock.setEnabled(false);
        btnSkip.setText("Skip");
        if(tvRemoveOptionChances.getText().equals("Remove Option Chances: 0")){
            btnRemoveWrongOptions.setEnabled(false);
        }
        else{
            btnRemoveWrongOptions.setEnabled(true);
        }

        loadNewQuestion(difficulty);
    }



    public void addition(String difficultyLevel) {

        Random random = new Random();
        int num1 = 0, num2 = 0;
        correctAnswer = 0;

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                num1 = random.nextInt(20) + 1; // Random number from 1 to 20
                num2 = random.nextInt(20) + 1; // Random number from 1 to 20
                break;
            case "medium":
                num1 = random.nextInt(181) + 20; // Random number from 20 to 200
                num2 = random.nextInt(181) + 20; // Random number from 20 to 200
                break;
            case "hard":
                num1 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                num2 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Calculate the correct answer
        correctAnswer = num1 + num2;

        // Generate 3 random incorrect answers around the correct answer
        int incorrectAnswer1 = correctAnswer + random.nextInt(20) + 1; // Small deviation
        int incorrectAnswer2 = correctAnswer - random.nextInt(20) - 1; // Small deviation
        int incorrectAnswer3 = correctAnswer + random.nextInt(50) + 1; // Larger deviation

        // Shuffle answers so the correct answer is not always in the same position
        int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
        shuffleArray(answers);
        String question = String.format("What is the Sum of %d + %d = ?", num1, num2);

        tvQuestion.setText(question);
        btnOptionA.setText("A) " + String.valueOf(answers[0]));
        btnOptionB.setText("B) " + String.valueOf(answers[1]));
        btnOptionC.setText("C) " + String.valueOf(answers[2]));
        btnOptionD.setText("D) " + String.valueOf(answers[3]));

    }

    public void subtraction(String difficultyLevel) {
        Random random = new Random();
        int num1 = 0, num2 = 0;
        correctAnswer = 0;

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                num1 = random.nextInt(20) + 1; // Random number from 1 to 20
                num2 = random.nextInt(20) + 1; // Random number from 1 to 20
                break;
            case "medium":
                num1 = random.nextInt(181) + 20; // Random number from 20 to 200
                num2 = random.nextInt(181) + 20; // Random number from 20 to 200
                break;
            case "hard":
                num1 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                num2 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Ensure num1 is greater than or equal to num2 to avoid negative results
        if (num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }

        // Calculate the correct answer
        correctAnswer = num1 - num2;

        // Generate 3 random incorrect answers around the correct answer
        int incorrectAnswer1 = correctAnswer + random.nextInt(20) + 1; // Small deviation
        int incorrectAnswer2 = correctAnswer - random.nextInt(20) - 1; // Small deviation
        int incorrectAnswer3 = correctAnswer + random.nextInt(50) + 1; // Larger deviation

        // Shuffle answers so the correct answer is not always in the same position
        int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
        shuffleArray(answers);

        // Create the question
        String question = String.format("What is the Difference of %d - %d = ?", num1, num2);

        // Set question and options to TextViews/Buttons
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + answers[0]);
        btnOptionB.setText("B) " + answers[1]);
        btnOptionC.setText("C) " + answers[2]);
        btnOptionD.setText("D) " + answers[3]);
    }


    public void division(String difficultyLevel) {
        Random random = new Random();
        int dividend = 0, divisor = 0;
        correctAnswer = 0;

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                // Easy: Divisor between 2 and 20, Quotient between 1 and 10
                divisor = random.nextInt(19) + 2; // Random number from 2 to 20
                correctAnswer = random.nextInt(10) + 1; // Random quotient from 1 to 10
                dividend = divisor * correctAnswer;
                break;
            case "medium":
                // Medium: Divisor between 20 and 100, Quotient between 1 and 20
                divisor = random.nextInt(81) + 20; // Random number from 20 to 100
                correctAnswer = random.nextInt(20) + 1; // Random quotient from 1 to 20
                dividend = divisor * correctAnswer;
                break;
            case "hard":
                // Hard: Divisor between 100 and 1000, Quotient between 1 and 50
                divisor = random.nextInt(900) + 100; // Random number from 100 to 1000
                correctAnswer = random.nextInt(50) + 1; // Random quotient from 1 to 50
                dividend = divisor * correctAnswer;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Generate 3 random incorrect answers around the correct answer
        int incorrectAnswer1 = correctAnswer + random.nextInt(3) + 1; // Small variation
        int incorrectAnswer2 = correctAnswer - random.nextInt(3) - 1; // Small variation
        int incorrectAnswer3 = correctAnswer + random.nextInt(5) + 1; // Small variation

        // Shuffle answers
        int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
        shuffleArray(answers);

        // Create the question
        String question = String.format("What is the result of %d ÷ %d = ?", dividend, divisor);

        // Set question and options to TextViews/Buttons
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + answers[0]);
        btnOptionB.setText("B) " + answers[1]);
        btnOptionC.setText("C) " + answers[2]);
        btnOptionD.setText("D) " + answers[3]);
    }

    public void multiplication(String difficultyLevel) {
        Random random = new Random();
        int num1 = 0, num2 = 0;
        correctAnswer = 0;

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                num1 = random.nextInt(20) + 1; // Random number from 1 to 20
                num2 = random.nextInt(10) + 1;  // Random number from 1 to 10
                break;
            case "medium":
                num1 = random.nextInt(181) + 20; // Random number from 20 to 200
                num2 = random.nextInt(91) + 20;  // Random number from 20 to 100
                break;
            case "hard":
                num1 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                num2 = random.nextInt(1301) + 200; // Random number from 200 to 1500
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Calculate the correct answer
        correctAnswer = num1 * num2;

        // Generate 3 random incorrect answers around the correct answer
        int incorrectAnswer1 = correctAnswer + random.nextInt(50) + 1;
        int incorrectAnswer2 = correctAnswer - random.nextInt(50) - 1;
        int incorrectAnswer3 = correctAnswer + random.nextInt(100) + 1;

        // Shuffle answers
        int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
        shuffleArray(answers);

        // Create the question
        String question = String.format("What is the product of %d × %d = ?", num1, num2);

        // Set question and options to TextViews/Buttons
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + answers[0]);
        btnOptionB.setText("B) " + answers[1]);
        btnOptionC.setText("C) " + answers[2]);
        btnOptionD.setText("D) " + answers[3]);
    }


    public void fractions(String difficultyLevel) {
        Random random = new Random();
        int numerator1 = 0, denominator1 = 0, numerator2 = 0, denominator2 = 0;
        String operation = ""; // The operation to perform
        String question;
        double correctAnswer;

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                numerator1 = random.nextInt(10) + 1; // Random numerator from 1 to 10
                denominator1 = random.nextInt(9) + 2; // Random denominator from 2 to 10
                numerator2 = random.nextInt(10) + 1; // Random numerator from 1 to 10
                denominator2 = random.nextInt(9) + 2; // Random denominator from 2 to 10
                operation = "+"; // Addition operation
                break;
            case "medium":
                numerator1 = random.nextInt(50) + 10; // Random numerator from 10 to 50
                denominator1 = random.nextInt(40) + 10; // Random denominator from 10 to 50
                numerator2 = random.nextInt(50) + 10; // Random numerator from 10 to 50
                denominator2 = random.nextInt(40) + 10; // Random denominator from 10 to 50
                operation = random.nextBoolean() ? "+" : "-"; // Randomly choose addition or subtraction
                break;
            case "hard":
                numerator1 = random.nextInt(100) + 50; // Random numerator from 50 to 100
                denominator1 = random.nextInt(90) + 10; // Random denominator from 10 to 100
                numerator2 = random.nextInt(100) + 50; // Random numerator from 50 to 100
                denominator2 = random.nextInt(90) + 10; // Random denominator from 10 to 100
                operation = random.nextInt(2) == 0 ? "*" : "/"; // Randomly choose multiplication or division
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Calculate the correct answer based on the operation
        switch (operation) {
            case "+":
                correctAnswer = (double) numerator1 / denominator1 + (double) numerator2 / denominator2;
                question = String.format("What is (%d/%d) + (%d/%d) ?", numerator1, denominator1, numerator2, denominator2);
                break;
            case "-":
                correctAnswer = (double) numerator1 / denominator1 - (double) numerator2 / denominator2;
                question = String.format("What is (%d/%d) - (%d/%d) ?", numerator1, denominator1, numerator2, denominator2);
                break;
            case "*":
                correctAnswer = ((double) numerator1 / denominator1) * ((double) numerator2 / denominator2);
                question = String.format("What is (%d/%d) × (%d/%d) ?", numerator1, denominator1, numerator2, denominator2);
                break;
            case "/":
                correctAnswer = ((double) numerator1 / denominator1) / ((double) numerator2 / denominator2);
                question = String.format("What is (%d/%d) ÷ (%d/%d) ?", numerator1, denominator1, numerator2, denominator2);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation");
        }

        // Generate incorrect answers
        double incorrectAnswer1 = correctAnswer + random.nextDouble();
        double incorrectAnswer2 = correctAnswer - random.nextDouble();
        double incorrectAnswer3 = correctAnswer + 2 * random.nextDouble();

        // Convert to strings and format answers
        String[] answers = {
                String.format("%.2f", correctAnswer),
                String.format("%.2f", incorrectAnswer1),
                String.format("%.2f", incorrectAnswer2),
                String.format("%.2f", incorrectAnswer3)
        };
        correctAnswerr = answers[0];

        // Shuffle answers
        shuffleArray(answers);

        // Display the question and answers
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + answers[0]);
        btnOptionB.setText("B) " + answers[1]);
        btnOptionC.setText("C) " + answers[2]);
        btnOptionD.setText("D) " + answers[3]);
    }


    public void wordProblems(String difficultyLevel) {
            Random random = new Random();
            String question = "";
            correctAnswer = 0;

            // Generate word problems based on difficulty level
            switch (difficultyLevel.toLowerCase()) {
                case "easy":
                    // Randomly pick one easy question type
                    int easyType = random.nextInt(20);
                    switch (easyType) {
                        case 0:
                            int apples = random.nextInt(20) + 1;
                            int moreApples = random.nextInt(20) + 1;
                            correctAnswer = apples + moreApples;
                            question = String.format("John has %d apples and buys %d more apples. How many apples does he have now?", apples, moreApples);
                            break;
                        case 1:
                            int boys = random.nextInt(20) + 10;
                            int girls = random.nextInt(20) + 10;
                            correctAnswer = boys + girls;
                            question = String.format("There are %d boys and %d girls in a classroom. How many students are there in total?", boys, girls);
                            break;
                        case 2:
                            int marbles = random.nextInt(20) + 5;
                            int foundMarbles = random.nextInt(20) + 5;
                            correctAnswer = marbles + foundMarbles;
                            question = String.format("Alice collected %d marbles and found %d more marbles. How many marbles does she have now?", marbles, foundMarbles);
                            break;
                        case 3:
                            int cakesMorning = random.nextInt(10) + 1;
                            int cakesAfternoon = random.nextInt(10) + 1;
                            correctAnswer = cakesMorning + cakesAfternoon;
                            question = String.format("A baker made %d cakes in the morning and %d cakes in the afternoon. How many cakes did he make in total?", cakesMorning, cakesAfternoon);
                            break;
                        case 4:
                            int pencils = random.nextInt(15) + 1;
                            int morePencils = random.nextInt(10) + 1;
                            correctAnswer = pencils + morePencils;
                            question = String.format("Sarah has %d pencils and her friend gave her %d more pencils. How many pencils does she have now?", pencils, morePencils);
                            break;
                        case 5:
                            int applesInBasket = random.nextInt(30) + 1;
                            int applesEaten = random.nextInt(10) + 1;
                            correctAnswer = applesInBasket - applesEaten;
                            question = String.format("John had %d apples in a basket and ate %d apples. How many apples does he have left?", applesInBasket, applesEaten);
                            break;
                        case 6:
                            int toys = random.nextInt(20) + 5;
                            int newToys = random.nextInt(10) + 1;
                            correctAnswer = toys + newToys;
                            question = String.format("Tom has %d toys and buys %d more toys. How many toys does he have in total?", toys, newToys);
                            break;
                        case 7:
                            int dogs = random.nextInt(10) + 2;
                            int cats = random.nextInt(10) + 2;
                            correctAnswer = dogs + cats;
                            question = String.format("There are %d dogs and %d cats in a park. How many animals are there in total?", dogs, cats);
                            break;
                        case 8:
                            int pencilsOnDesk = random.nextInt(10) + 1;
                            int newPencils = random.nextInt(5) + 1;
                            correctAnswer = pencilsOnDesk + newPencils;
                            question = String.format("Sarah had %d pencils on her desk and got %d more pencils. How many pencils are on her desk now?", pencilsOnDesk, newPencils);
                            break;
                        case 9:
                            int booksInLibrary = random.nextInt(30) + 20;
                            int booksBorrowed = random.nextInt(10) + 1;
                            correctAnswer = booksInLibrary - booksBorrowed;
                            question = String.format("There are %d books in a library. If %d books are borrowed, how many books are left?", booksInLibrary, booksBorrowed);
                            break;
                        case 10:
                            int friends = random.nextInt(5) + 3;
                            int moreFriends = random.nextInt(3) + 1;
                            correctAnswer = friends + moreFriends;
                            question = String.format("Tom has %d friends and makes %d more friends. How many friends does he have now?", friends, moreFriends);
                            break;
                        case 11:
                            int totalChocolates = random.nextInt(30) + 5;
                            int chocolatesGiven = random.nextInt(10) + 1;
                            correctAnswer = totalChocolates - chocolatesGiven;
                            question = String.format("There were %d chocolates, and %d chocolates were given away. How many chocolates are left?", totalChocolates, chocolatesGiven);
                            break;
                        case 12:
                            int carsInParking = random.nextInt(10) + 5;
                            int moreCars = random.nextInt(5) + 1;
                            correctAnswer = carsInParking + moreCars;
                            question = String.format("There are %d cars in a parking lot, and %d more cars arrive. How many cars are in the parking lot?", carsInParking, moreCars);
                            break;
                        case 13:
                            int oranges = random.nextInt(20) + 10;
                            int moreOranges = random.nextInt(5) + 1;
                            correctAnswer = oranges + moreOranges;
                            question = String.format("There are %d oranges in a basket and %d more oranges are added. How many oranges are there now?", oranges, moreOranges);
                            break;
                        case 14:
                            int bikesInShop = random.nextInt(20) + 3;
                            int bikesSold = random.nextInt(5) + 1;
                            correctAnswer = bikesInShop - bikesSold;
                            question = String.format("There are %d bikes in a shop, and %d bikes are sold. How many bikes are left?", bikesInShop, bikesSold);
                            break;
                        case 15:
                            int stickersInPack = random.nextInt(15) + 5;
                            int packsBought = random.nextInt(3) + 1;
                            correctAnswer = stickersInPack * packsBought;
                            question = String.format("Each pack has %d stickers, and you buy %d packs. How many stickers do you have in total?", stickersInPack, packsBought);
                            break;
                        case 16:
                            int studentsInClass = random.nextInt(30) + 10;
                            int studentsAbsent = random.nextInt(5) + 1;
                            correctAnswer = studentsInClass - studentsAbsent;
                            question = String.format("There are %d students in a class, and %d students are absent. How many students are present?", studentsInClass, studentsAbsent);
                            break;
                        case 17:
                            int cupsInBox = random.nextInt(20) + 1;
                            int boxes = random.nextInt(5) + 1;
                            correctAnswer = cupsInBox * boxes;
                            question = String.format("Each box contains %d cups, and you have %d boxes. How many cups do you have in total?", cupsInBox, boxes);
                            break;
                        case 18:
                            int coinsInPiggyBank = random.nextInt(50) + 10;
                            int newCoins = random.nextInt(10) + 1;
                            correctAnswer = coinsInPiggyBank + newCoins;
                            question = String.format("There are %d coins in the piggy bank, and %d more coins are added. How many coins are there now?", coinsInPiggyBank, newCoins);
                            break;
                        case 19:
                            int cookies = random.nextInt(15) + 3;
                            int cookiesEaten = random.nextInt(10) + 1;
                            correctAnswer = cookies - cookiesEaten;
                            question = String.format("There were %d cookies in a jar, and %d cookies were eaten. How many cookies are left?", cookies, cookiesEaten);
                            break;
                        case 20:
                            int applesBasket = random.nextInt(10) + 1;
                            int moreApplesBasket = random.nextInt(5) + 1;
                            correctAnswer = applesBasket + moreApplesBasket;
                            question = String.format("John has %d apples in his basket and buys %d more apples. How many apples does he have in total?", applesBasket, moreApplesBasket);
                            break;
                        case 21:
                            int birdsInTree = random.nextInt(5) + 1;
                            int moreBirdsInTree = random.nextInt(3) + 1;
                            correctAnswer = birdsInTree + moreBirdsInTree;
                            question = String.format("There are %d birds in a tree and %d more birds fly in. How many birds are in the tree?", birdsInTree, moreBirdsInTree);
                            break;
                        case 22:
                            int pencilsOnDesks = random.nextInt(5) + 1;
                            int newPencilss = random.nextInt(3) + 1;
                            correctAnswer = pencilsOnDesks + newPencilss;
                            question = String.format("Sarah has %d pencils on her desk and gets %d more pencils. How many pencils are on her desk now?", pencilsOnDesks, newPencilss);
                            break;
                        case 23:
                            int dogsInPark = random.nextInt(5) + 1;
                            int moreDogsInPark = random.nextInt(3) + 1;
                            correctAnswer = dogsInPark + moreDogsInPark;
                            question = String.format("There are %d dogs in the park, and %d more dogs come. How many dogs are in the park now?", dogsInPark, moreDogsInPark);
                            break;
                        case 24:
                            int toysInToyBox = random.nextInt(10) + 1;
                            int newToysInBox = random.nextInt(5) + 1;
                            correctAnswer = toysInToyBox + newToysInBox;
                            question = String.format("Tom has %d toys in his toy box and gets %d more toys. How many toys are in his toy box now?", toysInToyBox, newToysInBox);
                            break;
                        case 25:
                            int coinsInWallet = random.nextInt(10) + 1;
                            int moreCoinsInWallet = random.nextInt(5) + 1;
                            correctAnswer = coinsInWallet + moreCoinsInWallet;
                            question = String.format("There are %d coins in a wallet, and %d more coins are added. How many coins are in the wallet now?", coinsInWallet, moreCoinsInWallet);
                            break;
                        case 26:
                            int orangesInBasket = random.nextInt(5) + 1;
                            int moreOrangesInBasket = random.nextInt(3) + 1;
                            correctAnswer = orangesInBasket + moreOrangesInBasket;
                            question = String.format("There are %d oranges in a basket, and %d more oranges are added. How many oranges are in the basket now?", orangesInBasket, moreOrangesInBasket);
                            break;
                        case 27:
                            int cupsInCupboard = random.nextInt(5) + 1;
                            int moreCupsInCupboard = random.nextInt(3) + 1;
                            correctAnswer = cupsInCupboard + moreCupsInCupboard;
                            question = String.format("There are %d cups in the cupboard, and %d more cups are added. How many cups are in the cupboard now?", cupsInCupboard, moreCupsInCupboard);
                            break;
                        case 28:
                            int marblesInJar = random.nextInt(10) + 1;
                            int moreMarblesInJar = random.nextInt(3) + 1;
                            correctAnswer = marblesInJar + moreMarblesInJar;
                            question = String.format("There are %d marbles in a jar, and %d more marbles are added. How many marbles are in the jar now?", marblesInJar, moreMarblesInJar);
                            break;
                        case 29:
                            int candiesInJar = random.nextInt(10) + 1;
                            int candiesGivenAway = random.nextInt(5) + 1;
                            correctAnswer = candiesInJar - candiesGivenAway;
                            question = String.format("There are %d candies in the jar, and %d candies are given away. How many candies are left?", candiesInJar, candiesGivenAway);
                            break;
                        case 30:
                            int cookiesInBox = random.nextInt(10) + 1;
                            int cookiesEatens = random.nextInt(5) + 1;
                            correctAnswer = cookiesInBox - cookiesEatens;
                            question = String.format("There are %d cookies in a box, and %d cookies are eaten. How many cookies are left?", cookiesInBox, cookiesEatens);
                            break;
                        case 31:
                            int pencilsInCup = random.nextInt(10) + 1;
                            int pencilsDropped = random.nextInt(3) + 1;
                            correctAnswer = pencilsInCup - pencilsDropped;
                            question = String.format("There are %d pencils in a cup, and %d pencils are dropped. How many pencils are left in the cup?", pencilsInCup, pencilsDropped);
                            break;
                        case 32:
                            int applesInBowl = random.nextInt(5) + 1;
                            int applesTaken = random.nextInt(2) + 1;
                            correctAnswer = applesInBowl - applesTaken;
                            question = String.format("There are %d apples in a bowl, and %d apples are taken. How many apples are left in the bowl?", applesInBowl, applesTaken);
                            break;
                        case 33:
                            int balloonsInBag = random.nextInt(8) + 1;
                            int balloonsLost = random.nextInt(3) + 1;
                            correctAnswer = balloonsInBag - balloonsLost;
                            question = String.format("There are %d balloons in a bag, and %d balloons are lost. How many balloons are left in the bag?", balloonsInBag, balloonsLost);
                            break;
                        case 34:
                            int shoesInCloset = random.nextInt(6) + 1;
                            int shoesBought = random.nextInt(3) + 1;
                            correctAnswer = shoesInCloset + shoesBought;
                            question = String.format("There are %d shoes in the closet, and %d shoes are bought. How many shoes are in the closet now?", shoesInCloset, shoesBought);
                            break;
                        case 35:
                            int candiesInBox = random.nextInt(8) + 1;
                            int candiesBought = random.nextInt(3) + 1;
                            correctAnswer = candiesInBox + candiesBought;
                            question = String.format("There are %d candies in the box, and %d candies are bought. How many candies are in the box now?", candiesInBox, candiesBought);
                            break;
                        case 36:
                            int flowersInVase = random.nextInt(4) + 1;
                            int flowersPlanted = random.nextInt(2) + 1;
                            correctAnswer = flowersInVase + flowersPlanted;
                            question = String.format("There are %d flowers in a vase, and %d flowers are planted. How many flowers are in the vase now?", flowersInVase, flowersPlanted);
                            break;
                        case 37:
                            int chairsInRoom = random.nextInt(5) + 1;
                            int chairsAdded = random.nextInt(3) + 1;
                            correctAnswer = chairsInRoom + chairsAdded;
                            question = String.format("There are %d chairs in the room, and %d chairs are added. How many chairs are in the room now?", chairsInRoom, chairsAdded);
                            break;
                        case 38:
                            int stickersOnBoard = random.nextInt(8) + 1;
                            int stickersAdded = random.nextInt(3) + 1;
                            correctAnswer = stickersOnBoard + stickersAdded;
                            question = String.format("There are %d stickers on the board, and %d stickers are added. How many stickers are on the board now?", stickersOnBoard, stickersAdded);
                            break;
                        case 39:
                            int coinsInPocket = random.nextInt(6) + 1;
                            int moreCoinsInPocket = random.nextInt(3) + 1;
                            correctAnswer = coinsInPocket + moreCoinsInPocket;
                            question = String.format("There are %d coins in your pocket, and %d more coins are added. How many coins are in your pocket now?", coinsInPocket, moreCoinsInPocket);
                            break;
                        case 40:
                            int petsInHouse = random.nextInt(3) + 1;
                            int morePetsInHouse = random.nextInt(2) + 1;
                            correctAnswer = petsInHouse + morePetsInHouse;
                            question = String.format("There are %d pets in the house, and %d more pets are brought in. How many pets are in the house now?", petsInHouse, morePetsInHouse);
                            break;

                    }
                    break;

                case "medium":
                    // Randomly pick one medium question type
                    int mediumType = random.nextInt(20);
                    switch (mediumType) {
                        case 0:
                            int workers = random.nextInt(30) + 10;
                            int tasksPerWorker = random.nextInt(10) + 5;
                            correctAnswer = workers * tasksPerWorker;
                            question = String.format("A company has %d workers. Each worker is assigned %d tasks. How many tasks are assigned in total?", workers, tasksPerWorker);
                            break;
                        case 1:
                            int speed = random.nextInt(50) + 20;
                            int time = random.nextInt(5) + 1;
                            correctAnswer = speed * time;
                            question = String.format("A train travels %d km every hour for %d hours. How far does it travel in total?", speed, time);
                            break;
                        case 2:
                            int floors = random.nextInt(20) + 5;
                            int roomsPerFloor = random.nextInt(10) + 5;
                            correctAnswer = floors * roomsPerFloor;
                            question = String.format("There are %d floors in a building, and each floor has %d rooms. How many rooms are in the building?", floors, roomsPerFloor);
                            break;
                        case 3:
                            int boxes = random.nextInt(20) + 5;
                            int weightPerBox = random.nextInt(10) + 5;
                            correctAnswer = boxes * weightPerBox;
                            question = String.format("A truck carries %d boxes, and each box weighs %d kg. What is the total weight of the boxes?", boxes, weightPerBox);
                            break;
                        case 4:
                            int students = random.nextInt(50) + 20;
                            int notebooksPerStudent = random.nextInt(10) + 1;
                            correctAnswer = students * notebooksPerStudent;
                            question = String.format("A school has %d students, and each student is given %d notebooks. How many notebooks are distributed?", students, notebooksPerStudent);
                            break;
                        case 5:
                            int cars = random.nextInt(30) + 10;
                            int passengersPerCar = random.nextInt(4) + 1;
                            correctAnswer = cars * passengersPerCar;
                            question = String.format("A city has %d cars, and each car can carry %d passengers. How many passengers can be carried in total?", cars, passengersPerCar);
                            break;
                        case 6:
                            int apples = random.nextInt(50) + 10;
                            int boxess = random.nextInt(5) + 2;
                            correctAnswer = apples * boxess;
                            question = String.format("There are %d apples in each of %d boxes. How many apples are there in total?", apples, boxess);
                            break;
                        case 7:
                            int sheets = random.nextInt(100) + 50;
                            int notebooks = random.nextInt(5) + 1;
                            correctAnswer = sheets * notebooks;
                            question = String.format("Each of the %d notebooks has %d sheets. How many sheets are there in total?", notebooks, sheets);
                            break;
                        case 8:
                            int miles = random.nextInt(100) + 20;
                            int gallons = random.nextInt(3) + 1;
                            correctAnswer = miles / gallons;
                            question = String.format("A car travels %d miles with %d gallons of fuel. How many miles does the car travel per gallon?", miles, gallons);
                            break;
                        case 9:
                            int bags = random.nextInt(20) + 5;
                            int itemsPerBag = random.nextInt(10) + 1;
                            correctAnswer = bags * itemsPerBag;
                            question = String.format("A store has %d bags, and each bag contains %d items. How many items are there in total?", bags, itemsPerBag);
                            break;
                        case 10:
                            int chairs = random.nextInt(30) + 10;
                            int tables = random.nextInt(10) + 2;
                            correctAnswer = chairs * tables;
                            question = String.format("A restaurant has %d chairs, and each table can seat %d people. How many people can be seated in total?", chairs, tables);
                            break;
                        case 11:
                            int boxes1 = random.nextInt(10) + 2;
                            int boxes2 = random.nextInt(5) + 1;
                            correctAnswer = boxes1 * boxes2;
                            question = String.format("A warehouse has %d types of boxes, and each type contains %d boxes. How many boxes are there in total?", boxes1, boxes2);
                            break;
                        case 12:
                            int countries = random.nextInt(20) + 5;
                            int citiesPerCountry = random.nextInt(10) + 1;
                            correctAnswer = countries * citiesPerCountry;
                            question = String.format("There are %d countries, and each country has %d cities. How many cities are there in total?", countries, citiesPerCountry);
                            break;
                        case 13:
                            int gramsPerPacket = random.nextInt(100) + 50;
                            int packets = random.nextInt(10) + 5;
                            correctAnswer = gramsPerPacket * packets;
                            question = String.format("Each packet contains %d grams of rice, and there are %d packets. How many grams of rice are there in total?", gramsPerPacket, packets);
                            break;
                        case 14:
                            int books = random.nextInt(50) + 20;
                            int pagesPerBook = random.nextInt(100) + 50;
                            correctAnswer = books * pagesPerBook;
                            question = String.format("There are %d books, and each book has %d pages. How many pages are there in total?", books, pagesPerBook);
                            break;
                        case 15:
                            int workers2 = random.nextInt(30) + 10;
                            int hoursWorked = random.nextInt(8) + 1;
                            correctAnswer = workers2 * hoursWorked;
                            question = String.format("There are %d workers, and each worker works %d hours. How many hours are worked in total?", workers2, hoursWorked);
                            break;
                        case 16:
                            int trees = random.nextInt(100) + 50;
                            int fruitsPerTree = random.nextInt(50) + 10;
                            correctAnswer = trees * fruitsPerTree;
                            question = String.format("There are %d trees, and each tree produces %d fruits. How many fruits are there in total?", trees, fruitsPerTree);
                            break;
                        case 17:
                            int eggs = random.nextInt(100) + 30;
                            int dozen = 12;
                            correctAnswer = eggs / dozen;
                            question = String.format("There are %d eggs. How many dozen eggs are there?", eggs);
                            break;
                        case 18:
                            int studentsPerClass = random.nextInt(30) + 10;
                            int classes = random.nextInt(5) + 2;
                            correctAnswer = studentsPerClass * classes;
                            question = String.format("There are %d students in each class, and there are %d classes. How many students are there in total?", studentsPerClass, classes);
                            break;
                        case 19:
                            int trees2 = random.nextInt(40) + 5;
                            int leavesPerTree = random.nextInt(100) + 20;
                            correctAnswer = trees2 * leavesPerTree;
                            question = String.format("There are %d trees, and each tree has %d leaves. How many leaves are there in total?", trees2, leavesPerTree);
                            break;
                        case 20:
                            int workers3 = random.nextInt(40) + 10;
                            int tasksPerWorker2 = random.nextInt(15) + 5;
                            correctAnswer = workers3 * tasksPerWorker2;
                            question = String.format("A company has %d workers, and each worker is assigned %d tasks. How many tasks are assigned in total?", workers3, tasksPerWorker2);
                            break;
                        case 21:
                            int speed2 = random.nextInt(60) + 30;
                            int time2 = random.nextInt(5) + 2;
                            correctAnswer = speed2 * time2;
                            question = String.format("A train travels %d km every hour for %d hours. How far does it travel in total?", speed2, time2);
                            break;
                        case 22:
                            int floors2 = random.nextInt(30) + 10;
                            int roomsPerFloor2 = random.nextInt(15) + 5;
                            correctAnswer = floors2 * roomsPerFloor2;
                            question = String.format("There are %d floors in a building, and each floor has %d rooms. How many rooms are in the building?", floors2, roomsPerFloor2);
                            break;
                        case 23:
                            int boxes2s = random.nextInt(30) + 10;
                            int weightPerBox2 = random.nextInt(15) + 10;
                            correctAnswer = boxes2s * weightPerBox2;
                            question = String.format("A truck carries %d boxes, and each box weighs %d kg. What is the total weight of the boxes?", boxes2s, weightPerBox2);
                            break;
                        case 24:
                            int students2 = random.nextInt(60) + 20;
                            int notebooksPerStudent2 = random.nextInt(15) + 1;
                            correctAnswer = students2 * notebooksPerStudent2;
                            question = String.format("A school has %d students, and each student is given %d notebooks. How many notebooks are distributed?", students2, notebooksPerStudent2);
                            break;
                        case 25:
                            int cars2 = random.nextInt(40) + 10;
                            int passengersPerCar2 = random.nextInt(5) + 1;
                            correctAnswer = cars2 * passengersPerCar2;
                            question = String.format("A city has %d cars, and each car can carry %d passengers. How many passengers can be carried in total?", cars2, passengersPerCar2);
                            break;
                        case 26:
                            int apples2 = random.nextInt(100) + 20;
                            int boxes3 = random.nextInt(10) + 3;
                            correctAnswer = apples2 * boxes3;
                            question = String.format("There are %d apples in each of %d boxes. How many apples are there in total?", apples2, boxes3);
                            break;
                        case 27:
                            int sheets2 = random.nextInt(200) + 50;
                            int notebooks2 = random.nextInt(10) + 2;
                            correctAnswer = sheets2 * notebooks2;
                            question = String.format("Each of the %d notebooks has %d sheets. How many sheets are there in total?", notebooks2, sheets2);
                            break;
                        case 28:
                            int miles2 = random.nextInt(200) + 40;
                            int gallons2 = random.nextInt(5) + 1;
                            correctAnswer = miles2 / gallons2;
                            question = String.format("A car travels %d miles with %d gallons of fuel. How many miles does the car travel per gallon?", miles2, gallons2);
                            break;
                        case 29:
                            int bags2 = random.nextInt(25) + 5;
                            int itemsPerBag2 = random.nextInt(15) + 2;
                            correctAnswer = bags2 * itemsPerBag2;
                            question = String.format("A store has %d bags, and each bag contains %d items. How many items are there in total?", bags2, itemsPerBag2);
                            break;
                        case 30:
                            int chairs2 = random.nextInt(40) + 10;
                            int tables2 = random.nextInt(15) + 3;
                            correctAnswer = chairs2 * tables2;
                            question = String.format("A restaurant has %d chairs, and each table can seat %d people. How many people can be seated in total?", chairs2, tables2);
                            break;
                        case 31:
                            int boxes3s = random.nextInt(15) + 5;
                            int boxes4 = random.nextInt(10) + 2;
                            correctAnswer = boxes3s * boxes4;
                            question = String.format("A warehouse has %d types of boxes, and each type contains %d boxes. How many boxes are there in total?", boxes3s, boxes4);
                            break;
                        case 32:
                            int countries2 = random.nextInt(30) + 10;
                            int citiesPerCountry2 = random.nextInt(15) + 2;
                            correctAnswer = countries2 * citiesPerCountry2;
                            question = String.format("There are %d countries, and each country has %d cities. How many cities are there in total?", countries2, citiesPerCountry2);
                            break;
                        case 33:
                            int gramsPerPacket2 = random.nextInt(150) + 50;
                            int packets2 = random.nextInt(12) + 3;
                            correctAnswer = gramsPerPacket2 * packets2;
                            question = String.format("Each packet contains %d grams of rice, and there are %d packets. How many grams of rice are there in total?", gramsPerPacket2, packets2);
                            break;
                        case 34:
                            int books2 = random.nextInt(60) + 20;
                            int pagesPerBook2 = random.nextInt(150) + 50;
                            correctAnswer = books2 * pagesPerBook2;
                            question = String.format("There are %d books, and each book has %d pages. How many pages are there in total?", books2, pagesPerBook2);
                            break;
                        case 35:
                            int workers4 = random.nextInt(50) + 20;
                            int hoursWorked2 = random.nextInt(10) + 2;
                            correctAnswer = workers4 * hoursWorked2;
                            question = String.format("There are %d workers, and each worker works %d hours. How many hours are worked in total?", workers4, hoursWorked2);
                            break;
                        case 36:
                            int trees3 = random.nextInt(150) + 50;
                            int fruitsPerTree2 = random.nextInt(70) + 10;
                            correctAnswer = trees3 * fruitsPerTree2;
                            question = String.format("There are %d trees, and each tree produces %d fruits. How many fruits are there in total?", trees3, fruitsPerTree2);
                            break;
                        case 37:
                            int eggs2 = random.nextInt(150) + 50;
                            int dozen2 = 12;
                            correctAnswer = eggs2 / dozen2;
                            question = String.format("There are %d eggs. How many dozen eggs are there?", eggs2);
                            break;
                        case 38:
                            int studentsPerClass2 = random.nextInt(40) + 20;
                            int classes2 = random.nextInt(10) + 2;
                            correctAnswer = studentsPerClass2 * classes2;
                            question = String.format("There are %d students in each class, and there are %d classes. How many students are there in total?", studentsPerClass2, classes2);
                            break;
                        case 39:
                            int trees4 = random.nextInt(60) + 10;
                            int leavesPerTree2 = random.nextInt(150) + 50;
                            correctAnswer = trees4 * leavesPerTree2;
                            question = String.format("There are %d trees, and each tree has %d leaves. How many leaves are there in total?", trees4, leavesPerTree2);
                            break;
                        case 40:
                            int people = random.nextInt(100) + 20;
                            int ticketsPerPerson = random.nextInt(5) + 1;
                            correctAnswer = people * ticketsPerPerson;
                            question = String.format("There are %d people, and each person buys %d tickets. How many tickets are bought in total?", people, ticketsPerPerson);
                            break;


                    }
                    break;

                case "hard":
                    // Randomly pick one hard question type
                    int hardType = random.nextInt(20);
                    switch (hardType) {
                        case 0:
                            int hardSpeed = random.nextInt(100) + 50;
                            int hardTime = random.nextInt(10) + 1;
                            correctAnswer = hardSpeed * hardTime;
                            question = String.format("A car travels at a speed of %d km/h for %d hours. What is the total distance covered?", hardSpeed, hardTime);
                            break;
                        case 1:
                            int packages = random.nextInt(50) + 10;
                            int itemsPerPackage = random.nextInt(20) + 5;
                            correctAnswer = packages * itemsPerPackage;
                            question = String.format("A company ships %d boxes, each containing %d items. How many items are shipped in total?", packages, itemsPerPackage);
                            break;
                        case 2:
                            int planeSpeed = random.nextInt(300) + 200;
                            int flightTime = random.nextInt(5) + 1;
                            correctAnswer = planeSpeed * flightTime;
                            question = String.format("A plane flies at %d km/h for %d hours. How far does it travel in that time?", planeSpeed, flightTime);
                            break;
                        case 3:
                            int litersPerHour = random.nextInt(1000) + 500;
                            int hours = random.nextInt(10) + 1;
                            correctAnswer = litersPerHour * hours;
                            question = String.format("A pipeline delivers %d liters of water per hour. How much water is delivered in %d hours?", litersPerHour, hours);
                            break;
                        case 4:
                            int fieldSections = random.nextInt(10) + 5;
                            int wheatPerSection = random.nextInt(50) + 10;
                            correctAnswer = fieldSections * wheatPerSection;
                            question = String.format("A field is divided into %d sections, and each section produces %d kg of wheat. What is the total production?", fieldSections, wheatPerSection);
                            break;
                        case 5:
                            int workers = random.nextInt(50) + 20;
                            int hoursWorked = random.nextInt(12) + 5;
                            int wagePerHour = random.nextInt(100) + 20;
                            correctAnswer = workers * hoursWorked * wagePerHour;
                            question = String.format("A company employs %d workers. Each worker works %d hours and earns %d per hour. What is the total wage for all workers?", workers, hoursWorked, wagePerHour);
                            break;
                        case 6:
                            int cargoWeight = random.nextInt(10000) + 5000; // weight in kg
                            int containers = random.nextInt(20) + 5;
                            correctAnswer = cargoWeight * containers;
                            question = String.format("A shipping company has %d containers, and each container holds %d kg of cargo. What is the total weight of all the cargo?", containers, cargoWeight);
                            break;
                        case 7:
                            int students = random.nextInt(500) + 100;
                            int days = random.nextInt(30) + 1;
                            correctAnswer = students * days;
                            question = String.format("A school has %d students, and each student attends %d days of classes. What is the total number of student-days?", students, days);
                            break;
                        case 8:
                            int factoryMachines = random.nextInt(30) + 10;
                            int unitsPerMachine = random.nextInt(1000) + 500;
                            correctAnswer = factoryMachines * unitsPerMachine;
                            question = String.format("A factory has %d machines, and each machine produces %d units per day. How many units are produced in total each day?", factoryMachines, unitsPerMachine);
                            break;
                        case 9:
                            int trucks = random.nextInt(20) + 5;
                            int weightPerTruck = random.nextInt(10000) + 5000; // weight in kg
                            correctAnswer = trucks * weightPerTruck;
                            question = String.format("A fleet of %d trucks delivers %d kg of goods each. What is the total weight of goods delivered by all trucks?", trucks, weightPerTruck);
                            break;
                        case 10:
                            int houses = random.nextInt(200) + 50;
                            int roomsPerHouse = random.nextInt(10) + 5;
                            correctAnswer = houses * roomsPerHouse;
                            question = String.format("A neighborhood has %d houses, and each house has %d rooms. How many rooms are there in total?", houses, roomsPerHouse);
                            break;
                        case 11:
                            int farms = random.nextInt(50) + 10;
                            int acresPerFarm = random.nextInt(200) + 100;
                            correctAnswer = farms * acresPerFarm;
                            question = String.format("There are %d farms, and each farm has %d acres. How many acres of land are there in total?", farms, acresPerFarm);
                            break;
                        case 12:
                            int books = random.nextInt(1000) + 500;
                            int pagesPerBook = random.nextInt(300) + 50;
                            correctAnswer = books * pagesPerBook;
                            question = String.format("A library has %d books, and each book has %d pages. How many pages are there in total?", books, pagesPerBook);
                            break;
                        case 13:
                            int cylinders = random.nextInt(100) + 10;
                            int gallonsPerCylinder = random.nextInt(500) + 100;
                            correctAnswer = cylinders * gallonsPerCylinder;
                            question = String.format("A factory produces %d cylinders, and each cylinder holds %d gallons. How many gallons of liquid are produced in total?", cylinders, gallonsPerCylinder);
                            break;
                        case 14:
                            int airports = random.nextInt(50) + 10;
                            int flightsPerDay = random.nextInt(50) + 20;
                            correctAnswer = airports * flightsPerDay;
                            question = String.format("There are %d airports, and each airport has %d flights per day. How many flights are there in total each day?", airports, flightsPerDay);
                            break;
                        case 15:
                            int ships = random.nextInt(30) + 10;
                            int cargoPerShip = random.nextInt(20000) + 10000; // cargo weight in tons
                            correctAnswer = ships * cargoPerShip;
                            question = String.format("There are %d ships, and each ship carries %d tons of cargo. What is the total weight of cargo carried by all ships?", ships, cargoPerShip);
                            break;
                        case 16:
                            int items = random.nextInt(10000) + 5000;
                            int weightPerItem = random.nextInt(5) + 1; // weight in kg
                            correctAnswer = items * weightPerItem;
                            question = String.format("A warehouse holds %d items, and each item weighs %d kg. What is the total weight of all items?", items, weightPerItem);
                            break;
                        case 17:
                            int schools = random.nextInt(100) + 20;
                            int studentsPerSchool = random.nextInt(1000) + 500;
                            correctAnswer = schools * studentsPerSchool;
                            question = String.format("There are %d schools, and each school has %d students. How many students are there in total?", schools, studentsPerSchool);
                            break;
                        case 18:
                            int theaters = random.nextInt(50) + 10;
                            int seatsPerTheater = random.nextInt(300) + 100;
                            correctAnswer = theaters * seatsPerTheater;
                            question = String.format("There are %d theaters, and each theater has %d seats. How many seats are there in total?", theaters, seatsPerTheater);
                            break;
                        case 19:
                            int products = random.nextInt(200) + 50;
                            int workersPerProduct = random.nextInt(5) + 1;
                            correctAnswer = products * workersPerProduct;
                            question = String.format("There are %d products, and each product requires %d workers to produce. How many workers are needed in total?", products, workersPerProduct);
                            break;
                        case 20:
                            int factories = random.nextInt(30) + 10;
                            int workersPerFactory = random.nextInt(50) + 20;
                            int totalWorkers = factories * workersPerFactory;
                            int shifts = random.nextInt(3) + 1;
                            correctAnswer = totalWorkers * shifts;
                            question = String.format("There are %d factories, each with %d workers. If each worker works in %d shifts, how many total shifts are worked?", factories, workersPerFactory, shifts);
                            break;

                        case 21:
                            int itemsPerBox = random.nextInt(100) + 20;
                            int totalBoxes = random.nextInt(50) + 10;
                            int totalItems = itemsPerBox * totalBoxes;
                            int itemsPerContainer = random.nextInt(10) + 2;
                            correctAnswer = totalItems / itemsPerContainer;
                            question = String.format("A warehouse has %d boxes, each containing %d items. If %d items fit into one container, how many containers are needed to store all items?", totalBoxes, itemsPerBox, itemsPerContainer);
                            break;

                        case 22:
                            int employees = random.nextInt(200) + 100;
                            int projectsPerEmployee = random.nextInt(5) + 1;
                            int totalProjects = employees * projectsPerEmployee;
                            int employeesPerTeam = random.nextInt(10) + 1;
                            correctAnswer = totalProjects / employeesPerTeam;
                            question = String.format("There are %d employees, each working on %d projects. If %d employees work on one team, how many teams are needed to complete all the projects?", employees, projectsPerEmployee, employeesPerTeam);
                            break;

                        case 23:
                            int buses = random.nextInt(20) + 5;
                            int passengersPerBus = random.nextInt(50) + 30;
                            int totalPassengers = buses * passengersPerBus;
                            int totalSeatsInBuses = random.nextInt(200) + 50;
                            correctAnswer = totalPassengers / totalSeatsInBuses;
                            question = String.format("A fleet has %d buses, each carrying %d passengers. If each bus has %d seats, how many full buses are needed to transport all passengers?", buses, passengersPerBus, totalSeatsInBuses);
                            break;

                        case 24:
                            int factoriess = random.nextInt(10) + 5;
                            int machinesPerFactory = random.nextInt(50) + 10;
                            int totalMachines = factoriess * machinesPerFactory;
                            int productsPerMachine = random.nextInt(1000) + 500;
                            correctAnswer = totalMachines * productsPerMachine;
                            question = String.format("A factory has %d factories, and each factory has %d machines. Each machine produces %d products per day. How many products are produced in total per day?", factoriess, machinesPerFactory, productsPerMachine);
                            break;

                        case 25:
                            int shipments = random.nextInt(100) + 20;
                            int itemsPerShipment = random.nextInt(200) + 50;
                            int totalItemss = shipments * itemsPerShipment;
                            int workersPerShipment = random.nextInt(10) + 2;
                            correctAnswer = totalItemss / workersPerShipment;
                            question = String.format("A company ships %d shipments, each containing %d items. If each shipment requires %d workers to process, how many workers are needed in total?", shipments, itemsPerShipment, workersPerShipment);
                            break;

                        case 26:
                            int machines = random.nextInt(15) + 10;
                            int outputPerMachine = random.nextInt(200) + 100;
                            int totalOutput = machines * outputPerMachine;
                            int shiftTime = random.nextInt(10) + 1;
                            correctAnswer = totalOutput / shiftTime;
                            question = String.format("A factory has %d machines, each producing %d units. If the machines run for %d shifts, how many units are produced per shift?", machines, outputPerMachine, shiftTime);
                            break;

                        case 27:
                            int workerss = random.nextInt(50) + 30;
                            int hoursWorkedd  = random.nextInt(10) + 5;
                            int hourlyWage = random.nextInt(100) + 20;
                            correctAnswer = workerss * hoursWorkedd * hourlyWage;
                            question = String.format("A company employs %d workers. Each worker works for %d hours and earns %d per hour. How much is the total wage paid?", workerss, hoursWorkedd, hourlyWage);
                            break;

                        case 28:
                            int containerss = random.nextInt(30) + 10;
                            int itemsPerContainers = random.nextInt(500) + 100;
                            int totalItemsss = containerss * itemsPerContainers;
                            int totalEmployees = random.nextInt(20) + 5;
                            correctAnswer = totalItemsss / totalEmployees;
                            question = String.format("There are %d containers, each holding %d items. If %d employees are tasked with dividing the items equally, how many items will each employee handle?", containerss, itemsPerContainers, totalEmployees);
                            break;

                        case 29:
                            int truckss = random.nextInt(20) + 5;
                            int cargoPerTruck = random.nextInt(10000) + 5000;
                            int totalCargo = truckss * cargoPerTruck;
                            int containersPerCargo = random.nextInt(50) + 10;
                            correctAnswer = totalCargo / containersPerCargo;
                            question = String.format("A fleet has %d trucks, each carrying %d kg of cargo. If each container holds %d kg of cargo, how many containers are needed to store the total cargo?", truckss, cargoPerTruck, containersPerCargo);
                            break;

                        case 30:
                            int planes = random.nextInt(20) + 5;
                            int passengersPerPlane = random.nextInt(100) + 50;
                            int totalPassengerss = planes * passengersPerPlane;
                            int flightsPerDays = random.nextInt(10) + 1;
                            correctAnswer = totalPassengerss / flightsPerDays;
                            question = String.format("There are %d planes, each carrying %d passengers. If the planes operate %d flights per day, how many passengers are carried per flight?", planes, passengersPerPlane, flightsPerDays);
                            break;

                        case 31:
                            int workersInFactory = random.nextInt(100) + 50;
                            int unitsPerWorker = random.nextInt(200) + 50;
                            int totalUnitsProduced = workersInFactory * unitsPerWorker;
                            int daysWorked = random.nextInt(20) + 5;
                            correctAnswer = totalUnitsProduced / daysWorked;
                            question = String.format("A factory employs %d workers. Each worker produces %d units. If they work for %d days, how many units are produced per day?", workersInFactory, unitsPerWorker, daysWorked);
                            break;

                        case 32:
                            int schoolss = random.nextInt(100) + 10;
                            int teachersPerSchool = random.nextInt(20) + 5;
                            int totalTeachers = schoolss * teachersPerSchool;
                            int studentsPerTeacher = random.nextInt(20) + 10;
                            correctAnswer = totalTeachers * studentsPerTeacher;
                            question = String.format("There are %d schools, each with %d teachers. If each teacher has %d students, how many students are there in total?", schoolss, teachersPerSchool, studentsPerTeacher);
                            break;

                        case 33:
                            int farmss = random.nextInt(50) + 10;
                            int cropsPerFarm = random.nextInt(500) + 100;
                            int totalCrops = farmss * cropsPerFarm;
                            int daysToHarvest = random.nextInt(10) + 3;
                            correctAnswer = totalCrops / daysToHarvest;
                            question = String.format("There are %d farms, and each farm produces %d crops. If crops are harvested over %d days, how many crops are harvested per day?", farmss, cropsPerFarm, daysToHarvest);
                            break;

                        case 34:
                            int factoriesInRegion = random.nextInt(10) + 2;
                            int unitsProducedPerFactory = random.nextInt(1000) + 500;
                            int totalUnitsProduceds = factoriesInRegion * unitsProducedPerFactory;
                            int regionsInCountry = random.nextInt(5) + 1;
                            correctAnswer = totalUnitsProduceds * regionsInCountry;
                            question = String.format("There are %d factories in a region, and each factory produces %d units. If there are %d regions, how many units are produced in total?", factoriesInRegion, unitsProducedPerFactory, regionsInCountry);
                            break;

                        case 35:
                            int vehicles = random.nextInt(50) + 10;
                            int passengersPerVehicle = random.nextInt(30) + 5;
                            int totalPassengersss = vehicles * passengersPerVehicle;
                            int seatsPerVehicle = random.nextInt(20) + 2;
                            correctAnswer = totalPassengersss / seatsPerVehicle;
                            question = String.format("There are %d vehicles, each carrying %d passengers. If each vehicle has %d seats, how many vehicles will be full?", vehicles, passengersPerVehicle, seatsPerVehicle);
                            break;

                        case 36:
                            int theaterss = random.nextInt(30) + 10;
                            int seatsPerTheaters = random.nextInt(200) + 100;
                            int totalSeats = theaterss * seatsPerTheaters;
                            int audiencesPerShow = random.nextInt(10) + 1;
                            correctAnswer = totalSeats / audiencesPerShow;
                            question = String.format("There are %d theaters, each with %d seats. If each show has %d audience members, how many shows can be held in total?", theaterss, seatsPerTheaters, audiencesPerShow);
                            break;

                        case 37:
                            int restaurants = random.nextInt(50) + 20;
                            int tablesPerRestaurant = random.nextInt(10) + 2;
                            int totalTables = restaurants * tablesPerRestaurant;
                            int customersPerTable = random.nextInt(5) + 1;
                            correctAnswer = totalTables * customersPerTable;
                            question = String.format("There are %d restaurants, and each restaurant has %d tables. If each table serves %d customers, how many customers can be served in total?", restaurants, tablesPerRestaurant, customersPerTable);
                            break;

                        case 38:
                            int airportss = random.nextInt(20) + 5;
                            int flightsPerAirport = random.nextInt(30) + 10;
                            int totalFlights = airportss * flightsPerAirport;
                            int hoursPerFlight = random.nextInt(5) + 1;
                            correctAnswer = totalFlights * hoursPerFlight;
                            question = String.format("There are %d airports, each with %d flights. If each flight lasts for %d hours, how many total hours of flight time are there?", airportss, flightsPerAirport, hoursPerFlight);
                            break;

                        case 39:
                            int containersToShip = random.nextInt(100) + 20;
                            int weightPerContainer = random.nextInt(5000) + 1000;
                            int totalWeight = containersToShip * weightPerContainer;
                            int shipsAvailable = random.nextInt(10) + 3;
                            correctAnswer = totalWeight / shipsAvailable;
                            question = String.format("There are %d containers, each with %d kg of weight. If %d ships are used for shipping, how much weight does each ship carry?", containersToShip, weightPerContainer, shipsAvailable);
                            break;

                        case 40:
                            int productsToManufacture = random.nextInt(2000) + 500;
                            int workersNeededPerProduct = random.nextInt(10) + 1;
                            int totalWorkerss = productsToManufacture * workersNeededPerProduct;
                            int shiftsPerDay = random.nextInt(3) + 1;
                            correctAnswer = totalWorkerss / shiftsPerDay;
                            question = String.format("A factory needs to manufacture %d products, and each product requires %d workers. If each worker works %d shifts per day, how many shifts are needed to complete all production?", productsToManufacture, workersNeededPerProduct, shiftsPerDay);
                            break;


                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid difficulty level");
            }

            // Generate incorrect answers
            int incorrectAnswer1 = correctAnswer + random.nextInt(10) + 1;
            int incorrectAnswer2 = Math.max(0, correctAnswer - random.nextInt(10) - 1); // Ensure non-negative
            int incorrectAnswer3 = correctAnswer + random.nextInt(20) + 10;

            // Shuffle answers
            int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
            shuffleArray(answers);


        tvQuestion.setText(question);
        btnOptionA.setText("A) " + answers[0]);
        btnOptionB.setText("B) " + answers[1]);
        btnOptionC.setText("C) " + answers[2]);
        btnOptionD.setText("D) " + answers[3]);

    }


    public void pattern_sequence(String difficultyLevel) {
        Random random = new Random();
        int num1 = 0, num2 = 0;
        correctAnswer = 0;
        String question = "";

        // Decide whether to generate a pattern or sequence question
        boolean isPatternQuestion = random.nextBoolean();  // Randomly decide between pattern and sequence

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                num1 = random.nextInt(20) + 1; // Random number from 1 to 20
                num2 = random.nextInt(5) + 1;  // Random difference from 1 to 5
                break;
            case "medium":
                num1 = random.nextInt(50) + 10; // Random number from 10 to 50
                num2 = random.nextInt(10) + 1;  // Random difference from 1 to 10
                break;
            case "hard":
                num1 = random.nextInt(200) + 50; // Random number from 50 to 200
                num2 = random.nextInt(50) + 1;   // Random difference from 1 to 50
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }
        int[] mainans;

        if (isPatternQuestion) {
            // Generate a pattern question (Arithmetic progression)
            int difference = num2;
            int start = num1;
            correctAnswer = start + (difference * 4); // The next number in the sequence (5th term)

            // Generate 3 random incorrect answers around the correct answer
            int incorrectAnswer1 = correctAnswer + random.nextInt(10) + 1;
            int incorrectAnswer2 = correctAnswer - random.nextInt(10) - 1;
            int incorrectAnswer3 = correctAnswer + random.nextInt(30) + 1;

            // Shuffle answers so the correct answer is not always in the same position
            int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
            shuffleArray(answers);
            mainans=answers;

            // Create question
            question = String.format("What is the next number in the sequence: %d, %d, %d, %d, ... ?",
                    start, start + difference, start + (difference * 2), start + (difference * 3));
        } else {
            // Generate a sequence question (e.g., addition)
            correctAnswer = num1 + num2;

            // Generate 3 random incorrect answers around the correct answer
            int incorrectAnswer1 = correctAnswer + random.nextInt(10) + 1;
            int incorrectAnswer2 = correctAnswer - random.nextInt(10) - 1;
            int incorrectAnswer3 = correctAnswer + random.nextInt(20) + 1;

            // Shuffle answers so the correct answer is not always in the same position
            int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
            shuffleArray(answers);
            mainans=answers;

            // Create question
            question = String.format("What is the sum of %d + %d?", num1, num2);
        }

        // Display the question and options
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + String.valueOf(mainans[0]));
        btnOptionB.setText("B) " + String.valueOf(mainans[1]));
        btnOptionC.setText("C) " + String.valueOf(mainans[2]));
        btnOptionD.setText("D) " + String.valueOf(mainans[3]));
    }
    public void mathPuzzleGenerator(String difficultyLevel) {
        Random random = new Random();
        int num1 = 0, num2 = 0, num3 = 0;
        correctAnswer=0;
        String question = "";

        // Decide which type of puzzle to generate
        int puzzleType = random.nextInt(3); // 0: Arithmetic, 1: Number Pattern, 2: Logic Puzzle

        // Generate numbers based on difficulty level
        switch (difficultyLevel.toLowerCase()) {
            case "easy":
                num1 = random.nextInt(10) + 1; // Random number from 1 to 10
                num2 = random.nextInt(5) + 1;  // Random number from 1 to 5
                break;
            case "medium":
                num1 = random.nextInt(30) + 10; // Random number from 10 to 30
                num2 = random.nextInt(10) + 1;  // Random number from 1 to 10
                break;
            case "hard":
                num1 = random.nextInt(50) + 20; // Random number from 20 to 50
                num2 = random.nextInt(20) + 5;  // Random number from 5 to 20
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        int[] mainans;

        switch (puzzleType) {
            case 0: // Arithmetic Puzzle
                // Generate a simple arithmetic puzzle (e.g., addition, subtraction, multiplication, or division)
                int operator = random.nextInt(4);  // 0: addition, 1: subtraction, 2: multiplication, 3: division
                switch (operator) {
                    case 0: // Addition
                        correctAnswer = num1 + num2;
                        question = String.format("What is %d + %d?", num1, num2);
                        break;
                    case 1: // Subtraction
                        correctAnswer = num1 - num2;
                        question = String.format("What is %d - %d?", num1, num2);
                        break;
                    case 2: // Multiplication
                        correctAnswer = num1 * num2;
                        question = String.format("What is %d * %d?", num1, num2);
                        break;
                    case 3: // Division
                        correctAnswer = num1 / num2;
                        question = String.format("What is %d / %d?", num1, num2);
                        break;
                }
                break;

            case 1: // Number Pattern Puzzle (Arithmetic Progression)
                // Generate a simple number pattern (Arithmetic progression)
                int difference = num2;
                int start = num1;
                correctAnswer = start + (difference * 4); // 5th term in the series
                question = String.format("What is the next number in the sequence: %d, %d, %d, %d, ... ?",
                        start, start + difference, start + (difference * 2), start + (difference * 3));
                break;

            case 2: // Logic Puzzle
                // Generate a logic puzzle (e.g., finding the missing number in a sequence)
                num3 = random.nextInt(100) + 1;  // Random number for logic puzzle
                correctAnswer = num1 + num2 + num3; // Example logic: sum of the three numbers
                question = String.format("What is the sum of %d, %d, and %d?", num1, num2, num3);
                break;
        }

        // Generate 3 random incorrect answers
        int incorrectAnswer1 = correctAnswer + random.nextInt(10) + 1;
        int incorrectAnswer2 = correctAnswer - random.nextInt(10) - 1;
        int incorrectAnswer3 = correctAnswer + random.nextInt(20) + 1;

        // Shuffle answers so the correct answer is not always in the same position
        int[] answers = {correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3};
        correctAnswer=answers[0];
        shuffleArray(answers);
        mainans = answers;

        // Display the question and options
        tvQuestion.setText(question);
        btnOptionA.setText("A) " + String.valueOf(mainans[0]));
        btnOptionB.setText("B) " + String.valueOf(mainans[1]));
        btnOptionC.setText("C) " + String.valueOf(mainans[2]));
        btnOptionD.setText("D) " + String.valueOf(mainans[3]));
    }



    // Helper method to shuffle the array of answers
    private static void shuffleArray(String[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int j = random.nextInt(array.length);
            String temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    // Helper method to shuffle the array of answers
    private static void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int j = random.nextInt(array.length);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

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
    private void showScoreDialog(final Context context, final int totalScore, final int set) {
        // Calculate the percentage score
        int percentage = (totalScore * 100) / 10; // Assuming the score is out of 10

        // Prepare the score message
        String scoreMessage = "Your score for Set " + set + " is: " + totalScore + " out of 10\n" +
                "Which is " + percentage + "%";

        // Inflate the custom layout for the dialog
        View customView = LayoutInflater.from(context).inflate(R.layout.dialog_score, null);

        // Set the score message in the dialog
        TextView messageTextView = customView.findViewById(R.id.dialog_message);
        messageTextView.setText(scoreMessage);

        // Create the dialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView) // Set the custom layout
                .setCancelable(false)  // Disable dialog dismissal by tapping outside
                .setPositiveButton(null, null);  // Disable the default positive button

        // Show the dialog
        AlertDialog alertDialog = builder.create();

        // Set the background of the dialog window programmatically
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        // Find the OK button and set its click listener
        Button okButton = customView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(v -> {
            alertDialog.dismiss(); // Close the dialog when "OK" is clicked
        });

        alertDialog.show(); // Show the dialog
    }




    @Override
    protected void onPause() {
        super.onPause();

        // Stop and release the media player when the page is closed or paused
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
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
    private void colorcorrecti() {
        if (btnOptionA.getText().toString().substring(3).equals(String.valueOf(correctAnswer))) {
            btnOptionA.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        } else if (btnOptionB.getText().toString().substring(3).equals(String.valueOf(correctAnswer))) {
            btnOptionB.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else if (btnOptionC.getText().toString().substring(3).equals(String.valueOf(correctAnswer))) {
            btnOptionC.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else if (btnOptionD.getText().toString().substring(3).equals(String.valueOf(correctAnswer))) {
            btnOptionD.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }
    private void colorcorrectf() {
        if (btnOptionA.getText().toString().substring(3).equals(correctAnswerr)) {
            btnOptionA.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        } else if (btnOptionB.getText().toString().substring(3).equals(correctAnswerr)) {
            btnOptionB.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else if (btnOptionC.getText().toString().substring(3).equals(correctAnswerr)) {
            btnOptionC.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        } else if (btnOptionD.getText().toString().substring(3).equals(correctAnswerr)){
            btnOptionD.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }


}
