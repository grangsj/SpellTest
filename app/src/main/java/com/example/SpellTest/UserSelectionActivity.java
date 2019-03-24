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

public class UserSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "com.example.spelltest.user_id";

    private RecyclerView mRecyclerView;
    private UserDataAdapter mAdapter;

    private class UserViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView mTextView;
        private Objects.User mUser;

        public UserViewHolder(@NonNull View v) {
            super(v);
            mTextView = (TextView) v;
            v.setOnClickListener(this);
        }

        public void bindUser(Objects.User user){
            mUser = user;
            mTextView.setText(mUser.firstName + " " + mUser.lastName);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UserSelectionActivity.this, WordListChooserActivity.class);
            intent.putExtra(EXTRA_USER_ID, mUser.id);
            startActivity(intent);
        }
    }


    private class UserDataAdapter extends RecyclerView.Adapter {
        private ArrayList<Objects.User> users;
        private DataStore mData;


        public UserDataAdapter(Context context) {
            mData = DataStore.getDataStore(context);
            refreshData();
        }

        public void refreshData(){
            users = mData.getAllUsers();
        }


        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(UserSelectionActivity.this);
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            ((UserViewHolder)viewHolder).bindUser(users.get(i));
        }


        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        mRecyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserDataAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.user_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewUserFragment newFragment = new NewUserFragment();
                newFragment.setDialogListener(new NewUserFragment.UserDialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        mAdapter.refreshData();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                newFragment.show(getFragmentManager(), null);
            }
        });






    }

}
