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

    public static final String EXTRA_LIST_ID = "com.example.spelltest.list_id";
    private static final String EMPTY_STRING="";
    private Handler handler = new Handler();

    private long mListId = DataStore.NULL_ROW_ID;
    private static final String TAG = "WordListBuilderActivity";
    private RecyclerView mRecyclerView;
    private EditAdapter mAdapter;
    private DataStore mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_builder);

        mRecyclerView = (RecyclerView) findViewById(R.id.word_list_builder_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get the list id (send from the ListSelectionActivity class)
        mListId = getIntent().getLongExtra(EXTRA_LIST_ID, DataStore.NULL_ROW_ID);

        mData = DataStore.newInstance(this);
        ArrayList<Objects.Word> wordList = mData.getWords(mListId);

        mAdapter = new EditAdapter(this, wordList);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.word_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.Word word = new Objects.Word(
                        DataStore.NULL_ROW_ID,
                        mListId,
                        EMPTY_STRING
                );

                DataStore data = DataStore.newInstance(WordListBuilderActivity.this);
                long id = data.putWord(word);
                word.id = id;

                mAdapter.addWord(word);
            }
        });




    }
}
