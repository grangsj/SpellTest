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

public class TesterActivity extends AppCompatActivity implements View.OnKeyListener {

    //Class variables
    public static final String EXTRA_LIST_ID = "com.example.spelltest.list_id"; //Extra tag for the list ID to be tested.
    private static final String TAG = "TesterActivity";                         //Tag for debug logs on this class.
    private static final int DURATION_SHOW_WORD_RESULTS = 3000;                 //Time (in ms) to display correct/incorrect info before moving to next word
    private static final float SPEECH_RATE = 0.5f;                              //Rate at which word is spoken (1.0 = normal speed).
    private static final int DURATION_CLOSE_ACTIVITY = 1000;                    //Time delay(in ms) to close activity, to allow time for dialogs to close first.

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
    private Runnable timerHandleNextWord = new Runnable(){
        @Override
        public void run() {
            //Reset input field color
            mWordView.setTextColor(Color.BLACK);

            //Hide the "correct spelling" view element (if showing - normally hidden unless user puts a wrong answer in)
            mCorrectWordSpellingView.setVisibility(View.GONE);

            //If more words are lift, pick another one and show it.
            if (mWords.size() > 0) {             //more words left
                selectNextWord();
                sayCurrentWord();

                //Otherwise, terminate this activity and show stats.
            } else {

                //Stop the test timer.
                long endTime = System.currentTimeMillis();

                //Create a new spelling list stat object, with the stats from this list.
                Objects.SpellingListStat stat = new Objects.SpellingListStat(
                        DataStore.NULL_ROW_ID,
                        mListId,
                        System.currentTimeMillis(),
                        endTime - mStartTime,
                        mNumberCorrect,
                        mNumberWrong
                );

                //Get a link to the app datastore, and add this stat to the list.
                DataStore data = DataStore.newInstance(TesterActivity.this);
                long statId = data.putSpellingListStat(stat);

                //Create a new intent for the stat activity, and load the current stat id into it as an Extra.
                Intent i = new Intent(TesterActivity.this, TestStatsActivity.class);
                i.putExtra(TestStatsActivity.EXTRA_TEST_STAT_ID, statId);

                //Start the stat activity
                startActivity(i);

                //Prevents user from coming back to this screen via back button
                finish();
            }
        }
    };

    private Runnable timerCloseTest = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    /**
     * Method called by Android when the activity is first created.  This is where we inflate the
     * activity layout and initialize the screen.
     * @param savedInstanceState a Bundle with saved instance state for the activity (not used in this instance)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Run the super constructor.
        super.onCreate(savedInstanceState);

        //Inflate the view with this activity's layout, and attach the view to this activity.
        setContentView(R.layout.activity_tester);

        //Get the list ID from the extra that came with the intent
        mListId = getIntent().getLongExtra(this.EXTRA_LIST_ID, DataStore.NULL_ROW_ID);

        //Obtain a link to the app datastore, and use it to get an ArrayList of words that make up the spelling list.
        DataStore data = DataStore.newInstance(this);
        mWords = data.getWords(mListId);

        //Obtain a link to the EditText, and set this activity as a listener to respond to clicks.
        mWordView = (EditText) findViewById(R.id.test_activity_input);
        mWordView.setOnKeyListener(this);

        //Obtain a link to the (normally hidden) corrected spelling text box, set the color, and save it for later.
        mCorrectWordSpellingView = findViewById(R.id.test_activity_correct_spelling);
        mCorrectWordSpellingView.setTextColor(Color.BLUE);

        //Set the onClickListener for the floating action button.  This allows an audio pronuniciation of
        //a work to be repeated if needed.
        FloatingActionButton speakButton = (FloatingActionButton) findViewById(R.id.say_word_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If the button is clicked, call this method to say the word (again).
                sayCurrentWord();
            }
        });


        //Pick the next available word and save it.
        selectNextWord();

        //Start the test timer.
        mStartTime = System.currentTimeMillis();

    }

    /**
     * Method called by Android when the activity is being hidden (ie if another activity starts after this one.)
     * In that case, we'd like to shut off the keyboard and stop the TTS object.
     */
    @Override
    protected void onPause() {

        //Obtain a link to the input manager service and view, and hide the keyboard.
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);

