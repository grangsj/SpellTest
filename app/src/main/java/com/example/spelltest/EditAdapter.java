/**
 * Filename:  EditAdapter.java
 * Author:  Team SpellTest
 * Date:  07 April 2019
 *
 * Purpose:  This class represents the backing adapter for the Word List Builder activity.  This class
 * creates and populates each row of data in the Word List Builder, and also provides the "business logic"
 * for the buttons shown in each of the list items.
 *
 * Note that, given the (relative) complexity of this class, we broke this out into a separate class
 * rather than a nested class.  This should make the code a little easier to read and follow.
 */

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

    //Instance variables
    private ArrayList<Objects.Word> mData;      //The backing data for this adapter (stored locally)
    private Context mContext;                   //The Context to which this adapter is attached (typically from the parent Activity).
    private DataStore mDataStore;               //A reference to the application data store.

    /**
     * Class constructor.  This overrides the parent constructor by adding some additional useful
     * fields for this class.
     *
     * @param context the Context object to which this adapter is associated (should be a WordListBuilderActivity).
     * @param data the initial backing array of words displayed by this adapter.
     */
    public EditAdapter (Context context, ArrayList<Objects.Word> data){

        //Run the super constructor.
        super();

        //Save the incoming values for later use in the class.
        mData = data;
        mContext = context;

        //Create and save a link to the application datastore.
        mDataStore = DataStore.newInstance(context);
    }

    /**
     * Method called when a specific list item needs to be shown on the screen.  We will use this
     * method to inflate the item view only - populating it with data is done later (in onBindViewHolder).
     *
     * @param parent the parent RecyclerView to which this list item will be attached.
     * @param i  the element in the backing array represented by this specific list item.
     * @return the ListViewHolder item for the desired item in the list.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        //Inflate the layout for this list item.
        View newView = LayoutInflater.from(mContext).inflate(R.layout.item_edit_list, parent, false);

        //Instantiate an EditViewHolder with this new view.
        EditViewHolder vh = new EditViewHolder(newView);

        //Return the EditViewHolder we just instantiated.
        return vh;
    }

    /**
     * Public method to bind an inflated ViewHolder to its backing data.  This is where we update the
     * text field in each item, and we also wire up the listeners in the list item here as well.
     *
     * @param viewHolder the EditViewHolder for which data needs to be added.
     * @param i The specific item in the backing array from which the text in this ViewHolder needs to be updated.
     */
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

                //If we just lost focus...
                if (!hasFocus){

                    //Obtain the current word in the adapter array, and change the spelling to match whatever the user typed in.
                    Objects.Word currentWord = mData.get(i);
                    currentWord.spelling = vh.mInputBox.getText().toString();

                    //Update the application datastore with the revised word.
                    mDataStore.putWord(currentWord);

                    //Notify the adapter that this item has changed, so it can show it properly.
                    notifyItemChanged(i);
                }
            }
        });

        //Set a click listener for the delete button
        vh.mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtain the Word object for the current word.
                Objects.Word currentWord = mData.get(i);

                //Delete the word from the application datastore...
                mDataStore.deleteWord(currentWord.id);

                //...then delete it from the adapter backing array.
                mData.remove(i);

                //Notify the adapter that the item has been removed, and that the range of data displayed has changed.
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mData.size());
            }
        });

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
        return mData.size();
    }

    /**
     * Public method to add a new word to the adapter and to the datastore.  We call this from the parent Activity
     * whenever the user presses the Add New Word button (which is associated with the activity).
     * @param word the new Word object to be added to the backing array.
     */
    public void addWord(Objects.Word word){

        //Save the word to the application datastore, and reset the (empty) word id to be the assigned id.
        long id = mDataStore.putWord(word);
        word.id = id;

        //Add the word to the adapter backing array as well.
        mData.add(word);

        //Notify the adapter that a new item has been added.
        notifyItemInserted(mData.size()-1);
    }

    /**
     * Private class that represents the view structure of each element.  This
     * is a pretty dumb class - the only thing it does is save references to the
     * child views in the list item, so that they can be referenced in the adapter.
     */
    private class EditViewHolder extends RecyclerView.ViewHolder {

        //Instance variables (one of these exists per list item)
        public EditText mInputBox;          //Link to the input edit text box.
        public ImageView mDeleteImage;      //Link to the delete button.

        /**
         * Constructor for the view holder.  Teis is where we'll collect and
         * save references to the child list items (so the adapter can use them later).
         * @param itemView  the inflated view for this ViewHolder.
         */
        public EditViewHolder(@NonNull View itemView) {
            super(itemView);

            //Save references to the child view items, so the adapter can use them later.
            mInputBox = itemView.findViewById(R.id.edit_list_item_text);
            mDeleteImage = itemView.findViewById(R.id.edit_list_item_delete);
        }

    }
}
