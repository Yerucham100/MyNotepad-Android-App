package com.example.diacious.mynotepad;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

import utilities.DateUtils;

/**
 * Created by Akhihiero David(Yerucham) on 12/10/2017.
 */

//Note Adapter Class

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>
{

    private Cursor notes; //Notes Cursor
    private Context mContext;
    private noteItemClickListener mNoteItemClickListener;
    private noteItemLongClickListener mNoteItemLongClickListener;
    private boolean deleteMode = false;//REDUNDANT VARIABLE
    private NoteViewHolder noteViewHolder;
    private ArrayList<String> positionsMarkedForDelete = new ArrayList<>();



    public NoteAdapter(Context context, noteItemClickListener listener, noteItemLongClickListener longClickListener)
    {
        this.notes = null;
        this.mContext = context;
        this.mNoteItemClickListener = listener;
        this.mNoteItemLongClickListener = longClickListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int idForLayout = R.layout.note_items;
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(idForLayout, parent, shouldAttachToParentImmediately);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        String note;
        String dateTime;
        noteViewHolder = holder;

        final String NOTES_COLUMN = NoteContract.NoteEntries.COLUMN_NOTES;
        final String DATE_TIME_COLUMN = NoteContract.NoteEntries.COLUMN_TIME;
        if(!notes.moveToPosition(position))
            return;
        else
        {
            note = notes.getString(notes.getColumnIndex(NOTES_COLUMN));
            holder.notePreviewTextView.setText(getNotePreview(note));
            dateTime = DateUtils.formatNoteTime(notes.getString(notes.getColumnIndex(DATE_TIME_COLUMN)), mContext);
            holder.dateTimeTextView.setText(dateTime);

            if (!positionsMarkedForDelete.contains(String.valueOf(position)))
            {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
                holder.setItemMarkedForDelete(false);
            }

            else if (positionsMarkedForDelete.contains(String.valueOf(position)))
            {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDelete));
            }
        }

    }

    /*UNNECESSARY METHOD
     * Method to return a viewholder object for main activity
     * @return A viewholder object

    public NoteViewHolder getViewHolder()
    {
        return noteViewHolder;
    }
    */




    @Override
    public int getItemCount() {
        if(notes == null)
            return 0;
        return notes.getCount();
    }


    /**
     * Method to change the cursor object
     * @param notes The new cursor object
     */
    public void swapCursor(Cursor notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    /**
     * Method to set the delete mode for the recycler view
     * @param delete Boolean to set with
     */
    public void setDeleteMode(boolean delete){
        this.deleteMode = delete;
        if (!delete)
        positionsMarkedForDelete.clear();//Clear positions if deleteMode is false
    }


    /**
     * Method to get a note preview from the full note
     * @param note The full note
     * @return A clipped version of the full note
     */
    private String getNotePreview(String note) {
        int newLineCount = 0;
        int indexOfSecondNewLine = 0;
        for (int i = 0;i < note.length();i++)
        {
            if (note.charAt(i) == '\n')
            {
                newLineCount++;
                if (newLineCount == 2)
                    indexOfSecondNewLine = i;
            }
        }

      if (newLineCount > 2)
          return note.substring(0, indexOfSecondNewLine) + "\n...";
      else if (note.length() <= 100)
          return note;
      else
          return note.substring(0, 101) + "...";
    }

    /**
     * noteItemClickListener interface
     */
    public interface noteItemClickListener
    {
        void noteItemClicked(int position);
    }

    /**
     * noteItemClickListener interface
     */
    public interface noteItemLongClickListener
    {
        void noteItemLongClicked(int position);
    }

    /**
     * ViewHolder Class
     */
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        TextView notePreviewTextView;// Textview that will show a preview of the note
        TextView dateTimeTextView;
        private boolean itemMarkedForDelete;//REDUNDANT VARIABLE


        public NoteViewHolder(View view)
        {
            super(view);
            notePreviewTextView = (TextView) view.findViewById(R.id.note_preview_tv);
            dateTimeTextView = (TextView) view.findViewById(R.id.date_and_time_tv);
            itemMarkedForDelete = false;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (deleteMode)
            {
                alterBackgroundColor(v);
            }

            mNoteItemClickListener.noteItemClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            alterBackgroundColor(v);
            deleteMode = true;
            mNoteItemLongClickListener.noteItemLongClicked(getAdapterPosition());
            return true;
        }

        /**
         * Method to change the background color of a recycler view item
         * Change to grey if marked for delete else transparent
         * @param v View object
         */
        private void alterBackgroundColor(View v){
            if (!positionsMarkedForDelete.contains(String.valueOf(getAdapterPosition())))
            {
                v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDelete));
                positionsMarkedForDelete.add(String.valueOf(getAdapterPosition()));
            }
            else
            {
                v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
                positionsMarkedForDelete.remove(String.valueOf(getAdapterPosition()));
            }
        }

        /**REDUNDANT METHOD
         * Method to change the value of the markedForDelete class variable
         * @param markedForDelete Boolean to change to
         */
        public void setItemMarkedForDelete(boolean markedForDelete){
            itemMarkedForDelete = markedForDelete;
        }

    }
}
