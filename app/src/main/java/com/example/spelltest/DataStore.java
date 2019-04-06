/**
 * Filename:  DataStore.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 * <p>
 * Purpose:  This singleton class serves as the main access point for all persistent data required
 * by other user-facing application classes.  All accesses to the main application database for the
 * application occur through this class – no other classes are configured to access the database
 * directly.  The class provides methods to access the various application-specific objects (for
 * users, spelling lists, etc.) from the application’s database.
 */

package com.example.spelltest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.database.*;
import com.example.database.DatabaseSchema.SpellingListTable;
import com.example.database.DatabaseSchema.UserTable;

import java.util.ArrayList;

public class DataStore {

    //Class variables
    private static DataStore sDataStore;                //A private reference to the app database.
    public static final long NULL_ROW_ID = -1;           //Null row ID value - referenced by other classes in the app.
    private static final String TAG = "DataStore";      //Tag for app log entries (ie for debugging).


    //Instance variables
    private SQLiteDatabase mDataBase;                   //The application database.

    /**
     * The following classes "wrap" the output cursors for queries on sqecific objects in the application.
     * The use of these wrapper classes (rather than just the direct use of Cursors) allow all of the
     * Cursor-To-Object creation code in this wrapper class, rather than having to be repeated multiple
     * times elsewhere in the code.
     */
    private class UserCursorWrapper extends CursorWrapper {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public UserCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        /**
         * Method to return a User object from the current cursor location.
         * @return the User object representing the data at the current Cursor location.
         */
        public Objects.User getUser() {
            int id = getInt(getColumnIndex(UserTable.Cols.ID));
            String firstName = getString(getColumnIndex(UserTable.Cols.FIRST_NAME));
            String lastName = getString(getColumnIndex(UserTable.Cols.LAST_NAME));

            return new Objects.User(id, firstName, lastName);
        }
    }

    private class SpellingListCursorWrapper extends CursorWrapper {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public SpellingListCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        /**
         * Method to return a Spelling List object from the current cursor location.
         * @return the Spelling List object representing the data at the current Cursor location.
         */
        public Objects.SpellingList getSpellingList() {
            int id = getInt(getColumnIndexOrThrow(SpellingListTable.Cols.ID));
            String name = getString(getColumnIndex(SpellingListTable.Cols.NAME));
            int userId = getInt(getColumnIndex(SpellingListTable.Cols.USER_ID));

            return new Objects.SpellingList(id, name, userId);
        }
    }

