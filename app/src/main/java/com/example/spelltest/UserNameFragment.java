/**
 * Filename:  UserNameFragment.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents a dialog box through which the end user can add the name of a
 * new user to the application.  It is called from the UserSelectionActivity class, when the
 * class detects a request to add a new user.
 *
 */



package com.example.spelltest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;

public class UserNameFragment extends AppCompatDialogFragment
            implements DialogInterface.OnClickListener {

    //Instance variables
    private TextView mFirstName;            //Input field for user's first name
    private TextView mLastName;             //Input field for user's last name
    private DialogListener mListener;       //Listener for callbacks on this dialog box (typically a UserSelectionActivity


    /**
     * Method called by Android to create the dialog.  We will use this method to inflate the
     * layout for the dialog and add its title, buttons, etc.  We will also save the view elements
     * so we can extract them later in the click listeners.
     *
     * @param savedInstanceState a Bundle with saved state information (not used in this app.)
     * @return  a fully wired-up Dialog that can be displayed on the screen.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Instantiate the Builder object, that will be used to build the Dialog from its elements.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflate the vew from its layout file, and add the inflated View to the builder object.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(view);

        //Save the input text fields for use in the click listeners (another overridden method)
        mFirstName = view.findViewById(R.id.first_name);
        mLastName = view.findViewById(R.id.last_name);

        //Add buttons to the dialog box.
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);

        //Add a title to the dialog box.
        builder.setTitle(R.string.dialog_new_user);

        //Now that all the components of the dialog have been identified, use the Builder to create the dialog and return it.
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

            //Get the first and last names entered by the user, and trim any white space
            String firstName  = mFirstName.getText().toString().trim();
            String lastName = mLastName.getText().toString().trim();

            //Only proceed if both fields have been populated.
            if ((firstName.length()>0) && (lastName.length()>0)) {

                //Create a new user object from the input first and last name
                Objects.User user = new Objects.User(DataStore.NULL_ROW_ID, firstName, lastName);

                //Get access to the application datastore, and save the new user information in the app datastore
                DataStore data = DataStore.newInstance(getActivity().getApplicationContext());
                long userId = data.putUser(user);

                //If we have a listener, call the listener for this button to figure out what to do with the new user.
                if (mListener != null) mListener.onDialogPositiveClick(userId);
            }

        }

    }
}
