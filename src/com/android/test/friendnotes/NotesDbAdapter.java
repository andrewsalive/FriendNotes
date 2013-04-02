package com.android.test.friendnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class NotesDbAdapter {
		
	public static final String KEY_ROWID = "_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_BIRTHDAY = "birthday";
    
    public static final String KEY_NOTE = "note";
    public static final String KEY_NOTEID = "note_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE_USERS =
        "create table users (_id integer primary key autoincrement, "
        + "username text not null, birthday text not null );";
    private static final String DATABASE_CREATE_NOTES =
        "create table notes (_id integer primary key autoincrement, "
        + "note text not null, note_id integer not null );";    

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_USERS = "users";
    private static final String DATABASE_TABLE_NOTES = "notes";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {        	
            db.execSQL(DATABASE_CREATE_USERS);
            db.execSQL(DATABASE_CREATE_NOTES);            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    } 
    
    public long createUser(String mUserName, String mBirthDay) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_BIRTHDAY, mBirthDay);
        initialValues.put(KEY_USERNAME, mUserName);
        return mDb.insert(DATABASE_TABLE_USERS, null, initialValues);
    }
    
    public void createNote(Long userId, String mNewNote) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOTEID, userId);
        initialValues.put(KEY_NOTE, mNewNote);
        mDb.insert(DATABASE_TABLE_NOTES, null, initialValues);
    }    
    
    public boolean updateUser(long rowId, String userName, String birthDay) {
        ContentValues args = new ContentValues();
        args.put(KEY_USERNAME, userName);
        args.put(KEY_BIRTHDAY, birthDay);
        int isUpdated = mDb.update(DATABASE_TABLE_USERS, args, KEY_ROWID + "=" + rowId, null);
        return isUpdated > 0;
    }
    public boolean updateNote(long rowId, String newNote) {
        ContentValues args = new ContentValues();
        args.put(KEY_NOTE, newNote);
        int isUpdated = mDb.update(DATABASE_TABLE_NOTES, args, KEY_ROWID + "=" + rowId, null);
        return isUpdated > 0;
    }
    
    public boolean deleteUser(long rowId) {
    	mDb.delete(DATABASE_TABLE_NOTES, KEY_NOTEID + "=" + rowId, null);
    	return mDb.delete(DATABASE_TABLE_USERS, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE_NOTES, KEY_ROWID + "=" + rowId, null) > 0;
    }    
    
    public Cursor fetchAllUsers() {
    	Cursor allUsersC = mDb.query(DATABASE_TABLE_USERS, new String[] {KEY_ROWID, KEY_USERNAME }, null, null, null, null, null);
        return allUsersC;        
    }
    
    public Cursor fetchUserDetails(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE_USERS, new String[] {KEY_ROWID, KEY_USERNAME, KEY_BIRTHDAY}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }   
    
    public Cursor fetchUserNotes(long noteId){    	
    	Cursor userNotesC = mDb.query(DATABASE_TABLE_NOTES, null, KEY_NOTEID + "=" + noteId, null, null, null, null);
        return userNotesC;
    }
    
    public Cursor fetchNoteDetails(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE_NOTES, new String[] {KEY_ROWID, KEY_NOTE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void close() {
        mDbHelper.close();
    }
}