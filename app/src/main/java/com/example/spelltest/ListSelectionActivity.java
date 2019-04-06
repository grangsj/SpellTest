/**
 * Filename:  ListSelectionActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the word list selection screen for the application.  This
 * class creates and displays a list of all spelling word lists created for a specific user.
 * This class provides functionality to add new spelling word lists and edit existing lists.
 */



package com.example.spelltest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ListSelectionActivity extends AppCompatActivity {

    //Static class variables
    public static final String EXTRA_USER_ID = "com.example.spelltest.user_id";  //Used to pass Extras into this Activity
    public static final String TAG = "ListSelectionActivity";       //Used to format Logcat entries (for debugging)

    //Instance variables
    private ListSelectionActivity.ListDataAdapter mAdapter; //The data adapter for this activity.
    private long mUserId;                                   //The used ID for which spelling lists are being displayed.

    /**
     * Private ViewHolder class for the activity.  This class represents each item in the
     * activity's RecyclerView.  Data is passed to each of these ViewHolder items via the
     * private ListDataAdapter class instance.
     */
    private class ListViewHolder extends RecyclerView.ViewHolder {

        //Instance variables (ie one of these exists for each of the items shown in the activity.)
        private TextView mTextView;                 //The main TextView in the item
        private ImageView mEditImage;               //The image button in the item (ie the edit button)
        private Objects.SpellingList mSpellingList; //The data object for which data is displayed in this list item.

        /**
         * Constructor
         * @param v the inflated - but as yet unpopulated - view for this item.  This contains a TextView and
         *          an Imagebutton, but these objects aren't wired to anything until after this constructor is called.
         */
        public ListViewHolder(@NonNull View v) {

            //Call the super constructor to inflate the view
            super(v);

            //Save the text view and the image view for later use.
            mTextView = v.findViewById(R.id.spelling_list_item_text);
            mEditImage = v.findViewById(R.id.spelling_list_item_edit);
            Log.i(TAG,"mTextView assignment statement executed");

            //Set an onClick listener for the text view.  Note that the mSpellingList variable will be
            //"bound" with this view via an onBindViewHolder call immediately before the activity is displayed.
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Crate an Intent to call the TesterActivity.
                    Intent intent = new Intent(ListSelectionActivity.this, TesterActivity.class);

                    //Add an EXTRA to the activity (ie the id of the spelling list to be used during the test.)
                    //The TesterActivity object will use this value to call up the list of words to test.
                    intent.putExtra(TesterActivity.EXTRA_LIST_ID, mSpellingList.id);

                    //Start the activity!
                    startActivity(intent);
                }
            });

            //Set an onClick listener for the image view.  Note again that the mSpellingList variable will be
            //"bound" with this view via an onBindViewHolder call immediately before the activity is displayed.
            mEditImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Crate an Intent to call the WordListBuilderActivity.
                    Intent intent = new Intent(ListSelectionActivity.this, WordListBuilderActivity.class);

                    //Add an EXTRA to this activity (ie the id of the spelling list to edit.)  The
                    //WordListBuilderActivity will use this id to show the correct set of words.
                    intent.putExtra(WordListBuilderActivity.EXTRA_LIST_ID, mSpellingList.id);

                    //Start the activity.
                    startActivity(intent);
                }
            });
        }

        /**
         * Public method to "bind" a specific SpellingList object to this view.  This method is called in
         * the ViewHolder's onBindViewHolder activity, which is called shortly after the ViewHolder
         * is created.  We will use this method to retain the object data for later use, as well as to
         * update the text in the view with the name of the spelling list.
         *
         * @param list  the SpellingList object for which data will be shown in this list item.
         */
        public void bindSpellingList(Objects.SpellingList list){

            Log.i(TAG, "In BindSpellingList");

            //Save the SpellingList object for later use.
            mSpellingList = list;

            //Assuming that there are no issues with the TextView inflation, set the text to be the name of the list.
            if (mTextView != null)  mTextView.setText(mSpellingList.name);
        }
    }


    /**
     * Private inner class for the DataAdapter for the parent RecyclerView.  This class is the "tool" used
     * by Android to attach data to each item in a RecyclerView.  Only one of these exists for each instance'
     * of the parent RecyclerView activity.
     */
    private class ListDataAdapter extends RecyclerView.Adapter {

        //Instance variables.
        private ArrayList<Objects.SpellingList> mSpellingLists;     //An array of all the spelling list objects for the current user.
        private DataStore mData;                                    //A reference to the main application datastore.


        /**
         * Class constructor.
         * @param context is the context object to which this Adapter is bound.
         */
        public ListDataAdapter(Context context) {

            //Save the DataStore object reference for later use.
            mData = DataStore.newInstance(context);

            //Populate the ArrayLIst object for this class (ie update the list of spelling lists for the current user.)
            refreshData();
        }

        /**
         * Private convenience method to update the list of spelling lists for the activity.  This was easier
         * to read than the code (at least for me :-))
         */
        private void refreshData(){

            //Update the list of spelling lists for the activity, by querying the list from the DataStore object.
            mSpellingLists = mData.getSpellingLists(mUserId);
        }

        /**
         * Method called when a specific list item needs to be shown on the screen.  We will use this
         * method to inflate the item view only - populating it with data is done later (in onBindViewHolder).
         *
         * @param viewGroup the parent RecyclerView to which this list item will be attached.
         * @param i  the element in the backing array represented by this specific list item.
         * @return the ListViewHolder item for the desired item in the list.
         */
        @NonNull
        @Override
        public ListSelectionActivity.ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            Log.i(TAG, "in onCreateViewHolder");
            LayoutInflater layoutInflater = LayoutInflater.from(ListSelectionActivity.this);
            View view = layoutInflater.inflate(R.layout.item_spelling_list, viewGroup, false);
            Log.i(TAG, "Layout inflated");
            return new ListSelectionActivity.ListViewHolder(view);
        }

        /**
         * Public method to bind an inflated ViewHolder to its backing data.  This is where we update the
         * text field in each item, and we also allow the ViewHolder to save a copy of its own backing object
         * for its own later use.
         *
         * @param viewHolder the ListViewHolder for which data needs to be added.
         * @param i The specific item in the backing array from which the text in this ViewHolder needs to be updated.
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            Log.i(TAG, "in BindViewHolder");

            //Pass the call to the ListViewHolder's bindSpellingList method, which will populate the view
            //and will also save the object for later.
            ((ListSelectionActivity.ListViewHolder)viewHolder).bindSpellingList(mSpellingLists.get(i));
        }

        /**
         * Method to return a count for the total number of items in the array.  Android requires this
         * method to be overridden for derived classes of ListAdapter.
         *
         * @return the total number of items in the array.
         */
        @Override
        public int getItemCount() {

            //Just return the size of the backing array.
            return mSpellingLists.size();
        }
    }

    /**
     * Public method that is called when the RecyclerView is first created.  This must be overridden
     * in derived Activity classes.  We use this method to inflate the main view and set the ListAdapter
     * for the view.  (The ListAdapter will then call the ViewHolder for this view as each item
     * in the list is displayed in the screen.)
     *
     * @param savedInstanceState a Bundle that may contain saved state for the Activity (not used in this application).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Call the super class constructor
        super.onCreate(savedInstanceState);

        //Set and inflate the view.
        setContentView(R.layout.activity_word_list_chooser);

        //Get the user id (send from the UserSelectionActivity class)
        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, DataStore.NULL_ROW_ID);

        //Get a link to the RecyclerView for the activity.
        RecyclerView recyclerView = findViewById(R.id.list_chooser_recycler_view);

        //Set a linear layout manager for this activity
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Instantiate the adapter for the activity, and link it with the RecyclerView parent.
        mAdapter = new ListSelectionActivity.ListDataAdapter(this);
        recyclerView.setAdapter(mAdapter);

        //Get a link to the floating action button, and set an onClickListener for it (for adding new lists)
        FloatingActionButton fab = findViewById(R.id.list_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instantiate a new WordListNameFragment to show the list name dialog box.
                WordListNameFragment newFragment = WordListNameFragment.newInstance(mUserId);

                //Set a new dialog listener for this dialog box - code will be called when the dialog buttons are clicked.
                newFragment.setDialogListener(new DialogListener() {
                    @Override
                    public void onDialogPositiveClick(long listId) {

                        //Update list in adapter.  Note that the dialog box will add the new list to the app database before this call occurs.
                        mAdapter.refreshData();

                        //Update the adapter that we have a new spelling list to include in the main list.
                        mAdapter.notifyDataSetChanged();

                        //Show word list editor, by creating an intent for the word list editor activity.
                        Intent intent = new Intent(ListSelectionActivity.this, WordListBuilderActivity.class);

                        //Put an Extra on the intent, to allow us to pass along the new spelling list id to the list builder
                        //(ie so the list builder knows what list of words to show!)
                        intent.putExtra(WordListBuilderActivity.EXTRA_LIST_ID, listId);

                        //Start the activity
                        startActivity(intent);
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        //Not used in this case.
                    }
                });


                //Now that the dialog box has been instantiated and wired up, show the dialog box...
                newFragment.show(getSupportFragmentManager(), null);
            }
        });






    }


}