    private class SpellingListStatCursorWrapper extends CursorWrapper {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public SpellingListStatCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        /**
         * Method to return a Spelling List Stat object from the current cursor location.
         * @return the Spelling List Stat object representing the data at the current Cursor location.
         */
        public Objects.SpellingListStat getSpellingListStat() {
            long id = getLong(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.ID));
            long listId = getLong(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.LIST_ID));
            long date = getLong(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.DATE));
            long elapsedTime = getLong(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.ELAPSED_TIME));
            int numberCorrect = getInt(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_CORRECT));
            int numberIncorrect = getInt(getColumnIndexOrThrow(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_INCORRECT));

            return new Objects.SpellingListStat(id, listId, date, elapsedTime, numberCorrect, numberIncorrect);
        }
    }


    /**
     * Class constructor.  We have made this private to preclude other classes from calling the
     * constructor directly.  Instead, calling classes will need to use the newInstance() method
     * to obtain a reference to the (single) instance of this class.
     * @param context is the Context to which the database instance will be attached.
     */
    private DataStore(Context context) {

        //Just populate the database member variable.
        mDataBase = new DatabaseHelper(context).getWritableDatabase();
    }


    /**
     * Method used to access to the main application database.  Access is limited to this method
     * (rather than the class constructor) to ensure that only one instance of this class is created.  
     * This will eliminate the risk of database corruption issues caused by multiple database
     * access points.
     *
     *
     * @param context  the Context to which this database instance will be attached.
     * @return a reference to the single instance of this class.
     */
    public static DataStore newInstance(Context context) {

        //Check if the class variable holding the instance reference is null.  If so, an instance
        //of this class hasn't been created yet!  So, create it.
        if (sDataStore == null) {
            sDataStore = new DataStore(context);
        }

        //Now that we know an instance of the class has been created, return a reference to it.
        return sDataStore;
    }

    /**
     * Private convenience class to allow a query into the user table of the database.  Other methods
     * in this class will use this method to search users - this is done to prevent having to re-write
     * this query code multiple times.
     *
     * @param whereClause  the SQL Where clause for the query.
     * @param whereArgs  any ordering requirements for the query.  We usually set this to NULL.
     * @return a UserCursorWrapper containing the results of the database query.
     */
    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {

        //Perform the query on the User Table of the database, returning the values in a Cursor.
        Cursor cursor = mDataBase.query(
                UserTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        //Create a new UserCursorWrapper object from this cursor, and return it.
        return new UserCursorWrapper(cursor);
    }

    /**
     * Private convenience class to allow a query into the spelling list table of the database.  Other methods
     * in this class will use this method to search spelling lists - this is done to prevent having to re-write
     * this query code multiple times.
     *
     * @param whereClause  the SQL Where clause for the query.
     * @param whereArgs  any ordering requirements for the query.  We usually set this to NULL.
     * @return a SpellingListCursorWrapper containing the results of the database query.
     */
    private SpellingListCursorWrapper querySpellingLists(String whereClause, String[] whereArgs) {

        //Perform the query on the Spelling List Table of the database, returning the values in a Cursor.
        Cursor cursor = mDataBase.query(
                SpellingListTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        //Create a new SpellingListCursorWrapper object from this cursor, and return it.
        return new SpellingListCursorWrapper(cursor);
    }

    /**
     * Private convenience class to allow a query into the spelling list stat table of the database.  Other methods
     * in this class will use this method to search spelling list stats - this is done to prevent having to re-write
     * this query code multiple times.
     *
     * @param whereClause  the SQL Where clause for the query.
     * @param whereArgs  any ordering requirements for the query.  We usually set this to NULL.
     * @return a SpellingListStatCursorWrapper containing the results of the database query.
     */
    private SpellingListStatCursorWrapper querySpellingListStats(String whereClause, String[] whereArgs) {

        //Perform the query on the Spelling List Stat Table of the database, returning the values in a Cursor.
        Cursor cursor = mDataBase.query(

                DatabaseSchema.SpellingListStatTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        //Create a new SpellingListStatCursorWrapper object from this cursor, and return it.
        return new SpellingListStatCursorWrapper(cursor);
    }


    /**
     * Public method to extract all users from the database.
     * @return an ArrayList of user objects, containing all users in the database.  This ArrayList can be empty.
     */
    public ArrayList<Objects.User> getAllUsers() {

        //Instantiate the ArrayList
        ArrayList<Objects.User> output = new ArrayList<>();

        //Perform the User Table query, and get a UserCursorWrapper with the results.
        UserCursorWrapper cursor = queryUsers(null, null);

        //Step through each of the items in the cursor, obtain a User object rom each line of the Cursor,
        //and add it to the ArrayList.  This is enclosed within a Try block to allow the cursor to be
        //closed if something happens....
        try {
            cursor.moveToFirst();                   //Move to the first item in the cursor.
            while (!cursor.isAfterLast()) {         //As long as we're not at the end of the cursor....
                output.add(cursor.getUser());       //...create a User object and add it to the ArrayList
                cursor.moveToNext();                //Move to the next item in the cursor.
            }

        } finally {
            cursor.close();                         //Close the cursor when done.
        }

        //Return the ArrayList
        return output;
    }

    /**
     * Public method to get all spelling lists assigned to a specific user.
     * @param userId is the id of the user for which spelling lists are being searched.
     * @return an ArrayList of SpellingList objects for that user.  This can potentially be empty.
     */
    public ArrayList<Objects.SpellingList> getSpellingLists(long userId) {

        //Instantiate the output ArrayList
        ArrayList<Objects.SpellingList> output = new ArrayList<>();

        //Create the SQL WHERE clause (ie looking for a specific UserId)
        String whereClause = SpellingListTable.Cols.USER_ID + "=" + userId;

        //Do the query, and dump the results in a SpellingListCursorWrapper
        SpellingListCursorWrapper cursor = querySpellingLists(whereClause, null);

        //Step through each of the items in the cursor, obtain a SpellingList object from each line of the Cursor,
        //and add it to the ArrayList.  This is enclosed within a Try block to allow the cursor to be
        //closed if something happens....
        //Move to the first item in the Cursor
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {             //If we're not at the end of the Cursor...
                output.add(cursor.getSpellingList());   //...Create a new SpellingLIst object at the cursor and add it to the ArrayLIst
                cursor.moveToNext();                    //...and move to the next item on the list.
            }
        } finally {
            cursor.close();         //Close cursor when done.
        }

        //Return the ArrayList
        return output;
    }

    /**
     * Public method to get all the words in a single spelling list.
     * @param spellingListId the id of the spelling list for which words are needed.
     * @return  an ArrayList of Word objects, containing all the words in the spelling list.  This could be empty.
     */
    public ArrayList<Objects.Word> getWords(long spellingListId) {

        Log.i(TAG, "In GetWords method, pulling cursor...");

        //Instantiate the ArrayList for the output.
        ArrayList<Objects.Word> output = new ArrayList<>();

        //Do the query, and return a Cursor with the results.  Note that we didn't create a CursorWrapper
        //for words, since there's really only this method where we look for words.
        Cursor wordCursor = mDataBase.query(
                DatabaseSchema.WordTable.NAME,
                null,
                DatabaseSchema.WordTable.Cols.LIST_ID + "=" + spellingListId,
                null,
                null,
                null,
                null
        );

        /**
         * Step through each item in the Cursor, and (if we're not at the end of the list) create a
         * new Word object using the data at the current Cursor location and add it to the ArrayList.
         * Enclosing everything in a try block to ensure we close the Cursor when we're done with it.
         */
        try {

            //Move the Cursor to the first thing on the list.
            wordCursor.moveToFirst();

            //If we're not at the end....
            while (!wordCursor.isAfterLast()) {

                //Extract all the info we need from the cursor.
                int columnIndexId = wordCursor.getColumnIndex(DatabaseSchema.WordTable.Cols.ID);
                int columnIndexSpelling = wordCursor.getColumnIndex(DatabaseSchema.WordTable.Cols.SPELLING);
                int columnIndexListId = wordCursor.getColumnIndex(DatabaseSchema.WordTable.Cols.LIST_ID);

                //Build a new Word object from the data retrieved from the Cursor.
                Objects.Word word = new Objects.Word(
                        wordCursor.getLong(columnIndexId),
                        wordCursor.getLong(columnIndexListId),
                        wordCursor.getString(columnIndexSpelling)
                );

                //Add the newly-created Word object to the outout ArrayLIst
                output.add(word);

                //Move to the next item in the Cursor.
                wordCursor.moveToNext();
            }
        } finally {

            //Close the cursor when done.
            wordCursor.close();
        }

        //Return the output Cursor.
        return output;
    }

    /**
     * Public method to retrieve a single spelling list stat.
     * @param id the id of the spelling list stat we're looking for
     * @return  a SpellingListStat object associated with this id, or null if the id is invalid.
     */
    public Objects.SpellingListStat getSpellingListStat(long id) {

        //Instantiate the output SpellingListStat, and make it null (default if the query doesn't work.)
        Objects.SpellingListStat stat = null;

        //Create the SQL WHERE clause for this search (ie looking for spelling list stat id
        String whereClause = DatabaseSchema.SpellingListStatTable.Cols.ID + "=" + id;

        //Perform the query, and dump the results into a SpellingListStatCursorWrapper.
        SpellingListStatCursorWrapper cursor = querySpellingListStats(whereClause, null);

        //Attempt to read the cursor.  We put this into a try block to ensure the cursor is closed after use.
        try {

            //If we;re not at the end of the Cursor (ie we have a result), create a SpellingListStat object with the results.
            if (cursor.moveToFirst()) {
                stat = cursor.getSpellingListStat();
            }

        } finally {

            //Close cursor when done.
            cursor.close();
        }

        //Return the spelling list stat.  Note that this could be null if no stat was identified.
        return stat;
    }

    /**
     * Public method to retrieve a single spelling list, given its id.
     * @param listId  the id of the spelling list to be retrieved.
     * @return a SpellingList object containing the list info, or null if no spelling list with that id was located.
     */
    public Objects.SpellingList getSpellingList(long listId) {

        //Instantiate the spelling list output object, and set it to null (ie default value).
        Objects.SpellingList list = null;

        //Create the SQL WHERE clause (ie looking for a specific list id.)
        String whereClause = SpellingListTable.Cols.ID + "=" + listId;

        //Perform the query, and dump the results into a SpellingListCursorWrapper
        SpellingListCursorWrapper cursor = querySpellingLists(whereClause, null);

        //Attempt to pull the data out of the cursor.  Using a try block here to ensure cursor is closed after use.
        try{

            //If we're not at the end of the cursor (ie cursor actually contains something). create a
            //SpellingList object with the results.
            if (cursor.moveToFirst()) {
                list = cursor.getSpellingList();
            }
        } finally {

            //Close the cursor when done.
            cursor.close();
        }

        //Return the SpellingList object.  Note that this can be null if no list with the desired id was found.
        return list;
    }

    /**
     * Public method to delete a word from the database.
     * @param wordId the id of the word to be deleted.
     */
    public void deleteWord(long wordId) {

        //This line of code exectes the SQL query to delete that word (if it exists).
        mDataBase.delete(
                DatabaseSchema.WordTable.NAME,
                DatabaseSchema.WordTable.Cols.ID + "=" + Long.toString(wordId),
                null);
    }

    /**
     * Public method to add a new user to the database, or update a current user's information in the database.
      * @param user the User object containing the new user's information.  If the user is new, the id field
     *             in the object should be NULL_ROW_ID
     *  @return the ID of the user, or NULL_ROW_ID if the user was not added.
     */
    public long putUser(Objects.User user) {

        //Create a ContentValues object to transfer info into the database.
        ContentValues values = new ContentValues();

        //If the id field for the user is not null, then we want to transfer the id as well (ie updating user info).
        if (user.id != DataStore.NULL_ROW_ID) {

            //Add the user ID to the CV object.
            values.put(UserTable.Cols.ID, user.id);

        }

        //Add the remaining user items to the CV object.
        values.put(UserTable.Cols.FIRST_NAME, user.firstName);
        values.put(UserTable.Cols.LAST_NAME, user.lastName);

        //Add the item to the database, and return the row id for the database.
        return mDataBase.replace(UserTable.NAME, null, values);

    }

    /**
     * Public method to add a new spelling list to the database, or update a current list's information in the database.
     * @param list the SpellingList object containing the new list information.  If the list is new, the id field
     *             in the object should be NULL_ROW_ID
     *  @return  the ID of the new spelling list, or NULL_ROW_ID if the spelling list was not added.
     */
    public long putSpellingList(Objects.SpellingList list) {

        //Create a ContentValues object for transferring values to the database.
        ContentValues values = new ContentValues();

        //If the id field for the list is not null, then we want to transfer the list id as well (ie updating list info).
        if (list.id != DataStore.NULL_ROW_ID) {

            //Add the list ID to the CV object.
            values.put(SpellingListTable.Cols.ID, list.id);
        }

        //Add the remaining user items to the CV object.
        values.put(SpellingListTable.Cols.NAME, list.name);
        values.put(SpellingListTable.Cols.USER_ID, list.userId);

        //Add the item to the database, and return the row id for the spelling list
        return mDataBase.replace(SpellingListTable.NAME, null, values);
    }

    /**
     * Public method to add a new spelling word to the database, or update a current word's information in the database.
     * @param word the Word object containing the new word information.  If the word is new, the id field
     *             in the object should be NULL_ROW_ID
     *  @return  the ID of the new word, or NULL_ROW_ID if the word was not added.
     */
    public long putWord(Objects.Word word) {

        //Create a ContentValues object for transferring values to the database.
        ContentValues values = new ContentValues();

        //If the id field for the word is not null, then we want to transfer the word id as well (ie updating word info).
        if (word.id != DataStore.NULL_ROW_ID) {
            values.put(DatabaseSchema.WordTable.Cols.ID, word.id);
        }

        //Add the remaining user items to the CV object.
        values.put(DatabaseSchema.WordTable.Cols.LIST_ID, word.list_id);
        values.put(DatabaseSchema.WordTable.Cols.SPELLING, word.spelling);

        //Add the item to the database, and return the row id for the spelling word
        return mDataBase.replace(DatabaseSchema.WordTable.NAME, null, values);
    }

    /**
     * Public method to add a new spelling list stat to the database.
     * @param stat the SpellingListStat object containing the new stat information.
     *  @return  the ID of the new spelling list stat, or NULL_ROW_ID if the stat was not added.
     */
    public long putSpellingListStat(Objects.SpellingListStat stat) {

        //Create a ContentValues object for transferring values to the database.
        ContentValues values = new ContentValues();

        //Add the remaining stat items to the CV object.  Note that there is no option to update an
        //existing stat, since we don't expect that this will ever be necessary.
        values.put(DatabaseSchema.SpellingListStatTable.Cols.LIST_ID, stat.listId);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.DATE, stat.date);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.ELAPSED_TIME, stat.elapsedTime);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_CORRECT, stat.numberCorrect);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_INCORRECT, stat.numberIncorrect);

        //Add the item to the database, and return the row id for the spelling list stat
        return mDataBase.insert(DatabaseSchema.SpellingListStatTable.NAME, null, values);
    }




}