        //If the TTS object was instantiated, close it out.
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mIsTtsInitialized = false;
        }


        //Call the super constructor.
        super.onPause();
    }

    /**
     * Method called by Android right before the activity is displayed.  In that case, we'd like to
     * force the keyboard to be shown without waiting for the user to tough the edit box on the screen first.
     * We will also use this to instantiate the TTS object.
     */
    @Override
    protected void onResume() {

        //Call the super constructor
        super.onResume();

        //Instantiate the "text to speech" object for Android.  This is started in the (anonymous) OnInitListener object
        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    //Set the locale to be US
                    int ttsLang = mTextToSpeech.setLanguage(Locale.US);

                    //Provide log errors in case the TTS cannot be started for some reason.
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");

                    //Set speech speed
                    mTextToSpeech.setSpeechRate(SPEECH_RATE);

                    //Set flag that TTS has been initialized.  We use this in onPause to destroy the instance if we bail out of the app.
                    mIsTtsInitialized = true;

                    //Say the current word.
                    sayCurrentWord();

                } else {

                    //If we get here, it means that TTS was not initialized properly.  Show a Toast in that case.
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //Obtain a link to the input method manager, and force the keyboard to display.
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Private method to select and say the next word and set up input to receive word.
     */
    private void selectNextWord() {
        Log.i(TAG, "In showNextWord, requesting focus for wordView");

        //Clear out the current word text.
        mWordView.setText("");

        //Set the window focus to be the edit text at the top of the screen (ie user input field).
        mWordView.requestFocus();

        //Get the size of the remaining word list
        int size = mWords.size();

        //If there are words left, grab the next random one, remove it from the word list, and save it as the current word.
        if (size > 0) {
            int currentWordIndex = mRandom.nextInt(size);
            mCurrentWord = mWords.remove(currentWordIndex);
        }
    }

    /**
     * Private method to say the current word.
     */
    private void sayCurrentWord() {

        //Make sure the TTS object was initialized, and (if so) use the speak() method to say the word.
        if (mIsTtsInitialized) {
            mTextToSpeech.speak(mCurrentWord.spelling, TextToSpeech.QUEUE_FLUSH, null, null);
        }


    }

    /**
     * Private method to process a user submission of a word spelling.
     */
    private void handleWordSubmitted() {

        //Get the user input, and trim off the white space.
        String userSpelling = mWordView.getText().toString().trim();

        //If the user got the correct answer...
        if (userSpelling.equalsIgnoreCase(mCurrentWord.spelling)) {   //correct answer
            mNumberCorrect++;                       //Increase the correct word count.
            mWordView.setTextColor(Color.GREEN);    //Show the word colored as green.

        //If the user gets the wrong answer.....
        } else {
            mNumberWrong++;                                                     //Increase the wrong answer county.
           mWordView.setTextColor(Color.RED);                                   //Show the user input as red (wrong)
           mCorrectWordSpellingView.setText("(" + mCurrentWord.spelling+")");   //Show the correct word spelling) and unhide the correctly spelled word.
           mCorrectWordSpellingView.setVisibility(View.VISIBLE);


        }

        //Wait for a short period, and then initiate the timer code to decide what happens after the delay.
        handler.postDelayed(timerHandleNextWord, DURATION_SHOW_WORD_RESULTS);


    }

    /**
     * Method called by Android if the back button is pressed on the device.  We will use this to show a dialog if
     * the user tries to use the back button to bail out of the app.
     */
    @Override
    public void onBackPressed() {

        Log.i(TAG, "in onBackPressed()");

        //Create a simple dialog box with an OK and Cancel button.
        mDialog = new AlertDialog.Builder(TesterActivity.this)
                .setMessage("Do you want to stop the current test?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If the user his OK to stop test, get a link to teh DataStore and get the id
                        //of the current user.
                        DataStore data = DataStore.newInstance(TesterActivity.this);
                        Objects.SpellingList list = data.getSpellingList(mListId);
                        long userId = list.userId;

                        //Create an intent to show the ListSelectionActivity, and load it with the user ID as an extra
                        Intent intent = new Intent(TesterActivity.this, ListSelectionActivity.class);
                        intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, userId);

                        //Start the ListSelectionActivity
                        startActivity(intent);

                        //Wait for a short period to allow the dialog to close, then close this activity.
                        handler.postDelayed(timerCloseTest, DURATION_CLOSE_ACTIVITY);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If the user hits the cancel button, just dismiss the dialog and continue the test.
                        dialog.dismiss();
                    }
                })
                .show();        //Show the dialog

    }

    /**
     * Method called by Android whenever a hardware key is pressed on the device.  We use this to respond to user
     * presses of the back button or the enter key.
     *
     * @param v is the View object that receives the event.
     * @param keyCode the key that was pressed.
     * @param event     The ket action that was detected.
     * @return true if the method consumes the key press, and false otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        //If the user presses the enter key, assume we're done entering input - time to check spelling.
        if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            handleWordSubmitted();
        }

        //If the user presses the back button, go to the custom Back button method to show the dialog.
        if (keyCode == KeyEvent.KEYCODE_BACK) onBackPressed();

        //REturn true (we consume all hard key presses here).
        return true;
    }
}
