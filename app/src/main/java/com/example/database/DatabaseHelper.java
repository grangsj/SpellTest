package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.database.DatabaseSchema.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "spellTest.db";
    private static final String COMMA = ", ";
    private static final long DEFAULT_ROW_ID = -1;

    private static final String DIFFICULTY_EASY = "Easy";
    private static final String DIFFICULTY_MEDIUM = "Medium";
    private static final String DIFFICULTY_HARD = "Hard";

    private long id_difficulty_easy = DEFAULT_ROW_ID;
    private long id_difficulty_medium = DEFAULT_ROW_ID;
    private long id_difficulty_hard = DEFAULT_ROW_ID;


    public DatabaseHelper (Context context){

        super (context, DATABASE_NAME, null, VERSION);



        if (id_difficulty_easy == DEFAULT_ROW_ID){
            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_EASY + "'",
                    null,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            id_difficulty_easy = cursor.getInt(0);

            cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_MEDIUM + "'",
                    null,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            id_difficulty_medium = cursor.getInt(0);

            cursor = db.query(
                    DifficultyTable.NAME,
                    null,
                    DifficultyTable.Cols.DESCRIPTION + "=" + "'" + DIFFICULTY_HARD + "'",
                    null,
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            id_difficulty_hard = cursor.getInt(0);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + UserTable.NAME + "(" +
                UserTable.Cols.ID + " integer primary key autoincrement, " +
                UserTable.Cols.FIRST_NAME + " TEXT, " +
                UserTable.Cols.LAST_NAME + " TEXT" +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + DifficultyTable.NAME + "(" +
                DifficultyTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DifficultyTable.Cols.DESCRIPTION + " TEXT" +
                ")");

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


        /**  TO DO:  Figure out what we're going to track for data.
        sqLiteDatabase.execSQL("CREATE TABLE " + UserStatTable.NAME + "(" +
                UserStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserStatTable.Cols.USER_ID + " INTEGER, " +
                UserStatTable.Cols. + " REAL, " +
                UserStatTable.Cols.TOTAL_TESTS_TAKEN + " INTEGER, " +
                "FOREIGN KEY("+UserStatTable.Cols.USER_ID + ") " +
                        "REFERENCES " + UserTable.NAME + "(" +  UserTable.Cols.ID +")" +
                ")");

         sqLiteDatabase.execSQL("CREATE TABLE " + WordStatTable.NAME + "(" +
         WordStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
         WordStatTable.Cols.WORD_ID + " INTEGER, " +
         WordStatTable.Cols.OVERALL_ACCURACY + " REAL, " +
         "FOREIGN KEY("+WordStatTable.Cols.WORD_ID + ") " +
         "REFERENCES " + WordTable.NAME + "(" +  WordTable.Cols.ID +")" +
         ")");

         sqLiteDatabase.execSQL("CREATE TABLE " + UserWordStatTable.NAME + "(" +
         UserWordStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
         UserWordStatTable.Cols.USER_STAT_ID + " INTEGER, " +
         UserWordStatTable.Cols.WORD_STAT_ID + " INTEGER, " +
         UserWordStatTable.Cols.USER_WORD_ACCURACY + " REAL, " +
         "FOREIGN KEY(" + UserWordStatTable.Cols.USER_STAT_ID + ") " +
         "REFERENCES " + UserStatTable.NAME + "(" +  UserStatTable.Cols.ID +"), " +
         "FOREIGN KEY(" + UserWordStatTable.Cols.WORD_STAT_ID + ") " +
         "REFERENCES " + WordStatTable.NAME + "(" +  WordStatTable.Cols.ID +")" +
         ")");

        **/

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

         /**
        wordValues.put(WordTable.Cols.SPELLING, "carrot");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);

        wordValues.put(WordTable.Cols.SPELLING, "dog");
        sqLiteDatabase.insert(WordTable.NAME, null, wordValues);

         **/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);

    }
}
