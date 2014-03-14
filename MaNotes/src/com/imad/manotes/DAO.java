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
    	this.open();
        ContentValues values = new ContentValues();
        Enumeration<String> enumKey = infs.keys();
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            String val = (String) infs.get(key);
            values.put(key, val);
        }
        int res = (int)database.insert(table_name, null,values);
        this.close();
        return res ;
    }
    public String getValueByid(String table_name,String column_name,int id) {
    	this.open();
        Cursor cursor = database.query(table_name,
        		new String[]{column_name}, "id = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        String dated_added = cursor.getString(0);
        cursor.close();
        this.close();
        return dated_added;
	}
    public int update(String table_name,Hashtable infs,int id) {
    	this.open();
        ContentValues values = new ContentValues();
        Enumeration<String> enumKey = infs.keys();
        while(enumKey.hasMoreElements()) {
            String key = enumKey.nextElement();
            String val = (String) infs.get(key);
            values.put(key, val);
        }
        if(values.get("date_updated")==null) values.put("date_updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        String whereClause = "id = "+id;
        int res = database.update(table_name, values, whereClause, null);
        this.close();
        return res ;
    }
    
    public int delete(String table_name, String whereClause) {
        //System.out.println("Comment deleted with id: " + id);column_name + " = " + id
    	this.open();
    	int res = database.delete(table_name, whereClause, null);
    	this.close();
        return res;
    }
    
    
    public List<Hashtable> getAll(String table_name,String[] cols) {
       // List<Comment> comments = new ArrayList<Comment>();
    	this.open();
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
        this.close();
        return results;
    }
    public List<Hashtable> getByCond(String table_name,String[] cols,String WHERE) {
    	this.open();
        // List<Comment> comments = new ArrayList<Comment>();
     	List<Hashtable> results = new ArrayList<Hashtable>();
         Cursor cursor = database.query(table_name,cols, WHERE, null, null, null, null);
         
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
         this.close();
         return results;
     }

	public int emptyTrash() {
    	this.open();
    	int res = database.delete("notes_trash",null,null);
    	this.close();
        return res;
		
	}
}
