package com.android.test.friendnotes;

import com.android.test.friendnotes.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FriendEditProfile extends Activity {	

    private NotesDbAdapter mDbHelper;
    private EditText etUserName;
    private EditText etBirthDay;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        
//        setTitle(R.string.edit_user);

        etUserName = (EditText) findViewById(R.id.username);
        etBirthDay = (EditText) findViewById(R.id.birthday);
        Button confirmButton = (Button) findViewById(R.id.confirm);
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();	
			
			if (extras != null){
			mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
			etUserName.setText(extras.getString(NotesDbAdapter.KEY_USERNAME));
			etBirthDay.setText(extras.getString(NotesDbAdapter.KEY_BIRTHDAY));
			}
		}

        confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {            	
            	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(etUserName.getWindowToken(), 0);
		        
            	populateFields();            	
            }

        });
    }
    
    private void goToUser(Context c, long id){
    	Intent i = new Intent(c, FriendsNotes.class);
    	i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		c.startActivity(i);
		finish();
    }

    private void populateFields() {
    	
    	String mUserName = etUserName.getText().toString();
    	String mBirthDay = etBirthDay.getText().toString();
    	
    	if (mUserName.matches("")){
    		Toast.makeText(getApplicationContext(), "ENTER VALID NAME!", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	if (mRowId != null){
    		if(mDbHelper.updateUser(mRowId, mUserName, mBirthDay)){
    			goToUser(FriendEditProfile.this, mRowId);
    		}
    		else{
    			Toast.makeText(getApplicationContext(), "PROFILE NOT UPDATED!", Toast.LENGTH_LONG).show();
    			return;
    		}
    	}
    	else{
    		Long createdId = mDbHelper.createUser(mUserName, mBirthDay);
        	goToUser(FriendEditProfile.this, createdId);
    	}
    }
}