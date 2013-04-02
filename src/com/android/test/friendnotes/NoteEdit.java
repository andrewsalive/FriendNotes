package com.android.test.friendnotes;

import com.android.test.friendnotes.R;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {
		
	private NotesDbAdapter mDbHelper;
	private EditText etNoteText;
	private static long noteId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTitle(R.string.edit_note);
		
		mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        

		setContentView(R.layout.edit_note);
		
		etNoteText = (EditText) findViewById(R.id.note);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		
		Bundle extras = getIntent().getExtras();
		noteId = extras.getLong(NotesDbAdapter.KEY_ROWID);
		getNoteDetails();
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String newNote = etNoteText.getText().toString();
            	mDbHelper.updateNote(noteId, newNote);            	
            	setResult(RESULT_OK);
                finish();          	
            }
        });
	}
	
	private void getNoteDetails() {        
            Cursor noteDetails = mDbHelper.fetchNoteDetails(noteId);
            startManagingCursor(noteDetails);
            String noteText =noteDetails.getString(
            		noteDetails.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE));
            etNoteText.setText(noteText);        
    }
}
