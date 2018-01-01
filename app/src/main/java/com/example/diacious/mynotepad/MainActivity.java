package com.example.diacious.mynotepad;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import utilities.NoteReminderUtils;
import utilities.NotificationUtils;
import utilities.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/*
* Notepad App
* Start Date: 2017/12/10
* Time: 16:18 GMT + 1
* Author: Akhihiero David(Yerucham)
* Version: 0.0
*/

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NoteAdapter.noteItemClickListener, NoteAdapter.noteItemLongClickListener
{
    private RecyclerView previewRecyclerView;
    private NoteAdapter noteAdapter;
    private final int LOADER_ID = 99;
    private Cursor notes;
    private boolean noNoteUpdatedOrAdded = true;
    public static final String NOTE_TO_NOTE_ACTIVITY = "note";
    public static final String NOTE_ID = "note_id";
    public static final long DEFAULT_NOTE_ID = -1;
    public static final String IS_A_NEW_NOTE_BOOL = "new_note";
    public static final boolean NEW_NOTE = true;
    private ArrayList<Long> idsForItemsToBeDeleted = new ArrayList<>();
    private boolean deleteMode = false;
    private Menu menuObject;
    private ActionBar actionBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpTheme();

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null)
        {
            if (intentThatStartedThisActivity.hasExtra(SignInActivity.PASSWORD_VERIFIED_KEY))
            {
                if (intentThatStartedThisActivity.getBooleanExtra(SignInActivity.PASSWORD_VERIFIED_KEY, false))
                {
                    //do nothing
                }
            }

            else if (intentThatStartedThisActivity.hasExtra(NoteActivity.NOTE_ACTIVITY))
            {
                //do nothing
            }

            else if (intentThatStartedThisActivity.hasExtra(SettingsActivity.SETTINGS_ACTIVITY))
            {
                //do nothing
            }

            else if (PreferenceUtils.themeChangedAtRuntime())
            {
                //do nothing
            }

            else if (PreferenceUtils.isPasswordOn(this))
            {
                Intent toSignInActivity = new Intent(this, SignInActivity.class);
                toSignInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toSignInActivity);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        previewRecyclerView = (RecyclerView)findViewById(R.id.note_rv);
        noteAdapter = new NoteAdapter(this, this, this);
        previewRecyclerView.setAdapter(noteAdapter);
        previewRecyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        previewRecyclerView.setLayoutManager(layoutManager);

        actionBar = getSupportActionBar();

        getAllNotes();
        NoteReminderUtils.scheduleNoteReminder(this);


    }


    @Override
    protected void onResume() {
        super.onResume();
        getAllNotes();

        NotificationUtils.cancelNotifications(this);
    }

    /**
     * Method to set up app background colors
     */
    private void setUpTheme() {

        int themeId = PreferenceUtils.getThemeId(this);
        setTheme(themeId);
    }
    /**
     * Method to get the notes from the database
     * Uses a loader that runs in a background thread
     */

    public void getAllNotes(){
        Bundle bundle = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> myLoader = loaderManager.getLoader(LOADER_ID);
        if(myLoader == null)
            loaderManager.initLoader(LOADER_ID, bundle, this);
        else
            loaderManager.restartLoader(LOADER_ID, bundle, this);

    }

    /**
     * Method to create a new note or view/edit existing note
     */
    private void toNoteActivity(String note, long id){
        Intent toNoteActivity = new Intent(this, NoteActivity.class);
        if (note != null)
        {
            toNoteActivity.putExtra(NOTE_TO_NOTE_ACTIVITY, note);
            toNoteActivity.putExtra(NOTE_ID, id);
        }
        else
        {
            toNoteActivity.putExtra(IS_A_NEW_NOTE_BOOL, NEW_NOTE);
        }
        //Log.d("MAIN", "Starting note activity");
        startActivity(toNoteActivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor allNotesCache = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(allNotesCache != null && noNoteUpdatedOrAdded)
                    deliverResult(allNotesCache);
                else
                {
                    forceLoad();
                    noteUpdated(true);
                }

            }

            @Override
            public Cursor loadInBackground() {
                ContentResolver resolver = getContentResolver();
                return resolver.query(NoteContract.NoteEntries.CONTENT_URI,
                        null,
                        null,
                        null,
                        NoteContract.NoteEntries.COLUMN_TIME + " DESC");
            }

            @Override
            public void deliverResult(Cursor data)
            {
                super.deliverResult(data);
                allNotesCache = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notes = data;
        noteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void noteItemClicked(int position) {
        String note;
        long id;
        if(!deleteMode)
        {
            if (notes.moveToPosition(position))//1 added because adapter position is from 0 while db is from 1
            {
                note = notes.getString(notes.getColumnIndex(NoteContract.NoteEntries.COLUMN_NOTES));
                id = notes.getLong(notes.getColumnIndex(NoteContract.NoteEntries._ID));
                toNoteActivity(note, id);//1 added because adapter position is from 0 while db is from 1
            } else
                return;
        }
        else
        {
            markForDelete(position);
        }

    }

    @Override
    public void noteItemLongClicked(int position) {
        deleteMode = true;
        markForDelete(position);
        setDeleteButtonVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuObject = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuId = item.getItemId();

        switch (menuId)
        {
            case R.id.delete_btn:
                deleteNotes(idsForItemsToBeDeleted);
                break;
            case R.id.add_btn:
                toNoteActivity(null, -1);
                break;
            case R.id.settings_btn:
                Intent toSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(toSettings);
                break;
            case R.id.about_btn:
                Intent toAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(toAbout);
                break;
            case android.R.id.home:
                cancelDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Method to force the loader to reload when a note is added or updated
     * @param notesUnchanged True if no new note was added, no note deleted or modified else false
     */
    public void noteUpdated(boolean notesUnchanged){
        noNoteUpdatedOrAdded = notesUnchanged;
    }


    /**
     * Method to mark a note item for deletion or unmark a previously marked item
     * @param position The position of the note item to be marked or unmarked
     */
    private void markForDelete(int position){

        notes.moveToPosition(position);
        long id = notes.getLong(notes.getColumnIndex(NoteContract.NoteEntries._ID));
        if(!idsForItemsToBeDeleted.contains(id))
        {
            idsForItemsToBeDeleted.add(id);
        }
        else
        {
            idsForItemsToBeDeleted.remove(id);
        }

        if (idsForItemsToBeDeleted.size() == 0)
        {
            cancelDelete();
            return;
        }
        updateDeleteCount();

    }

    /**
     * Method to make the delete menu item visible or not
     * The delete item should be visible if deleteMode is true else it should be invisible
     * When the delete item is visible the add item is invisible and vice versa
     */
    private void setDeleteButtonVisibility(){
        if (deleteMode)
        {
            menuObject.findItem(R.id.delete_btn).setVisible(true);
            menuObject.findItem(R.id.add_btn).setVisible(false);
        }
        else
        {
            menuObject.findItem(R.id.delete_btn).setVisible(false);
            menuObject.findItem(R.id.add_btn).setVisible(true);
        }
    }


    /**
     * Method to cancel deleteMode(set it to false) as well as signal the adapter to change the background color of all items
     * to transparent
     * The idsForItemsToBeDeleted arraylist is also emptied
     */
    private void cancelDelete(){
        deleteMode = false;
        noteAdapter.setDeleteMode(false);
        noteAdapter.notifyDataSetChanged();

        for (int i = 0;i < noteAdapter.getItemCount();i++)
        {
           // noteAdapter.onBindViewHolder(noteAdapter.getViewHolder(), i);//Not neccesary
        }

        idsForItemsToBeDeleted.clear();
        setDeleteButtonVisibility();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(getString(R.string.app_name));
    }

    /**
     * Method to set the Title of the action bar to the current number of items to be deleted
     */
    private void updateDeleteCount() {
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.valueOf(idsForItemsToBeDeleted.size()));
        }
    }

    /**
     * Method to delete notes
     * @return number of notes deleted
     */
    private int deleteNotes(List<Long> noteIds){
        int numberDeleted = 0;

        for(Long noteId : noteIds)
         numberDeleted += getContentResolver().delete(NoteContract.NoteEntries.CONTENT_URI.buildUpon().appendPath(String.valueOf(noteId)).build(),
                null, null);

        cancelDelete();
        noteUpdated(true);
        onResume();
        Toast.makeText(this,
                numberDeleted > 1 ? getString(R.string.x_notes_deleted, numberDeleted) : getString(R.string.one_note_deleted),
                Toast.LENGTH_SHORT).show();
        return numberDeleted;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deleteMode)
            cancelDelete();
    }

    @Override
    public void onBackPressed() {
        if (deleteMode)
        {
            cancelDelete();
            return;
        }
        super.onBackPressed();
    }
}
