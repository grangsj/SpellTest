package com.example.spelltest;

import android.content.Context;
import android.content.Intent;
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

    private long mListId = DataStore.DEFAULT_ID;
    private static final String TAG = "WordListBuilderActivity";
    private RecyclerView mRecyclerView;
    private WordDataAdapter mAdapter;
    private DataStore mData;
    private Runnable runnableAdapterDataChange = new Runnable() {
        @Override
        public void run() {
            mAdapter.refreshData();
            mAdapter.notifyDataSetChanged();
        }
    };


    private class WordViewHolder extends RecyclerView.ViewHolder {

        private EditText mWordBox;
        private Objects.Word mWord;
        private ImageView mDeleteImage;


        public WordViewHolder(@NonNull View v) {
            super(v);
            mWordBox = v.findViewById(R.id.word_list_item_text);
            mWordBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.i(TAG, "in EditText, hasFocus = " + hasFocus+ " word=" + mWordBox.getText().toString());

                    if (!hasFocus){
                        mWord.spelling = mWordBox.getText().toString().trim();
                        mData.putWord(mWord);
                        handler.post(runnableAdapterDataChange);
                    }
                }
            });
            mDeleteImage = v.findViewById(R.id.word_list_item_delete);
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.deleteWord(mWord.id);
                    handler.post(runnableAdapterDataChange);

                }
            });
        }

        public void bindWord(Objects.Word word){
            mWord = word;
            mWordBox.setText(word.spelling);
        }

    }

    private class WordDataAdapter extends RecyclerView.Adapter {
        private ArrayList<Objects.Word> mWordList;
        private DataStore mData;


        public WordDataAdapter(Context context) {
            mData = DataStore.newInstance(context);
            refreshData();
        }

        public void refreshData(){

            mWordList = mData.getWords(mListId);
        }


        @NonNull
        @Override
        public WordListBuilderActivity.WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(WordListBuilderActivity.this);
            View view = layoutInflater.inflate(R.layout.item_word_list, viewGroup, false);
            Log.i(TAG, "Layout inflated");
            return new WordListBuilderActivity.WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            ((WordViewHolder)viewHolder).bindWord(mWordList.get(i));
            Log.i(TAG, "in onBindViewHolder, with word " + mWordList.get(i).spelling);
        }


        @Override
        public int getItemCount() {

            Log.i(TAG, "GetItemCount = " + mWordList.size());
            return mWordList.size();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_builder);



        //Get the list id (send from the ListSelectionActivity class)
        mListId = getIntent().getLongExtra(EXTRA_LIST_ID, DataStore.DEFAULT_ID);

        mData = DataStore.newInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.word_list_builder_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new WordDataAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.word_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.Word word = new Objects.Word(
                        DataStore.DEFAULT_ID,
                        mListId,
                        EMPTY_STRING
                );

                DataStore data = DataStore.newInstance(WordListBuilderActivity.this);
                data.putWord(word);
                handler.post(runnableAdapterDataChange);
            }
        });




    }
}
