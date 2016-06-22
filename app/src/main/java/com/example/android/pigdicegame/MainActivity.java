package com.example.android.pigdicegame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private int score = 0, userScore = 0, userTurnScore = 0, compScore = 0, compTurnScore = 0;
    ImageView diceImage;
    Button rollButton, holdButton, resetButton;
    TextView scoreBoard, turnInfo;
    View myView;
    private int mWinScore = 100, mCompAiScore = 25;
    MediaPlayer mRollSound;

    //Clock Component
    private boolean mIsRunning;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(!mIsRunning) {
                return;
            }

            if(((compScore + compTurnScore) >= mWinScore) || (compTurnScore >= mCompAiScore)) {

                //Computer Holds
                if(mCompAiScore + (userScore - compScore) <= 40 && mCompAiScore + (userScore - compScore) >= 10) {
                    mCompAiScore += (userScore - compScore);
                }
                if(mCompAiScore + (userScore - compScore) <= 0) {
                    mCompAiScore = 10;
                }

                stopClock();
                Toast.makeText(getApplicationContext(), "Computer Holds", Toast.LENGTH_SHORT).show();
                compScore += compTurnScore;

                compTurnScore = 0;
                updateScoreBoard();
                rollButton.setEnabled(true);
                rollButton.setVisibility(View.VISIBLE);
                holdButton.setEnabled(true);
                holdButton.setVisibility(View.VISIBLE);
                turnInfo.setText("...YOUR TURN...");
            }
            else {
                //Computer Rolls
                    compTurn();
            }

            timerHandler.postDelayed(timerRunnable, 2000);
        }
    };

    //Method to start the clock
    void startClock() {
        mIsRunning = true;
        timerRunnable.run();
    }

    //Method to stop the clock
    void stopClock() {
        mIsRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRollSound = new MediaPlayer().create(getApplicationContext(), R.raw.dicesound);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diceImage = (ImageView) findViewById(R.id.diceImage);
        rollButton = (Button) findViewById(R.id.roll);
        holdButton = (Button) findViewById(R.id.hold);
        resetButton = (Button) findViewById(R.id.reset);
        scoreBoard = (TextView) findViewById(R.id.label1);
        turnInfo = (TextView) findViewById(R.id.turnLabel);
        myView = (View) findViewById(R.id.parentview);


        rollButton.setText("Start New Game");
        holdButton.setEnabled(false);
        holdButton.setVisibility(View.GONE);
        resetButton.setEnabled(false);
        holdButton.setText("Hold");
        resetButton.setText("Reset");
        resetButton.setVisibility(View.GONE);

        mCompAiScore = 25;

        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scoreBoard.setGravity(Gravity.CENTER_HORIZONTAL);
                rollButton.setText("Roll");
                holdButton.setText("Hold");
                resetButton.setText("Reset");
                holdButton.setEnabled(true);
                holdButton.setVisibility(View.VISIBLE);
                resetButton.setEnabled(true);
                resetButton.setVisibility(View.VISIBLE);
                diceImage.setAlpha(255);
                myView.setBackgroundResource(R.drawable.background);
                userTurn();
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diceImage.setAlpha(255);
                userScore += userTurnScore;
                updateScoreBoard();
                userTurnScore = 0;
                startClock();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holdButton.setText("Hold");
                resetButton.setText("Reset");
                rollButton.setVisibility(View.VISIBLE);
                rollButton.setEnabled(true);
                rollButton.setText("Start New Game");
                holdButton.setEnabled(false);
                holdButton.setVisibility(View.GONE);
                resetButton.setEnabled(false);
                resetButton.setVisibility(View.GONE);

                stopClock();
                score = userScore = userTurnScore = compScore = compTurnScore = 0;
                mCompAiScore = 25;

                scoreBoard.setText("Let the Game Begin!\nPress Start New Game to start.");
                diceImage.setAlpha(0);
                myView.setBackgroundResource(R.drawable.background);
                //resetButton.setText("Reset");
                turnInfo.setText("");
            }
        });

        scoreBoard.setText("Let the Game Begin!\nPress Start New Game to start.");
        diceImage.setAlpha(0);
        myView.setBackgroundResource(R.drawable.background);
        turnInfo.setText("");
        score = 0;
    }

    @Override
    public void onDestroy() {
        mRollSound.stop();
        super.onDestroy();
    }

    public static int randomNumberWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static int diePicker() {
        return randomNumberWithRange(1, 6);
    }

    public int setDiceImage() {
        int total = 0;
        int turn = diePicker();

        //TODO mRollSound.start();
        //mRollSound.start();

        switch (turn) {

            case 1:
                diceImage.setImageResource(R.drawable.dice1);
                Toast.makeText(getApplicationContext(), "Oh! It's an One!", Toast.LENGTH_SHORT).show();
                total = 0;
                break;

            case 2:
                diceImage.setImageResource(R.drawable.dice2);
                total += turn;
                break;

            case 3:
                diceImage.setImageResource(R.drawable.dice3);
                total += turn;
                break;

            case 4:
                diceImage.setImageResource(R.drawable.dice4);
                total += turn;
                break;

            case 5:
                diceImage.setImageResource(R.drawable.dice5);
                total += turn;
                break;

            case 6:
                diceImage.setImageResource(R.drawable.dice6);
                total += turn;
                break;
        }
        //mRollSound.stop();
        return total;
    }

    public void userTurn() {
        turnInfo.setText("...YOUR TURN...");
        score = setDiceImage();
        mRollSound.start();
        updateScoreBoard();
        if(score == 0) {
            userTurnScore = 0;
            startClock();
        }
        else {
            userTurnScore += score;
            updateScoreBoard();
        }
    }

    public void compTurn() {
        turnInfo.setText("...COMPUTER'S TURN...");
        rollButton.setEnabled(false);
        rollButton.setVisibility(View.GONE);
        holdButton.setEnabled(false);
        holdButton.setVisibility(View.GONE);

        score = setDiceImage();
        mRollSound.start();
        if(score == 0) {
            compTurnScore = 0;
            stopClock();
            updateScoreBoard();
            rollButton.setEnabled(true);
            rollButton.setVisibility(View.VISIBLE);
            holdButton.setEnabled(true);
            holdButton.setVisibility(View.VISIBLE);
            turnInfo.setText("...YOUR TURN...");
        }
        else {
            compTurnScore += score;
            updateScoreBoard();
        }
    }

    public void updateScoreBoard() {
        if(userScore >= mWinScore) {
            rollButton.setVisibility(View.GONE);
            rollButton.setEnabled(false);
            holdButton.setVisibility(View.GONE);
            holdButton.setEnabled(false);
            resetButton.setText("New Game");
            diceImage.setAlpha(0);
            myView.setBackgroundResource(R.drawable.gameover);
            turnInfo.setText("Congratulations! YOU WON !!");
            stopClock();
        }
        else if(compScore >= mWinScore) {

            stopClock();
            rollButton.setVisibility(View.GONE);
            rollButton.setEnabled(false);
            holdButton.setVisibility(View.GONE);
            holdButton.setEnabled(false);
            resetButton.setText("New Game");
            diceImage.setAlpha(0);
            myView.setBackgroundResource(R.drawable.gameover);
            turnInfo.setText("You Lose! Try Again!");
        }
        scoreBoard.setText("YOUR SCORE : " + userScore + " \nCOMPUTER'S SCORE : " + compScore + "\nYour Turn Score: " + userTurnScore
                + "\nComputer's Turn Score: " + compTurnScore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
