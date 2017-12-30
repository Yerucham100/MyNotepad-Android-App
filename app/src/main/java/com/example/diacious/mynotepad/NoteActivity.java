package com.example.diacious.mynotepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import utilities.DateUtils;
import utilities.PreferenceUtils;

import java.util.Date;

public class NoteActivity extends AppCompatActivity
{
    private TextView noteTextView;
    private EditText noteEditText;
    private ScrollView editScrollView;
    private ScrollView viewScrollView;
    private boolean isANewNote = false;
    private boolean editMode = false;
    private long noteId = -1;
    private MainActivity mainActivityObject;
    private final String TAG = "NoteActivity.class";
    public static final String NOTE_ACTIVITY = "NoteActivity";
    private int viewModeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteTextView = (TextView) findViewById(R.id.note_tv);
        noteEditText = (EditText) findViewById(R.id.note_et);
        editScrollView = (ScrollView) findViewById(R.id.edit_scroll);
        viewScrollView = (ScrollView) findViewById(R.id.view_scroll);

        mainActivityObject = new MainActivity();

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent thatStartedThisActivity = getIntent();
        if(thatStartedThisActivity != null) {

            if (thatStartedThisActivity.hasExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY))
            {
                viewMode();
                noteTextView.setText(thatStartedThisActivity.getStringExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY));
                noteId = thatStartedThisActivity.getLongExtra(MainActivity.NOTE_ID, MainActivity.DEFAULT_NOTE_ID);
            }

            else if (thatStartedThisActivity.hasExtra(MainActivity.IS_A_NEW_NOTE_BOOL))
            {
                isANewNote = thatStartedThisActivity.getBooleanExtra(MainActivity.IS_A_NEW_NOTE_BOOL, MainActivity.NEW_NOTE);
                editMode();
            }
        }

    }

    /**
     * Method to set up app background colors
     */
    private void setUpTheme() {

        int themeId = PreferenceUtils.getThemeId(this);
        setTheme(themeId);
    }

    /**
     * Method to set up edit mode
     * Edit mode: textView is made gone and editText is made visible
     */
    private void editMode(){
        editMode = true;

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        String toBeEdited = noteTextView.getText().toString();
        noteEditText.setVisibility(View.VISIBLE);
        editScrollView.setVisibility(View.VISIBLE);

        noteTextView.setVisibility(View.GONE);
        viewScrollView.setVisibility(View.GONE);

        noteEditText.setText(toBeEdited);
    }

    /**
     * Method to set up view mode
     * Edit mode: textView is made visible and editText is made gone
     */
    private void viewMode(){
        editMode = false;

        String toBeViewed = noteEditText.getText().toString();
        noteEditText.setVisibility(View.GONE);
        editScrollView.setVisibility(View.GONE);

        noteTextView.setVisibility(View.VISIBLE);
        viewScrollView.setVisibility(View.VISIBLE);

        noteTextView.setText(toBeViewed);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (viewModeCount > 0 || isANewNote) //To prevent unpleasant animation when note activity first opened
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        viewModeCount++;
    }

    /**
     * Method to save or update a note in the database
     * @param newNote A Boolean, if true the note is inserted in the DB else it is updated
     */
    private void saveNote(boolean newNote){
        viewMode();
        String note = noteEditText.getText().toString();
        if (note.trim().isEmpty()) //Empty notes will not be saved
            return;

        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntries.COLUMN_NOTES, note);
        cv.put(NoteContract.NoteEntries.COLUMN_TIME, DateUtils.setDateAndTimeForDatabase(new Date()));
        Uri savedNoteUri;

        if(newNote)
        {
            savedNoteUri = getContentResolver().insert(NoteContract.NoteEntries.CONTENT_URI, cv);
            isANewNote = false;
            noteId = Long.parseLong(savedNoteUri.getPathSegments().get(1));
        }
        else
        {
            getContentResolver().update(NoteContract.NoteEntries.CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(noteId))
                            .build(),
                             cv,
                             null,
                             null);
        }
        mainActivityObject.noteUpdated(false);
    }

    /**
     * Method to delete the note
     */
    private void deleteNote()
    {
        if (editMode)
        {
            saveNote(isANewNote);
        }

        if (!isANewNote)
        {
            getContentResolver().delete(NoteContract.NoteEntries.CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(noteId))
                            .build(),
                    null,
                    null);

            mainActivityObject.noteUpdated(false);
            Toast.makeText(this, getString(R.string.one_note_deleted), Toast.LENGTH_SHORT).show();
        }

        Intent toMainActivityIntent = new Intent(NoteActivity.this, MainActivity.class);
        toMainActivityIntent.putExtra(NOTE_ACTIVITY, true);
        toMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMainActivityIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);

        if (isANewNote)
        {
            menu.findItem(R.id.edit_view_btn).setTitle(getString(R.string.save));
            menu.findItem(R.id.edit_view_btn).setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_save));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.edit_view_btn:
                if (editMode)
                {
                    item.setTitle(getString(R.string.edit));
                    item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_edit));
                   // viewMode(); NOW REDUNDANT SINCE SAVE NOTE METHOD CALLS VIEW MODE
                    saveNote(isANewNote);
                }
                else
                {
                    item.setTitle(getString(R.string.save));
                    item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_save));
                    editMode();
                }
                break;

            case R.id.delete_btn_in_note_activity:
                deleteNote();
                break;

            case R.id.settings_btn_in_note_activity:
                if (isANewNote)
                    saveNote(isANewNote);

                Intent toSettingsIntent = new Intent(NoteActivity.this, SettingsActivity.class);
                toSettingsIntent.putExtra(NOTE_ACTIVITY, NOTE_ACTIVITY);
                toSettingsIntent.putExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY, noteTextView.getText().toString());
                toSettingsIntent.putExtra(MainActivity.NOTE_ID, noteId);
                startActivity(toSettingsIntent);
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent thatStartedThisActivity = getIntent();
        String oldNote = "";
        if (thatStartedThisActivity != null)
            if (thatStartedThisActivity.hasExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY))
                oldNote = thatStartedThisActivity.getStringExtra(MainActivity.NOTE_TO_NOTE_ACTIVITY);

        if (!oldNote.equals(noteEditText.getText().toString()) && editMode)
            saveNote(isANewNote);   //Only Save note if note changed

        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
        intent.putExtra(NOTE_ACTIVITY, true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
