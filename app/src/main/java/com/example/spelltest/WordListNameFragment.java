/**
 * Filename:  WordListNameFragment.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents a dialog box through which the name of a specific spelling word
 * list can be entered by the user.  It is called from the ListSelectionActivity class.
 * Once a spelling list name is added, program control is handed off to the WordListBuilderActivity
 * class through which new words can be added to the list. *
 */

package com.example.spelltest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;

public class WordListNameFragment extends AppCompatDialogFragment
            implements DialogInterface.OnClickListener {

    //Static class variables
    private static long mUserId;                                //The id of the user creating the word list.
    private static final String TAG = "WordListNameFragment";   //Tag used to help debug software

    //Instance variables
    private TextView mListName;             //TextView in which user enters spelling list name
    private DialogListener mListener;       //Callback for listeners (when user clicks buttons) - typically a ListSelectionActivity

    /**
     * Public method to instantiate a new instance of this dialog box.  We use this instead of using
     * a constructor or the normal "onCreate" override to ensure we get a userId as part of
     * object creation.  (IN other words, this acts like a singleton class - only difference is that I can't
     * make the constructor private because Android doesn't allow it.
     *
     * @param userId the ID of the user for whom the new list will be created.
     * @return an instance of WordNameListFragment
     */
    public static WordListNameFragment newInstance (long userId) {

        //Create the new fragment
        WordListNameFragment fragment = new WordListNameFragment();

        //Save the userId.
        mUserId = userId;

        //Return the fragment.
        return fragment;
    }

    /**
     * Method called by Android to create the dialog box.  We will use this method to inflate the
     * layout and set it up for use.  Note that the onClickListeners for the OK / cancel
     * buttons are wired up in a different method.
     *
     * @param savedInstanceState a Bundle with saved state information (not used here)
     * @return an instance of the Dialog class with the completed, wired up dialog box.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Log.i(TAG, "in onCreateDialog, userId = " + mUserId);
        //Instantiate the Builder object to build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflate the dialog box from its layout, and attach it to the dialog using the Builder.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_word_list, null);
        builder.setView(view);

        //Save a link to the text box with the list name.
        mListName = view.findViewById(R.id.word_list_name);

        //Add positive ("OK") and negative ("Cancel") buttons to the dialog box
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);

        //Add a title to the dialog box
        builder.setTitle(R.string.dialog_word_list_name);

        //Now that all elements of the dialog have been added, use the Builder to create the dialog and return it.
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * Public method to allow calling classes to add themselves as a listener for clicks from this dialog.
     * We only expect one listener for the dialog, so we'll only create space for a single listener callback.
     * @param listener  a class that implements the DialogListener interface, that wants to receive callbacks
     *                  when the buttons on the dialog are pressed.
     */
    public void setDialogListener (DialogListener listener){

        //Just save the listener for use later (in the onClickListener method).
        mListener = listener;
    }

    /**
     * Method called by Android whenever the dialog box buttons are clicked.  We use this method to
     * create a new spelling list with the appropriate name, if the "OK" button on the dialog box
     * is pressed.  Note that Android handles closing the dialog box if the OK or Cancel buttons are
     * pressed, so we don't have to add additional code for that here.
     *
     * @param dialog a link to the dialog box from which the button was pressed.
     * @param which the button that is pressed.
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {

        //Add code if OK button is pressed (Android handles the "cancel" button)
        if (which == DialogInterface.BUTTON_POSITIVE) {

            //Get the list name from the text view, and trim any white space.
            String listName = mListName.getText().toString().trim();

            //Only proceed if the new list name isn't empty.
            if (listName.length()>0) {

                //Create a new spelling list object.
                Objects.SpellingList spellingList = new Objects.SpellingList(DataStore.NULL_ROW_ID,
                        listName,
                        mUserId);

                //Get a reference to the application datastore
                DataStore data = DataStore.newInstance(getActivity().getApplicationContext());

                //Add the spelling list to the database.
                long listId = data.putSpellingList(spellingList);

                //Call the listener for the button, to figure out what to do with the new list.
                if (mListener != null) mListener.onDialogPositiveClick(listId);
            }

        }

    }
}


