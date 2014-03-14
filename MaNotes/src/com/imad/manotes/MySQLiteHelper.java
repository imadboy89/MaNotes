package com.imad.manotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
 
	public static final String TABLE_NOTES = "notes";
	public static final String TABLE_CAT = "cat";
	public static final String TABLE_TRASH = "notes_trash";

	private static final String DATABASE_NAME = "MaNotes.db";
	private static final int DATABASE_VERSION = 12;

	// Database creation sql statement
	private static final String DATABASE_CREATE_NOTES = "create table "
			+ TABLE_NOTES
			+ "(" 
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT , " 
			+ "note TEXT NOT NULL, "
			+ "color TEXT NOT NULL, "
			+ "date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			+ "date_updated DATETIME , "
			+ "id_online INTEGER DEFAULT 0 , "
			+ "isSynch INTEGER DEFAULT 0 , "
			+ "cat INTEGER DEFAULT 1);";
	private static final String DATABASE_CREATE_CAT = "create table "
			+ TABLE_CAT
			+ "(" 
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT , " 
			+ "note TEXT NOT NULL); ";
	private static final String DATABASE_CREATE_TRASH = "create table "
			+ TABLE_TRASH
			+ "(" 
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "id_online INTEGER NOT NULL UNIQUE, "
			+ "date_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP  "
			+ "); ";
	private static final String DATABASE_CREATE_TR_MOVE_TO_TRASH = "CREATE TRIGGER `move_to_trash` BEFORE DELETE ON notes FOR EACH ROW "
			+" WHEN OLD.id_online <> 0 "
			+"BEGIN "
			+"INSERT INTO `notes_trash` "
			+" ( `id_online`) "
			+"VALUES "
			+" ( OLD.`id_online`) ;"
			+"END";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_NOTES);
		database.execSQL(DATABASE_CREATE_TRASH);
		database.execSQL(DATABASE_CREATE_CAT);
		database.execSQL(DATABASE_CREATE_TR_MOVE_TO_TRASH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRASH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
		db.execSQL("DROP TABLE IF EXISTS move_to_trash");
		onCreate(db);
	}

}