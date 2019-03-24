package com.example.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.database.DatabaseSchema.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "spellTest.db";
    private static final String COMMA = ", ";

    private static final String DIFFICULTY_EASY = "Easy";
    private static final String DIFFICULTY_MEDIUM = "Medium";
    private static final String DIFFICULTY_HARD = "Hard";


    public DatabaseHelper (Context context){
        super (context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + UserTable.NAME + "(" +
                UserTable.Cols.ID + " integer primary key autoincrement, " +
                UserTable.Cols.FIRST_NAME + " TEXT, " +
                UserTable.Cols.LAST_NAME + " TEXT" +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + UserStatTable.NAME + "(" +
                UserStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserStatTable.Cols.USER_ID + " INTEGER, " +
                UserStatTable.Cols.OVERALL_ACCURACY + " REAL, " +
                UserStatTable.Cols.TOTAL_TESTS_TAKEN + " INTEGER, " +
                "FOREIGN KEY("+UserStatTable.Cols.USER_ID + ") " +
                        "REFERENCES " + UserTable.NAME + "(" +  UserTable.Cols.ID +")" +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + DifficultyTable.NAME + "(" +
               DifficultyTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DifficultyTable.Cols.DIFFICULTY + " TEXT" +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + WordTable.NAME + "(" +
                WordTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WordTable.Cols.SPELLING + " TEXT, " +
                WordTable.Cols.DEFINITION + " TEXT, " +
                WordTable.Cols.EXAMPLE_SENTENCE + " TEXT, " +
                WordTable.Cols.DIFFICULTY_ID + " INTEGER, " +
                WordTable.Cols.TYPE + " TEXT, " +
                "FOREIGN KEY("+WordTable.Cols.DIFFICULTY_ID + ") " +
                "REFERENCES " + DifficultyTable.NAME + "(" +  DifficultyTable.Cols.ID +")" +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + WordStatTable.NAME + "(" +
                WordStatTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WordStatTable.Cols.WORD_ID + " INTEGER, " +
                WordStatTable.Cols.OVERALL_ACCURACY + " REAL, " +
                "FOREIGN KEY("+WordStatTable.Cols.WORD_ID + ") " +
                "REFERENCES " + WordTable.NAME + "(" +  WordTable.Cols.ID +")" +
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

        sqLiteDatabase.execSQL("CREATE TABLE " + SpellingListWordTable.NAME + "(" +
                SpellingListWordTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpellingListWordTable.Cols.WORD_ID + " INTEGER, " +
                SpellingListWordTable.Cols.SPELLING_LIST_ID + " INTEGER, " +
                "FOREIGN KEY(" + SpellingListWordTable.Cols.WORD_ID + ") " +
                "REFERENCES " + WordTable.NAME + "(" +  WordTable.Cols.ID +"), " +
                "FOREIGN KEY(" + SpellingListWordTable.Cols.SPELLING_LIST_ID + ") " +
                "REFERENCES " + SpellingListTable.NAME + "(" +  SpellingListTable.Cols.ID +")" +
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

        //Now, add basic data to the table:
        sqLiteDatabase.execSQL("INSERT INTO " + DifficultyTable.NAME +
                "(" + DifficultyTable.Cols.DIFFICULTY +") " +
                "VALUES ( 'EASY')");

        sqLiteDatabase.execSQL("INSERT INTO " + DifficultyTable.NAME +
                "(" + DifficultyTable.Cols.DIFFICULTY +") " +
                "VALUES ('MEDIUM')");

        sqLiteDatabase.execSQL("INSERT INTO " + DifficultyTable.NAME +
                "(" + DifficultyTable.Cols.DIFFICULTY +") " +
                "VALUES ('HARD')");

        sqLiteDatabase.execSQL("INSERT INTO " + UserTable.NAME +
                "(" + UserTable.Cols.FIRST_NAME + COMMA + UserTable.Cols.LAST_NAME +") " +
                "VALUES ('Donald', 'Trump')");

        sqLiteDatabase.execSQL("INSERT INTO " + UserTable.NAME +
                "(" + UserTable.Cols.FIRST_NAME + COMMA + UserTable.Cols.LAST_NAME +") " +
                "VALUES ('Hillary', 'Clinton')");

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
