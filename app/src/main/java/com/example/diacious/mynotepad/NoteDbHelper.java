package com.example.diacious.mynotepad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Date;

/**
 * Created by Akhihiero David(Yerucham) on 12/11/2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "MyNotepad.db";
    private static final int DATABASE_VERSION = 3;

    public NoteDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CREATE_NOTE_TABLE = "CREATE TABLE " + NoteContract.NoteEntries.TABLE_NAME +
                "(" + NoteContract.NoteEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteContract.NoteEntries.COLUMN_NOTES + " TEXT, " +
                NoteContract.NoteEntries.COLUMN_TIME + " TEXT NOT NULL " +  //TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ");";

        try{
            db.execSQL(CREATE_NOTE_TABLE);
        }
        catch (Exception e){
            Log.d("NoteDbHelper.class", e.getMessage());
        }
        finally {
            Log.d("NoteDbHelper.class","TABLE CREATED OR NOT CREATED");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + NoteContract.NoteEntries.TABLE_NAME;

        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

}
