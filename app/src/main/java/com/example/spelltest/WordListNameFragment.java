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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;

public class WordListNameFragment extends AppCompatDialogFragment
            implements DialogInterface.OnClickListener {

    private TextView mListName;
    private DialogListener mListener;
    private long mUserId;

    private static final String EXTRA_USER_ID = "com.example.spelltest.userId";

    public static WordListNameFragment newInstance (long userId) {

        WordListNameFragment fragment = new WordListNameFragment();

        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getArguments().getLong(EXTRA_USER_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_word_list, null);

        builder.setView(view);
        mListName = view.findViewById(R.id.word_list_name);

        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);



        builder.setTitle(R.string.dialog_word_list_name);



        AlertDialog dialog = builder.create();

        return dialog;
    }

    public void setDialogListener (DialogListener listener){
        mListener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {
            Objects.SpellingList spellingList = new Objects.SpellingList(DataStore.NULL_ROW_ID,
                    mListName.getText().toString(),
                    mUserId);
            DataStore data = DataStore.newInstance(getActivity().getApplicationContext());
            long listId = data.putSpellingList(spellingList);

            if (mListener != null) mListener.onDialogPositiveClick(listId);
        }

    }
}


