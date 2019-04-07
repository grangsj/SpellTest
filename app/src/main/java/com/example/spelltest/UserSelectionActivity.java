/**
 * Filename:  UserSelectionActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the user selection screen within the application.  The class
 * provides a list of saved users of the application and allows the user to select a user name from
 * the list.  Once a user name is selected, the class transfers application control to the
 * ListSelectionActivity class.  The class also provides a way to add new users to the application.
 *
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import java.util.ArrayList;

public class UserSelectionActivity extends AppCompatActivity  {

    //Instance variables
    private UserDataAdapter mAdapter;       //The data adapter for this activity

    /**
     * Private ViewHolder class for the activity.  This class represents each item in the
     * activity's RecyclerView.  Data is passed to each of these ViewHolder items via the
     * private ListDataAdapter class instance.
     */
    private class UserViewHolder extends RecyclerView.ViewHolder{

        //Instance variables (ie one of these exists for each of the items shown in the activity.)
        private TextView mTextView;          //The main TextView for the item
        private Objects.User mUser;         //The data object for which data is displayed for this item
        private ImageView mDeleteImage;     //The delete image button for each list item

        /**
         * Constructor
         * @param v the inflated - but as yet unpopulated - view for this item.  This contains a TextView,
         *          but the object isn't wired to anything until after this constructor is called.
         */
        public UserViewHolder(@NonNull View v) {

            //Call the super constructor to inflate the view
            super(v);

            //Save the text view for later use, and add an onClickListener to itL
            mTextView = v.findViewById(R.id.user_list_item_text);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Crate an Intent to call the ListSelectionActivity.
                    Intent intent = new Intent(UserSelectionActivity.this, ListSelectionActivity.class);

                    //Add an EXTRA to the activity (ie the id of the user that was selected.)
                    //The ListSelectionActivity object will use this value to call up the spelling lists associated with this user.
                    intent.putExtra(ListSelectionActivity.EXTRA_USER_ID, mUser.id);

                    //Start the activity!
                    startActivity(intent);
                }
            });

            //Save the delete button for later use, and add a click listener to it.
            mDeleteImage = v.findViewById(R.id.user_list_item_delete);
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Get a link to the app datastore, and delete this list item's user
                    DataStore data = DataStore.newInstance(UserSelectionActivity.this);
                    data.deleteUser(mUser.id);

                    //Notify the adapter that its data set has changed - time to refresh.
                    mAdapter.refreshData();
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

        /**
         * Public method to "bind" a specific User object to this view.  This method is called in
         * the ViewHolder's onBindViewHolder activity, which is called shortly after the ViewHolder
         * is created.  We will use this method to retain the object data for later use, as well as to
         * update the text in the view with the name of the spelling list.
         *
         * @param user  the User object for which data will be shown in this list item.
         */
        public void bindUser(Objects.User user){

            //Save this user for later use.
            mUser = user;

            //Add the user's name to this view
            mTextView.setText(mUser.firstName + " " + mUser.lastName);
        }
    }

    /**
     * Private inner class for the DataAdapter for the parent RecyclerView.  This class is the "tool" used
     * by Android to attach data to each item in a RecyclerView.  Only one of these exists for each instance
     * of the parent RecyclerView activity.
     */
    private class UserDataAdapter extends RecyclerView.Adapter {

        //Instance variables.
        private ArrayList<Objects.User> users;  //The list of all users to be displayed.
        private DataStore mData;                //A reference to the main application datastore.


        /**
         * Class constructor.
         * @param context is the context object to which this Adapter is bound.
         */
        public UserDataAdapter(Context context) {

            //Save the DataStore object reference for later use.
            mData = DataStore.newInstance(context);

            //Populate the ArrayLIst object for this class (ie update the list of all possible users in the application.)
            refreshData();
        }


        /**
         * Private convenience method to update the list of users for the activity.  This was easier
         * to read than the code (at least for me :-))
         */
        public void refreshData(){

            //Update the list of users for the activity, by querying the list from the DataStore object.
            users = mData.getAllUsers();
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
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(UserSelectionActivity.this);
            View view = layoutInflater.inflate(R.layout.item_user_list, viewGroup, false);
            return new UserViewHolder(view);
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

            //Pass the call to the ListViewHolder's bindUser method, which will populate the view
            //and will also save the object for later.
            ((UserViewHolder)viewHolder).bindUser(users.get(i));
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
            return users.size();
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
        setContentView(R.layout.activity_user_selection);

        //Get a link to the RecyclerView for the activity.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);

        //Set a linear layout manager for this activity
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Instantiate the adapter for the activity, and link it with the RecyclerView parent.
        mAdapter = new UserDataAdapter(this);
        recyclerView.setAdapter(mAdapter);

        //Get a link to the floating action button, and set an onClickListener for it (for adding new users)
        FloatingActionButton fab = findViewById(R.id.user_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instantiate a new UserNameFragment to show the list name dialog box.
                UserNameFragment newFragment = new UserNameFragment();

                //Set a new dialog listener for this dialog box - code will be called when the dialog buttons are clicked.
                newFragment.setDialogListener(new DialogListener() {
                    @Override
                    public void onDialogPositiveClick(long id) {

                        //Update list in adapter.  Note that the dialog box will add the new user to the app database before this call occurs.
                        mAdapter.refreshData();

                        //Update the adapter that we have a new spelling list to include in the main list.
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        //Not used
                    }
                });

                //Now that the dialog box has been instantiated and wired up, show the dialog box...
                newFragment.show(getSupportFragmentManager(), null);
            }
        });
    }

}
