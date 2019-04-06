/**
 *
 * Filename:  DatabaseHelper.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the main application database.  This class “wraps” the main
 * application database, and it provides methods to allow calling classes to issue SQL commands
 * to the database.   This class will automatically create a new database and all required
 * database tables if one is not located on application startup.  To prevent multiple open
 * connections to the main application database, all calls to this class’ methods are performed
 * through the singleton DataStore class.
 */

package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.database.DatabaseSchema.*;
import com.example.spelltest.DataStore;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Private static variables
    private static final int VERSION = 1;                           //Database version
    private static final String DATABASE_NAME = "spellTest.db";     //Database name on device
    private static final String COMMA = ", ";                       //Comma (used in SQL inserts / queries

    //Public static variables for difficulty (not implemented in this version of app)
    public static final String DIFFICULTY_EASY = "Easy";
    public static final String DIFFICULTY_MEDIUM = "Medium";
    public static final String DIFFICULTY_HARD = "Hard";

    //Private member variables for difficulty (not used for this app).
    //Note that this file will assign "real" ID's to these values at application startup
    private long id_difficulty_easy = DataStore.NULL_ROW_ID;
    private long id_difficulty_medium = DataStore.NULL_ROW_ID;
    private long id_difficulty_hard = DataStore.NULL_ROW_ID;


    /**
     * Class constructor.
     * @param context The context to which this database is attached.
     */
    public DatabaseHelper (Context context){

        //Call super version of constructor.  This method will call onCreate() if the database cannot be located.
        //So, after this call, we can assume that the database exists.
        super (context, DATABASE_NAME, null, VERSION);

        /**
         * When the database is created, we assign values to the difficulty id's.  However, if the
         * application database is already created at startup, we enter this code to pull the correct
         * ID values from the database./
         */
        if (id_difficulty_easy == DataStore.NULL_ROW_ID){
            //Get access to the database to seach for ID's
            SQLiteDatabase db = getReadableDatabase();

            //Search for the difficulty name (ie "Easy") and put the single result into a Cursor.
            Cursor cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_EASY + "'",
                    null,
                    null,
                    null,
                    null
            );

            //Extract the (single) value we want from the cursor.
            cursor.moveToFirst();
            id_difficulty_easy = cursor.getInt(0);

            //Search for the difficulty name (ie "Medium") and put the single result into a Cursor.
            cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_MEDIUM + "'",
                    null,
                    null,
                    null,
                    null
            );

            //Extract the (single) value we want from the cursor.
            cursor.moveToFirst();
            id_difficulty_medium = cursor.getInt(0);

            //Search for the difficulty name (ie "Medium") and put the single result into a Cursor.
            cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_HARD + "'",
                    null,
                    null,
                    null,
                    null
            );

            //Extract the (single) value we want from the cursor
            cursor.moveToFirst();
            id_difficulty_hard = cursor.getInt(0);
        }
    }

    /**
     * Method that is called by the class super constructor if the application database cannot be located.
     * This is typical for the initial install of the application.  If that occurs, this method'
     * will create the database using standard SQL code.
     *
     * @param sqLiteDatabase  the empty database that needs to be populated with application-specific code.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create the user name table
        sqLiteDatabase.execSQL("create table " + UserTable.NAME + "(" +
                UserTable.Cols.ID + " integer primary key autoincrement, " +
                UserTable.Cols.FIRST_NAME + " TEXT, " +
                UserTable.Cols.LAST_NAME + " TEXT" +
                ")");

        //Create the difficulty table
        sqLiteDatabase.execSQL("CREATE TABLE " + DifficultyTable.NAME + "(" +
                DifficultyTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DifficultyTable.Cols.DESCRIPTION + " TEXT" +
                ")");

        //Create the table of words.  Note that the word difficulty and associated word list is stored as a foreign key.
        sqLiteDatabase.execSQL("CREATE TABLE " + WordTable.NAME + "(" +
                WordTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WordTable.Cols.LIST_ID + " INTEGER, " +
                WordTable.Cols.SPELLING + " TEXT, " +
                WordTable.Cols.DEFINITION + " TEXT, " +
                WordTable.Cols.EXAMPLE_SENTENCE + " TEXT, " +
                WordTable.Cols.DIFFICULTY_ID + " INTEGER, " +
                WordTable.Cols.TYPE + " TEXT, " +
                "FOREIGN KEY("+WordTable.Cols.DIFFICULTY_ID + ") " +
                "REFERENCES " + DifficultyTable.NAME + "(" +  DifficultyTable.Cols.ID +"), " +
                "FOREIGN KEY("+WordTable.Cols.LIST_ID + ") " +
                "REFERENCES " + SpellingListTable.NAME + "(" +  SpellingListTable.Cols.ID +")" +
                ")");


        //CReate the table of spelling lists.  Note that the difficulty and oowner of the list is retained as a foreign key.
        sqLiteDatabase.execSQL("CREATE TABLE " + SpellingListTable.NAME + "(" +
                SpellingListTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpellingListTable.Cols.NAME + " TEXT, " +
                SpellingListTable.Cols.USER_ID + " INTEGER, " +
                SpellingListTable.Cols.TYPE+ " TEXT, " +
                SpellingListTable.Cols.DIFFICULTY_ID + " INTEGER, " +
                "FOREIGN KEY(" + SpellingListTable.Cols.USER_ID + ") " +
                "REFERENCES " + UserTable.NAME + "(" +  UserTable.Cols.ID +"), " +
                "FOREIGN KEY(" +SpellingListTable.Cols.DIFFICULTY_ID + ") " +
                "REFERENCES " + DifficultyTable.NAME + "(" +  DifficultyTable.Cols.ID +")" +
                ")");

        //Create the spelling list stat table.
        sqLiteDatabase.execSQL("CREATE TABLE " + SpellingListStatTable.NAME + "(" +
                SpellingListStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpellingListStatTable.Cols.LIST_ID + " INTEGER, " +
                SpellingListStatTable.Cols.DATE + " INTEGER, " +
                SpellingListStatTable.Cols.ELAPSED_TIME + " INTEGER, " +
                SpellingListStatTable.Cols.NUMBER_CORRECT + " INTEGER, " +
                SpellingListStatTable.Cols.NUMBER_INCORRECT + " INTEGER, " +
                "FOREIGN KEY(" +SpellingListStatTable.Cols.LIST_ID + ") " +
                "REFERENCES " + SpellingListTable.NAME + "(" +  SpellingListTable.Cols.ID +")" +
                ")");

        //Now, add basic data to the table:
        /*******************************************
         * Difficulty table
         *********************************************/
        ContentValues difficultyValues = new ContentValues();
        difficultyValues.put(DifficultyTable.Cols.DESCRIPTION, DIFFICULTY_EASY);
        id_difficulty_easy = sqLiteDatabase.insert(DifficultyTable.NAME, null, difficultyValues);

        difficultyValues.put(DifficultyTable.Cols.DESCRIPTION, DIFFICULTY_MEDIUM);
        id_difficulty_medium = sqLiteDatabase.insert(DifficultyTable.NAME, null, difficultyValues);

        difficultyValues.put(DifficultyTable.Cols.DESCRIPTION, DIFFICULTY_HARD);
        id_difficulty_hard = sqLiteDatabase.insert(DifficultyTable.NAME, null, difficultyValues);

        /****************************************
         * USER TABLE
         ****************************************/
        ContentValues userValues = new ContentValues();
        userValues.put(UserTable.Cols.FIRST_NAME, "Default");
        userValues.put(UserTable.Cols.LAST_NAME, "User");
        long userId = sqLiteDatabase.insert(UserTable.NAME, null, userValues);

        /***********************************************
         * SPELLING LIST TABLE
         ******************************************/
        ContentValues spellingListValues = new ContentValues();
        spellingListValues.put(SpellingListTable.Cols.NAME, "Default Spelling List");
        spellingListValues.put(SpellingListTable.Cols.USER_ID, userId);
        long listId = sqLiteDatabase.insert(SpellingListTable.NAME, null, spellingListValues);

        /*******************************************
         * SPELLING WORDS
         ******************************************/
        ContentValues wordValues = new ContentValues();
        wordValues.put(WordTable.Cols.LIST_ID, listId);
        wordValues.put(WordTable.Cols.SPELLING, "apple");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);


        wordValues.put(WordTable.Cols.SPELLING, "banana");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);


        wordValues.put(WordTable.Cols.SPELLING, "cat");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);

        wordValues.put(WordTable.Cols.SPELLING, "dog");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);
    }

    /**
     * Called by the class constructor if the database version passed to the constructor does not
     * match the database version in memory.  This allows us to upgrade the database if needed.
     * Since this is the very first iteration of the application, we will leave this blank.
     *
     *
     * @param sqLiteDatabase
     * @param i  old version of database
     * @param i1 new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Method that is called by the Android OS when a new database is configured.
     * We will use this method to activate foreign key constraints on the database, which are off
     * by default.  This will ensure that all foreign key references in a table match up with an
     * associated primary key in another table.
     *
     * @param db is a reference to the SQ database to be configured.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);

    }
}
