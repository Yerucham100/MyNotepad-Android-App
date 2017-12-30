package com.example.diacious.mynotepad;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Akhihiero David(Yerucham) on 12/13/2017.
 */

public class NotesProvider extends ContentProvider
{
    private NoteDbHelper dbHelper;
    private static final int ALL_NOTES = 100;
    private static final int SINGLE_NOTE = 101;

    private static final UriMatcher sURI_MATCHER = buildUriMatcher();

    @Override
    public boolean onCreate() {
        dbHelper = new NoteDbHelper(getContext());
        return true;
    }

    /**
     * Method to create a UriMatcher
     * @return The UriMatcher
     */
    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(NoteContract.AUTHORITY, NoteContract.NoteEntries.PATH, ALL_NOTES);
        matcher.addURI(NoteContract.AUTHORITY, NoteContract.NoteEntries.PATH + "/#", SINGLE_NOTE);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = sURI_MATCHER.match(uri);

        switch (match)
        {
            case ALL_NOTES:
                retCursor = db.query(NoteContract.NoteEntries.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case SINGLE_NOTE:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                retCursor = db.query(NoteContract.NoteEntries.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Invalid Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri retUri;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sURI_MATCHER.match(uri);
        long id;

        switch (match)
        {
            case ALL_NOTES:
                id = db.insert(NoteContract.NoteEntries.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Invalid Uri: " + uri);
        }
        if (id > 0)
        {
            retUri = ContentUris.withAppendedId(NoteContract.NoteEntries.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(uri, null);
            return retUri;
        }
        else
        throw new SQLException("Failed to insert at " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        int numberDeleted;

        switch (match)
        {
            case SINGLE_NOTE:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                numberDeleted = db.delete(NoteContract.NoteEntries.TABLE_NAME, mSelection, mSelectionArgs);

                if (numberDeleted < 1)
                    throw new SQLException("Failed to delete at: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Invalid Uri: " + uri);
        }
        return numberDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        int numberUpdated;

        switch (match)
        {
            case SINGLE_NOTE:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                numberUpdated = db.update(NoteContract.NoteEntries.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs);
                if (numberUpdated < 1)
                    throw new SQLException("Failed to update at " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numberUpdated;
    }
}
