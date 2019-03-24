package com.example.SpellTest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;

public class NewUserFragment extends DialogFragment {
    public interface UserDialogListener {
        public void onDialogPositiveClick();
    }
    private static final int DEFAULT_USER_ID = -1;

    private TextView mFirstName;
    private TextView mLastName;
    private UserDialogListener mListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_user, null);

        builder.setView(view);
        mFirstName = view.findViewById(R.id.first_name);
        mLastName = view.findViewById(R.id.last_name);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Objects.User user = new Objects.User(DEFAULT_USER_ID, mFirstName.getText().toString(), mLastName.getText().toString());
                DataStore data = DataStore.getDataStore(getActivity().getApplicationContext());
                data.addUser(user);

                if (mListener != null) mListener.onDialogPositiveClick();
            }
        });


        builder.setTitle(R.string.dialog_new_user);



        AlertDialog dialog = builder.create();

        return dialog;
    }

    public void setDialogListener (UserDialogListener listener){
        mListener = listener;
    }
}
