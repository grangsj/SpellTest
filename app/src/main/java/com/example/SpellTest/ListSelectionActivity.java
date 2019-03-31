package com.example.SpellTest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ListSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "com.example.spelltest.user_id";

    private RecyclerView mRecyclerView;
    private ListSelectionActivity.ListDataAdapter mAdapter;
    private long mUserId;

    private class ListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView mTextView;
        private Objects.SpellingList mSpellingList;

        public ListViewHolder(@NonNull View v) {
            super(v);
            mTextView = (TextView) v;
            v.setOnClickListener(this);
        }

        public void bindSpellingList(Objects.SpellingList list){
            mSpellingList = list;
            mTextView.setText(mSpellingList.name);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListSelectionActivity.this, TesterActivity.class);
            intent.putExtra(TesterActivity.EXTRA_LIST_ID, mSpellingList.id);
            startActivity(intent);
        }
    }


    private class ListDataAdapter extends RecyclerView.Adapter {
        private ArrayList<Objects.SpellingList> mSpellingLists;
        private DataStore mData;


        public ListDataAdapter(Context context) {
            mData = DataStore.newInstance(context);
            refreshData();
        }

        public void refreshData(){
            mSpellingLists = mData.getSpellingLists(mUserId);
        }


        @NonNull
        @Override
        public ListSelectionActivity.ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(ListSelectionActivity.this);
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            return new ListSelectionActivity.ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            ((ListSelectionActivity.ListViewHolder)viewHolder).bindSpellingList(mSpellingLists.get(i));
        }


        @Override
        public int getItemCount() {
            return mSpellingLists.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_chooser);

        //Get the user id (send from the UserSelectionActivity class)
        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, DataStore.DEFAULT_ID);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_chooser_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ListSelectionActivity.ListDataAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.list_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordListNameFragment newFragment = WordListNameFragment.newInstance(mUserId);
                newFragment.setDialogListener(new DialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        mAdapter.refreshData();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        //Not used in this case.
                    }
                });



                newFragment.show(getSupportFragmentManager(), null);
            }
        });






    }


}
