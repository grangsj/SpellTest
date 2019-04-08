package com.example.spelltest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter {

    private ArrayList<Objects.Word> mData;
    private Context mContext;
    private DataStore mDataStore;

    public EditAdapter (Context context, ArrayList<Objects.Word> data){

        super();
        mData = data;
        mContext = context;
        mDataStore = DataStore.newInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View newView = LayoutInflater.from(mContext).inflate(R.layout.item_edit_list, parent, false);
        EditViewHolder vh = new EditViewHolder(newView);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        //Cast the ViewHolder to be an EditViewHolder
        final EditViewHolder vh = (EditViewHolder)viewHolder;

        //Set the text in the EditView
        vh.mInputBox.setText(mData.get(i).toString());

        //Set an onFocusChange listener for the input box
        vh.mInputBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){

                    Objects.Word currentWord = mData.get(i);
                    currentWord.spelling = vh.mInputBox.getText().toString();

                    mDataStore.putWord(currentWord);

                    notifyItemChanged(i);
                }
            }
        });

        //Set a click listener for the delete button
        vh.mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.Word currentWord = mData.get(i);
                mDataStore.deleteWord(currentWord.id);
                mData.remove(i);


                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mData.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addWord(Objects.Word word){
        mData.add(word);
        notifyItemInserted(mData.size()-1);
    }


    private class EditViewHolder extends RecyclerView.ViewHolder {

        public EditText mInputBox;
        public ImageView mDeleteImage;

        public EditViewHolder(@NonNull View itemView) {
            super(itemView);

            mInputBox = itemView.findViewById(R.id.edit_list_item_text);
            mDeleteImage = itemView.findViewById(R.id.edit_list_item_delete);
        }

    }
}
