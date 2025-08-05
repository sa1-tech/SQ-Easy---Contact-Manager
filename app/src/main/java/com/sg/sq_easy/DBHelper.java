package com.sg.sq_easy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DBHelper manages database creation and CRUD operations
public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "ContactDB";
	private static final int DB_VERSION = 1;

	private static final String TABLE_NAME = "contacts";
	private static final String COL_ID = "id";
	private static final String COL_NAME = "name";
	private static final String COL_PHONE = "phone";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	// Called once when DB is first created
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " TEXT NOT NULL, " + COL_PHONE + " TEXT NOT NULL)";
		db.execSQL(createTable);
	}

	// Search contacts by name or phone
	public Cursor searchContacts(String keyword) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery("SELECT * FROM " + TABLE_NAME +
						" WHERE " + COL_NAME + " LIKE ? OR " + COL_PHONE + " LIKE ?",
				new String[]{"%" + keyword + "%", "%" + keyword + "%"});
	}


	// Called when DB version is upgraded
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop existing table and recreate
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	// Insert a new contact
	public boolean insertContact(String name, String phone) {
		if (name.isEmpty() || phone.isEmpty()) return false;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_NAME, name);
		values.put(COL_PHONE, phone);
		long result = db.insert(TABLE_NAME, null, values);
		db.close();
		return result != -1;
	}

	// Get all contacts
	public Cursor getAllContacts() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
	}

	// Update contact by ID
	public boolean updateContact(int id, String name, String phone) {
		if (id <= 0 || name.isEmpty() || phone.isEmpty()) return false;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_NAME, name);
		values.put(COL_PHONE, phone);
		int result = db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
		db.close();
		return result > 0;
	}

	// Delete contact by ID
	public boolean deleteContact(int id) {
		if (id <= 0) return false;
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
		db.close();
		return result > 0;
	}
}
