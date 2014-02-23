package com.imad.manotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_NOTES = "notes";
	public static final String TABLE_CAT = "cat";

	private static final String DATABASE_NAME = "MaNotes.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE_NOTES = "create table "
			+ TABLE_NOTES
			+ "(" 
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT , " 
			+ "note TEXT NOT NULL, "
			+ "color TEXT NOT NULL, "
			+ "date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			+ " cat INTEGER DEFAULT 1);";
	private static final String DATABASE_CREATE_CAT = "create table "
			+ TABLE_CAT
			+ "(" 
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT , " 
			+ "note TEXT NOT NULL); ";
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_NOTES);
		database.execSQL(DATABASE_CREATE_CAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
		onCreate(db);
	}

}