package com.example.SpellTest;

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

public class WordListNameFragment extends AppCompatDialogFragment {

    private TextView mListName;
    private DialogListener mListener;
    private int mUserId;

    private static final String EXTRA_USER_ID = "com.example.spelltest.userId";

    public static WordListNameFragment newInstance (int userId) {

        WordListNameFragment fragment = new WordListNameFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getArguments().getInt(EXTRA_USER_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_word_list, null);

        builder.setView(view);
        mListName = view.findViewById(R.id.word_list_name);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Objects.SpellingList spellingList = new Objects.SpellingList(DataStore.DEFAULT_ID,
                        mListName.getText().toString(),
                        mUserId);
                DataStore data = DataStore.getDataStore(getActivity().getApplicationContext());
                data.addSpellingList(spellingList);

                if (mListener != null) mListener.onDialogPositiveClick();
            }
        });


        builder.setTitle(R.string.dialog_new_user);



        AlertDialog dialog = builder.create();

        return dialog;
    }

    public void setDialogListener (DialogListener listener){
        mListener = listener;
    }
}


