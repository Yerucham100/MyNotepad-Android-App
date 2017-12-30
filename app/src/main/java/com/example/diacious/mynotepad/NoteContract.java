package com.example.diacious.mynotepad;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Akhihiero David(Yerucham) on 12/11/2017.
 */
//final Contract Class so that it cannot be inherited from
public final class NoteContract
{
    private NoteContract(){}//Constructor Private so that this class cannot be instantiated

    public static final String AUTHORITY = "com.example.diacious.mynotepad";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class NoteEntries implements BaseColumns
    {
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                                              .appendPath(PATH)
                                                              .build();

        public static final String TABLE_NAME = "notes_table";
        public static final String COLUMN_NOTES = "notes_column";
        public static final String COLUMN_TIME = "note_times";


    }
}
