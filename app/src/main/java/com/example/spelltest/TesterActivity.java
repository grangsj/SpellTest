/**
 * Filename:  TesterActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the screen through which the spelling test is conducted.  The
 * class will include a representation of a keyboard, along with an area into which the user can
 * type in a spelling word.  The class will emit the audio representation of a word and will verify
 * that a user’s entered spelling matches the spelling of that word stored in the application’s
 * persistent storage.
 */


package com.example.spelltest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class TesterActivity extends AppCompatActivity {

    //Class variables
    public static final String EXTRA_LIST_ID = "com.example.spelltest.list_id"; //Extra tag for the list ID to be tested.
    private static final String TAG = "TesterActivity";                         //Tag for debug logs on this class.
    private static final int SHOW_WORD_RESULTS_DURATION = 3000;                 //Time (in ms) to display correct/incorrect info before moving to next word

    //Instance variables
    private long mListId = DataStore.NULL_ROW_ID;       //The id of the list we're testing on.
    private EditText mWordView;                         //The input field in which the user enters the spelling of the word.
    private TextView mCorrectWordSpellingView;          //Display field for correct spelling (normally hidden from user)
    private ArrayList<Objects.Word> mWords;             //List of words remaining to be tested.
    private int mNumberCorrect = 0;                     //Number of correct answers given in this test.
    private int mNumberWrong = 0;                       //Number of incorrect answers given in this test.
    private Objects.Word mCurrentWord = null;           //Word object for the word currently being tested.
    private Random mRandom = new Random();              //Random number generator
    private TextToSpeech mTextToSpeech = null;          //Reference to the Android Text-To-Sppech instance for the class
    private boolean mIsTtsInitialized = false;          //Flag to determine if the TTS element has been initialized.
    private long mStartTime = 0L;                       //Start time of the test
    private AlertDialog mDialog;                        //Dialog box for when the user attempts to abort a test.

    //Timer / handler, to allow screen to be reset after correct / incorrect results are shown.
    private Handler handler = new Handler();
    private Runnable timer = new Runnable(){
        @Override
        public void run() {
            //Reset input field color
            mWordView.setTextColor(Color.BLACK);

            //Hide the "correct spelling" view element (if showing - normally hidden unless user puts a wrong answer in)
            mCorrectWordSpellingView.setVisibility(View.GONE);

            if (mWords.size() > 0) {             //more words left
                selectNextWord();
            } else {                            //No more words left!  Show summary screen
                long endTime = System.currentTimeMillis();

                Objects.SpellingListStat stat = new Objects.SpellingListStat(
                        DataStore.NULL_ROW_ID,
                        mListId,
                        System.currentTimeMillis(),
                        endTime - mStartTime,
                        mNumberCorrect,
                        mNumberWrong
                );

                DataStore data = DataStore.newInstance(TesterActivity.this);
                long statId = data.putSpellingListStat(stat);


                Intent i = new Intent(TesterActivity.this, TestStatsActivity.class);
                i.putExtra(TestStatsActivity.EXTRA_TEST_STAT_ID, statId);
                startActivity(i);
                finish();    //Prevents user from coming back to this screen via back button
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        mListId = getIntent().getLongExtra(this.EXTRA_LIST_ID, DataStore.NULL_ROW_ID);
        DataStore data = DataStore.newInstance(this);
        mWords = data.getWords(mListId);


        mWordView = (EditText) findViewById(R.id.test_activity_input);
        mWordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    handleWordSubmitted();
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) onBackPressed();

                return true;
            }
        });

        mCorrectWordSpellingView = findViewById(R.id.test_activity_correct_spelling);
        mCorrectWordSpellingView.setTextColor(Color.BLUE);


        FloatingActionButton speakButton = (FloatingActionButton) findViewById(R.id.say_word_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sayCurrentWord();
            }
        });

        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = mTextToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                    mTextToSpeech.setSpeechRate(0.5f);
                    mIsTtsInitialized = true;
                    sayCurrentWord();

                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }


            }
        });



        selectNextWord();
        mStartTime = System.currentTimeMillis();

    }

    @Override
    protected void onPause() {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);


        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        inputManager.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    /**
     * Private method to select and say the next word and set up input to receive word.
     */
    private void selectNextWord() {
        Log.i(TAG, "In showNextWord, requesting focus for wordView");
        mWordView.setText("");
        mWordView.requestFocus();

        int size = mWords.size();

        if (size > 0) {
            int currentWordIndex = mRandom.nextInt(mWords.size());
            mCurrentWord = mWords.remove(currentWordIndex);
        }

        sayCurrentWord();

    }

    /**
     * Private method to say the current word.
     */
    private void sayCurrentWord() {

        if (mIsTtsInitialized) {
            mTextToSpeech.speak(mCurrentWord.spelling, TextToSpeech.QUEUE_FLUSH, null, null);
        }


    }

    private void handleWordSubmitted() {
        String userSpelling = mWordView.getText().toString().trim();
        if (userSpelling.equalsIgnoreCase(mCurrentWord.spelling)) {   //correct answer
            mNumberCorrect++;
            mWordView.setTextColor(Color.GREEN);
        } else {   //incorrect answer
            mNumberWrong++;
           mWordView.setTextColor(Color.RED);
           mCorrectWordSpellingView.setText("(" + mCurrentWord.spelling+")");
           mCorrectWordSpellingView.setVisibility(View.VISIBLE);


        }

        handler.postDelayed(timer, SHOW_WORD_RESULTS_DURATION);


    }

    @Override
    public void onStop() {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        super.onStop();
    }


    @Override
    public void onBackPressed() {

        Log.i(TAG, "in onBackPressed()");

        mDialog = new AlertDialog.Builder(TesterActivity.this)
                .setMessage("Do you want to stop the current test?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DataStore data = DataStore.newInstance(TesterActivity.this);
                        Objects.SpellingList list = data.getSpellingList(mListId);
                        long userId = list.userId;

                        Intent intent = new Intent(TesterActivity.this, ListSelectionActivity.class);
                        intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, userId);
                        startActivity(intent);
                        TesterActivity.this.finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

}
