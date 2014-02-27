package com.imad.manotes;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
@SuppressWarnings("serial") 
public class Note implements Serializable{
	private String title;
	private String note;
	private String date_added;
	private String date_updated;
	private String color;
	private int id;
	private int cat;
	private String table_name = "notes"; 
	

	//private DAO dao;
	private Log l;
	//private MainActivity act;
	
	public Note(int id, String title, String note, String date_added, String date_updated, int cat,String color) {
		super();
		this.title = title;
		this.note = note;
		this.date_added = date_added;
		this.id = id;
		this.cat = cat;
		this.color = color;
		this.date_updated = date_updated;

		
	}
	public Note(String title, String note,String color, int cat) {
		super();
		this.id = -1;
		this.title = title;
		this.note = note;
		this.cat = cat;
		this.color = color;
	}
	public Note() {
		// TODO Auto-generated constructor stub
		super();
		this.id = -1;
	}
	public String getFormatedDate(String date_str){
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "";
		}
		String formattedDate = new SimpleDateFormat("MMMM dd,E ,HH:mm").format(date);
		return formattedDate;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDate_added() {
		return date_added;
	}
	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCat() {
		return cat;
	}
	public void setCat(int cat) {
		this.cat = cat;
	}
	public String getDate_updated() {
		return date_updated;
	}
	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}
	
	
	
	
	public int insert(MainActivity act,Notes notes) {
		DAO dao = new DAO(act);
		dao.open();
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", this.getTitle());
		infs.put("note", this.getNote());
		infs.put("cat", this.getCat()+"");
		infs.put("color", this.getColor()+"");
		
		int id = dao.insert(table_name, infs);
		
		this.date_added = dao.getValueByid("notes", "date_added", id);
		l.i("note",this.getTitle());
		this.setId(id);
		notes.addNote(this);
		//i.i("where", n.getTitle());
		int buttonId = act.addButton( this);
		act.buttons_notes.put(buttonId, this.getId());
		dao.close();
		return id;
	}
	/*
	public int insert(Context cntxt, Notes notes) {
		DAO dao = new DAO(cntxt);
		dao.open();
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", this.getTitle());
		infs.put("note", this.getNote());
		infs.put("cat", this.getCat()+"");
		
		int id = dao.insert(table_name, infs);
		l.i("note",this.getTitle());
		this.setId(id);
		notes.addNote(this);
		//i.i("where", n.getTitle());
		dao.close();
		return id;
	}*/
	public int update(Context cntxt) {
		DAO dao = new DAO(cntxt);
		dao.open();
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", this.getTitle());
		infs.put("note", this.getNote());
		infs.put("cat", this.getCat()+"");
		infs.put("color", this.getColor()+"");
		 dao.update(table_name, infs,this.getId());
		dao.close();
		return 1; 
	}
	public int delete(Context cntxt) {
		DAO dao = new DAO(cntxt);
		dao.open();
		int res = dao.delete(this.table_name, "id = "+this.getId()) ;
		dao.close();
		return res;
	}
}
