package com.android.test.friendnotes;

import com.android.test.friendnotes.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FriendsList extends ListActivity {
		
	private NotesDbAdapter mDbHelper;
	private Cursor usersCursor;    
	
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final String all = "all";
	private static final String count = "count";
	private static final String birthday = "birthday";
	private Cursor argCursor = null;
	
	private EditText etNoteCount;
	private EditText etBirthDay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();     
        
        etNoteCount = (EditText) findViewById(R.id.notes_count);
        etBirthDay = (EditText) findViewById(R.id.birth_date);
        
        findViewById(R.id.createUser).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {				
				Intent i = new Intent(getBaseContext(), FriendEditProfile.class);
				startActivity(i); 
			}
		});
        
        findViewById(R.id.notes_filter).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(etNoteCount.getWindowToken(), 0);
				getUsersList(count); 
			}
		});
        
        findViewById(R.id.birthday_filter).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(etBirthDay.getWindowToken(), 0);
				getUsersList(birthday); 
			}
		});
        findViewById(R.id.show_all).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				getUsersList(all); 
			}
		});
        getUsersList(all);
        registerForContextMenu(getListView());
    }
    
    
    private void getUsersList(String filter) {
        
    	if (filter == "all"){
    		argCursor = mDbHelper.fetchAllUsers();
    	}
    	else if(filter == "count"){
    		int mNotesConut = Integer.parseInt(etNoteCount.getText().toString());
    		argCursor = mDbHelper.fetchUsersWithCount(mNotesConut);
    	}
    	else if(filter == "birthday"){
    		String mBirthDay = etBirthDay.getText().toString();
    		argCursor = mDbHelper.fetchUsersWithDate(mBirthDay);
    	};
    	
    	usersCursor = argCursor;
        startManagingCursor(usersCursor);        
        
        // Create an array to specify the fields we want to display in the list (only USERNAME)
        String[] from = new String[]{NotesDbAdapter.KEY_USERNAME};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter users = new SimpleCursorAdapter(this, R.layout.users_row, usersCursor, from, to);
        setListAdapter(users);
    }    

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        super.onListItemClick(lv, v, position, id);		
        Intent i = new Intent(this, FriendsNotes.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivity(i);        
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
                mDbHelper.deleteUser(info.id);
                usersCursor.requery();
                return true;
        }
        return super.onContextItemSelected(item);
    }    
}