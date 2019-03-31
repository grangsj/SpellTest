package com.example.SpellTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class TesterActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_ID = "com.example.spelltest.list_id";
    private static final String TAG = "TesterActivity";

    private long mListId = DataStore.DEFAULT_ID;
    private EditText mWordView;
    private ArrayList<String> mWords;
    private int mNumberCorrect = 0;
    private int mNumberWrong = 0;
    private String mCurrentWord = null;
    private Random mRandom = new Random();
    private TextToSpeech mTextToSpeech = null;
    private boolean mIsTtsInitialized = false;
    private long mStartTime = 0L;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        mListId = getIntent().getLongExtra(this.EXTRA_LIST_ID, DataStore.DEFAULT_ID);
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
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        selectNextWord();
        mStartTime = System.currentTimeMillis();

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


        /**
         Button statsButton = findViewById(R.id.tester_button_test_stats);
         statsButton.setOnClickListener(new View.OnClickListener(){

        @Override public void onClick(View view) {
        Intent i = new Intent(TesterActivity.this, TestStatsActivity.class);
        startActivity(i);
        finish();    //Prevents user from coming back to this screen via back button
        }
        });
         **/
    }

    /**
     * Private method to say the current word.
     */
    private void sayCurrentWord() {

        if (mIsTtsInitialized) {
            mTextToSpeech.speak(mCurrentWord, TextToSpeech.QUEUE_FLUSH, null, null);
        }


    }

    private void handleWordSubmitted() {
        String userSpelling = mWordView.getText().toString().trim();
        if (userSpelling.equalsIgnoreCase(mCurrentWord)) {   //correct answer
            mNumberCorrect++;
            Toast.makeText(this, R.string.correct_answer, Toast.LENGTH_SHORT).show();
        } else {   //incorrect answer
            mNumberWrong++;
            Toast.makeText(this, R.string.incorrect_answer, Toast.LENGTH_SHORT).show();

        }

        if (mWords.size() > 0) {             //more words left
            selectNextWord();
        } else {                            //No more words left!  Show summary screen
            long endTime = System.currentTimeMillis();

            Objects.SpellingListStat stat = new Objects.SpellingListStat(
                    DataStore.DEFAULT_ID,
                    mListId,
                    System.currentTimeMillis(),
                    endTime - mStartTime,
                    mNumberCorrect,
                    mNumberWrong
            );

            DataStore data = DataStore.newInstance(this);
            long statId = data.putSpellingListStat(stat);


            Intent i = new Intent(TesterActivity.this, TestStatsActivity.class);
            i.putExtra(TestStatsActivity.EXTRA_TEST_STAT_ID, statId);
            startActivity(i);
            finish();    //Prevents user from coming back to this screen via back button
        }
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
