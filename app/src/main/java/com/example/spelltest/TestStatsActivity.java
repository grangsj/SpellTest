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

public class TestStatsActivity extends AppCompatActivity {

    public static final String EXTRA_TEST_STAT_ID = "com.example.spelltest.list_stat_id";
    private long mStatId;
    private DataStore mData;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_stats);

        mStatId = getIntent().getLongExtra(EXTRA_TEST_STAT_ID, DataStore.NULL_ROW_ID);
        mData = DataStore.newInstance(this);

        if (mStatId != DataStore.NULL_ROW_ID) {
            Objects.SpellingListStat stat = mData.getSpellingListStat(mStatId);
            TextView dateView = findViewById(R.id.activity_test_stat_test_date);
            TextView elapsedTimeView = findViewById(R.id.activity_test_stat_elapsed_time);
            TextView correctAnswerCountView = findViewById(R.id.activity_test_stat_correct_answers);
            TextView incorrectAnswerCountView = findViewById(R.id.activity_test_stat_incorrect_answers);
            TextView overallGradeView = findViewById(R.id.activity_test_stat_overall_grade);

            correctAnswerCountView.setText(Integer.toString(stat.numberCorrect));
            incorrectAnswerCountView.setText(Integer.toString(stat.numberIncorrect));

            DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            dateView.setText(df.format(new Date(stat.date)));

            DateFormat tf = new SimpleDateFormat("mm:ss");
            elapsedTimeView.setText(tf.format(new Date(stat.elapsedTime)));

            long overallGrade = 100*(stat.numberCorrect) / (stat.numberCorrect + stat.numberIncorrect);
            overallGradeView.setText(Long.toString(overallGrade) + "%");

            final long listId = stat.listId;
            final long userId = mData.getSpellingList(listId).userId;

            Button tryAgainButton = findViewById(R.id.activity_test_stat_button_try_again);
            tryAgainButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TestStatsActivity.this, TesterActivity.class);
                    intent.putExtra(TesterActivity.EXTRA_LIST_ID, listId);
                    startActivity(intent);
                    finish();
                }
            });

            Button selectNewListButton = findViewById(R.id.activity_test_stat_button_select_new_test);
            selectNewListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TestStatsActivity.this, ListSelectionActivity.class);
                    intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, userId);
                    startActivity(intent);
                    finish();
                }
            });

            Button quitButton = findViewById(R.id.activity_test_stat_button_quit);
            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });
        }





    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TestStatsActivity.this, ListSelectionActivity.class);
        startActivity(intent);

        //super.onBackPressed();
        }
}
