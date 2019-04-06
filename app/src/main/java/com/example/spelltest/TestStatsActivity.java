/**
 * Filename:  TestStatsActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the screen by which the user can observe the overall results
 * summary from a single spelling test.
 */



package com.example.spelltest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestStatsActivity extends AppCompatActivity {

    //Class variables
    public static final String EXTRA_TEST_STAT_ID = "com.example.spelltest.list_stat_id";  //used to pass Extras into this Activity.

    //Instance variables
    private DataStore mData;
    private long mStatId;


    /**
     * Method called when the Activity is first created.  We use this method to inflate the views for the
     * activity, and to show the stat data and wire up the buttons on the screen.
     *
     * @param savedInstanceState is a Bundle of saved state information for the Activity (not used here)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Call the super class constructor first.
        super.onCreate(savedInstanceState);

        //Set the content view.  This will inflate all of the views for the Activity from the listed resource file.
        setContentView(R.layout.activity_test_stats);

        //Obtain and save the stat id for the activity - this should have been passed in via an Extra with the intent.
        mStatId = getIntent().getLongExtra(EXTRA_TEST_STAT_ID, DataStore.NULL_ROW_ID);

        //Obtain a link to the application datastore (to allow access to the stats for this activity.)
       mData = DataStore.newInstance(this);

        //Enclosing the rest of the code in an if statement, in case someone ever starts this activity without
        //sending the stat id as an intent - screen will be blank, which is better than FC'ing the app due to errors.
        if (mStatId != DataStore.NULL_ROW_ID) {

            //Obtain a reference to the stat object associated with the incoming stat id.
            Objects.SpellingListStat stat = mData.getSpellingListStat(mStatId);

            //Get references to each of the TextViews in the activity that need to be updated with real stat values.
            TextView dateView = findViewById(R.id.activity_test_stat_test_date);
            TextView elapsedTimeView = findViewById(R.id.activity_test_stat_elapsed_time);
            TextView correctAnswerCountView = findViewById(R.id.activity_test_stat_correct_answers);
            TextView incorrectAnswerCountView = findViewById(R.id.activity_test_stat_incorrect_answers);
            TextView overallGradeView = findViewById(R.id.activity_test_stat_overall_grade);

            //Populate the correct / incorrect answer counts.
            correctAnswerCountView.setText(Integer.toString(stat.numberCorrect));
            incorrectAnswerCountView.setText(Integer.toString(stat.numberIncorrect));

            //Format the "date tested" line of the stats, and display that next.
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            dateView.setText(df.format(new Date(stat.date)));

            //Format the "elapsed time" of the stats, and display that.
            DateFormat tf = new SimpleDateFormat("mm:ss", Locale.US);
            elapsedTimeView.setText(tf.format(new Date(stat.elapsedTime)));

            //Calculate the % correct, and show that on the screen.  Note that this is integer division,
            //so will not show decimals.
            long overallGrade = 100*(stat.numberCorrect) / (stat.numberCorrect + stat.numberIncorrect);
            overallGradeView.setText(overallGrade + "%");

            //Obtain a link to the list id and associated user id (used to wire up buttons).
            final long listId = stat.listId;
            final long userId = mData.getSpellingList(listId).userId;

            //Wire up the "try again" button, by getting a link to it and attaching an onClickListener.
            Button tryAgainButton = findViewById(R.id.activity_test_stat_button_try_again);
            tryAgainButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {

                    //Create an intent for the test activity.
                    Intent intent = new Intent(TestStatsActivity.this, TesterActivity.class);

                    //Send the spelling list id as an Extra.
                    intent.putExtra(TesterActivity.EXTRA_LIST_ID, listId);

                    //Start the activity
                    startActivity(intent);

                    //Terminate this activity, so the user can't get here by hitting back button on device.
                    finish();
                }
            });

            //Wire up the "select new list" button, by getting a link to it and attaching an onClickListener.
            Button selectNewListButton = findViewById(R.id.activity_test_stat_button_select_new_test);
            selectNewListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Create an intent for the spelling list selection activity.
                    Intent intent = new Intent(TestStatsActivity.this, ListSelectionActivity.class);

                    //Send the current user's id as an Extra.
                    intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, userId);

                    //Start the activity
                    startActivity(intent);

                    //Terminate this activity, so the user can't get here by hitting back button on device.
                    finish();
                }
            });

            //Wire up the "quit" button, by getting a link to it and attaching an onClickListener.
            Button quitButton = findViewById(R.id.activity_test_stat_button_quit);
            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Kill this application, and return the user to the device home screen.
                    finishAffinity();
                }
            });
        }
    }

    /**
     * Method called if the user presses the "back" button on the device while in this activity.
     * In this case, we want to go back to the spelling list selection screen.
     */
    @Override
    public void onBackPressed() {

        //Create an intent for the spelling list selection activity.
        Intent intent = new Intent(TestStatsActivity.this, ListSelectionActivity.class);

        //Get the current user id, and add it to the Intent as an Extra.  The ListSelectionActivity will
        //need this to show the correct list of spelling lists.
        final long userId = mData.getSpellingList(mData.getSpellingListStat(mStatId).listId).userId;
        intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, userId);

        //Start the activity
        startActivity(intent);

        //Terminate this activity, so the user can't get here by hitting back button on device.
        finish();

        }
}
