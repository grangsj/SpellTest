package com.example.SpellTest;

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

    private static DataStore sDataStore;
    private Context context;
    private SQLiteDatabase mDataBase;

    public static final long DEFAULT_ID = -1;
    private static final String TAG = "DataStore";

    private DataStore(Context context) {
        mDataBase = new DatabaseHelper(context).getWritableDatabase();
    }

    public static DataStore newInstance(Context context) {
        if (sDataStore == null) {
            sDataStore = new DataStore(context);
        }
        return sDataStore;
    }


    private class UserCursorWrapper extends CursorWrapper {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public UserCursorWrapper(Cursor cursor) {
            super(cursor);
        }

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


    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(
                UserTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );


        return new UserCursorWrapper(cursor);
    }

    private SpellingListCursorWrapper querySpellingLists(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(
                SpellingListTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new SpellingListCursorWrapper(cursor);
    }


    public ArrayList<Objects.User> getAllUsers() {
        ArrayList<Objects.User> output = new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                output.add(cursor.getUser());
                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return output;
    }

    public ArrayList<Objects.SpellingList> getSpellingLists(long userId) {
        ArrayList<Objects.SpellingList> output = new ArrayList<>();

        String whereClause = SpellingListTable.Cols.USER_ID + "=" + userId;

        SpellingListCursorWrapper cursor = querySpellingLists(whereClause, null);

        if (cursor.moveToFirst()) {
        try {

                while (!cursor.isAfterLast()) {
                    output.add(cursor.getSpellingList());
                    cursor.moveToNext();
                }
            } finally{
                cursor.close();
            }
        }


        return output;
    }


    public long addUser(Objects.User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.FIRST_NAME, user.firstName);
        values.put(UserTable.Cols.LAST_NAME, user.lastName);

        return mDataBase.insert(UserTable.NAME, null, values);
    }

    public long addSpellingList(Objects.SpellingList list) {
        ContentValues values = new ContentValues();
        values.put(SpellingListTable.Cols.NAME, list.name);
        values.put(SpellingListTable.Cols.USER_ID, list.userId);

        return mDataBase.insert(SpellingListTable.NAME, null, values);
    }

    public ArrayList<String> getWords(long spellingListId){

        Log.i(TAG, "In GetWords method, pulling cursor...");
        ArrayList<String> output = new ArrayList<>();
        Cursor wordCursor = mDataBase.query(

                DatabaseSchema.WordTable.NAME,
                null,
                DatabaseSchema.WordTable.Cols.LIST_ID + "=" + spellingListId,
                null,
                null,
                null,
                null
        );

        try{
            wordCursor.moveToFirst();
            while (!wordCursor.isAfterLast()){
                int columnIndex = wordCursor.getColumnIndex(DatabaseSchema.WordTable.Cols.SPELLING);
                output.add(wordCursor.getString(columnIndex));
                wordCursor.moveToNext();
            }
        }  finally {
            wordCursor.close();
        }

        return output;
    }

    public long putSpellingListStat(Objects.SpellingListStat stat){
        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.SpellingListStatTable.Cols.LIST_ID, stat.listId);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.DATE, stat.date);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.ELAPSED_TIME, stat.elapsedTime);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_CORRECT, stat.numberCorrect);
        values.put(DatabaseSchema.SpellingListStatTable.Cols.NUMBER_INCORRECT, stat.numberIncorrect);

        return mDataBase.insert(DatabaseSchema.SpellingListStatTable.NAME, null, values);
    }

    private SpellingListStatCursorWrapper querySpellingListStats(String whereClause, String[] selectionArgs){
        Cursor cursor = mDataBase.query(

                DatabaseSchema.SpellingListStatTable.NAME,
                null,
                whereClause,
                selectionArgs,
                null,
                null,
                null
        );

        return new SpellingListStatCursorWrapper(cursor);
    }


    public Objects.SpellingListStat getSpellingListStat(long id){

        Objects.SpellingListStat stat = null;

        String whereClause = DatabaseSchema.SpellingListStatTable.Cols.ID + "=" + id;
        SpellingListStatCursorWrapper cursor = querySpellingListStats(whereClause, null);

        try{
            if (cursor.moveToFirst()){
                stat = cursor.getSpellingListStat();
            };


        } finally {
            cursor.close();
        }


        return stat;
    }

    public Objects.SpellingList getSpellingList(long listId){

        Objects.SpellingList list = null;

        String whereClause = SpellingListTable.Cols.ID + "=" + listId;

        SpellingListCursorWrapper cursor = querySpellingLists(whereClause, null);

        if (cursor.moveToFirst()){
            list = cursor.getSpellingList();
        }

        return list;
    }


}
