package com.imad.manotes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DAO {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public int insert(String table_name,Hashtable infs) {
        ContentValues values = new ContentValues();
        Enumeration<String> enumKey = infs.keys();
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            String val = (String) infs.get(key);
            values.put(key, val);
        }
        //values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        /*
        Cursor cursor = database.query(table_name,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
        */
        return (int) database.insert(table_name, null,values);
    }
    public String getValueByid(String table_name,String column_name,int id) {
    	
        Cursor cursor = database.query(table_name,
        		new String[]{column_name}, "id = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        String dated_added = cursor.getString(0);
        cursor.close();
        return dated_added;
	}
    public int update(String table_name,Hashtable infs,int id) {
        ContentValues values = new ContentValues();
        Enumeration<String> enumKey = infs.keys();
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            String val = (String) infs.get(key);
            values.put(key, val);
        }
        values.put("date_updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        String whereClause = "id = "+id;
        return (int) database.update(table_name, values, whereClause, null);
    }
    
    public int delete(String table_name, String whereClause) {
        //System.out.println("Comment deleted with id: " + id);column_name + " = " + id
        return database.delete(table_name, whereClause, null);
    }
    
    
    public List<Hashtable> getAll(String table_name,String[] cols) {
       // List<Comment> comments = new ArrayList<Comment>();
    	List<Hashtable> results = new ArrayList<Hashtable>();
        Cursor cursor = database.query(table_name,cols, null, null, null, null, null);
        
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
        	Hashtable<String,String> result = new Hashtable<String,String>();
        	for(int i=0;i<cols.length;i++){
        		int icol = cursor.getColumnIndex(cols[i]);
        		try {
        			result.put(cols[i], cursor.getString(icol));
				} catch (Exception e) {
					// TODO: handle exception
				}
        		
        	}
        	results.add(result);
        }
        // make sure to close the cursor
        cursor.close();
        return results;
    }

}
