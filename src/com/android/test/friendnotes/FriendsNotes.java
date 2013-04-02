package com.android.test.friendnotes;

import com.android.test.friendnotes.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsNotes extends ListActivity {

	private static final int ACTIVITY_EDIT=1;
	private static final int DELETE_ID = Menu.FIRST + 1;
    private NotesDbAdapter mDbHelper;
    
    private EditText etNewNote;    
    private TextView tvUserName;
    private TextView tvBirthDay;    
    
    private String mUserName;
    private String mBirthDay;
    
    private Long mRowId;    
    private Cursor notesCursor=null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_notes);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        
        tvUserName = (TextView) findViewById(R.id.username);
        tvBirthDay = (TextView) findViewById(R.id.userbirthday);
	            
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
		}
                
        etNewNote = (EditText) findViewById(R.id.newNote);
        
        findViewById(R.id.editUser).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(getBaseContext(), FriendEditProfile.class);
				i.putExtra(NotesDbAdapter.KEY_ROWID, mRowId);
				i.putExtra(NotesDbAdapter.KEY_USERNAME, mUserName);
				i.putExtra(NotesDbAdapter.KEY_BIRTHDAY, mBirthDay);
				startActivity(i); 				
			}
		});
        
        findViewById(R.id.createNote).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(etNewNote.getWindowToken(), 0);
		        
				String newnote = etNewNote.getText().toString();
				
				if (newnote.matches("")){
		    		Toast.makeText(getApplicationContext(), "NOTE CAN'T BE EMPTY!", Toast.LENGTH_LONG).show();
		    		return;
		    	}
				
				mDbHelper.createNote(mRowId, newnote);
				notesCursor.requery();				
		        etNewNote.setText("");
			}
		});
        
        getUserDetails();   
        registerForContextMenu(getListView());
    }
    
    private void getUserDetails() {
        if (mRowId != null) {
            Cursor userData = mDbHelper.fetchUserDetails(mRowId);
            startManagingCursor(userData);
            mUserName =userData.getString(
            		userData.getColumnIndexOrThrow(NotesDbAdapter.KEY_USERNAME));
            mBirthDay =userData.getString(
            		userData.getColumnIndexOrThrow(NotesDbAdapter.KEY_BIRTHDAY));
            tvUserName.setText(mUserName);
            tvBirthDay.setText(mBirthDay);
            setTitle(mUserName);
            getUserNotes();
        }
    }
    
    private void getUserNotes() {
        notesCursor = mDbHelper.fetchUserNotes(mRowId);
        startManagingCursor(notesCursor);
                
        // Create an array to specify the fields we want to display in the list (only USERNAME)
        String[] from = new String[]{NotesDbAdapter.KEY_NOTE};            
                    
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
            
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.users_row, notesCursor, from, to);
        setListAdapter(notes);
    }
  	 
  	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);	
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        i.putExtra(NotesDbAdapter.KEY_USERNAME, mUserName);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
  	
  	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
  	
  	@Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                notesCursor.requery();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}