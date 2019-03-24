package com.example.SpellTest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.database.*;
import com.example.database.DatabaseSchema.UserTable;

import java.util.ArrayList;

public class DataStore {

    private static DataStore sDataStore;
    private Context context;
    private SQLiteDatabase mDataBase;

    private DataStore(Context context){
        mDataBase = new DatabaseHelper(context).getWritableDatabase();
    }

    public static DataStore getDataStore(Context context){
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

        public Objects.User getUser(){
            int id = getInt(getColumnIndex(UserTable.Cols.ID));
            String firstName = getString(getColumnIndex(UserTable.Cols.FIRST_NAME));
            String lastName = getString(getColumnIndex(UserTable.Cols.LAST_NAME));

            return new Objects.User(id, firstName, lastName);
        }
    }

    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs){
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


    public ArrayList<Objects.User> getAllUsers(){
        ArrayList<Objects.User> output = new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                output.add(cursor.getUser());
                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return output;
    }


    public void addUser(Objects.User user){
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.FIRST_NAME, user.firstName);
        values.put(UserTable.Cols.LAST_NAME, user.lastName);

        mDataBase.insert(UserTable.NAME, null, values);
    }



}
