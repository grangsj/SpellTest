/**
 * Filename:  WordListBuilderActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents a word entry screen for the application.  This allows users to
 * build new spelling word lists or edit existing word lists.
 *
 */

package com.example.spelltest;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class WordListBuilderActivity extends AppCompatActivity {

    //Class variables
    public static final String EXTRA_LIST_ID = "com.example.spelltest.list_id"; //Tag for Extra transmission (ie identification of spelling list)
    private static final String EMPTY_STRING="";                                //Empty string

    //Instance variables
    private long mListId = DataStore.NULL_ROW_ID;                               //id of the Spelling List we're editing
    private static final String TAG = "WordListBuilderActivity";                //Tag to identify class during debugging
    private RecyclerView mRecyclerView;                                         //Reference to the RecyclerView for this activity
    private EditAdapter mAdapter;                                               //Data adapter for the RecyclerView
    private DataStore mData;                                                    //Reference to application datastore

    /**
     * Method called by Android when the activity is first created.  We use this to inflate the layout
     * for the activity and wire up most of the views and buttons.
     * @param savedInstanceState a Bundle with saved state information (not used in this app).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Call the super constructor
        super.onCreate(savedInstanceState);

        //Inflate the layout for the activity, and set the activity's view to this inflated layout.
        setContentView(R.layout.activity_word_list_builder);

        //Retain the link to the RecyclerView, and set the layout manager for ut.
        mRecyclerView = (RecyclerView) findViewById(R.id.word_list_builder_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get the list id (send from the ListSelectionActivity class), then get the list of spelling words from the DataStore
        mListId = getIntent().getLongExtra(EXTRA_LIST_ID, DataStore.NULL_ROW_ID);
        mData = DataStore.newInstance(this);
        ArrayList<Objects.Word> wordList = mData.getWords(mListId);

        //Instantiate the adapter for the RecyclerView, and link it to the REcyclerView
        mAdapter = new EditAdapter(this, wordList);
        mRecyclerView.setAdapter(mAdapter);

        //Wire up the "add word" button
        FloatingActionButton fab = findViewById(R.id.word_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a new blank word object
                Objects.Word word = new Objects.Word(
                        DataStore.NULL_ROW_ID,
                        mListId,
                        EMPTY_STRING
                );

                //Add the new word (with the correct id) to the datastore and to the adapter as well, so it can be shown to the user.
                mAdapter.addWord(word);
            }
        });

    }
}
