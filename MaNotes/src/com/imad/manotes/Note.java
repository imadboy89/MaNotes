package com.imad.manotes;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
@SuppressWarnings("serial") 
public class Note implements Serializable{
	private String title;
	private String note;
	private String date_added;
	private String date_updated;
	private String color;
	private int id;
	private int isSynch;
	private int id_online;
	private int cat;
	private String table_name = "notes"; 

	//private DAO dao;
	private Log l;
	//private MainActivity act;
	
	public Note(int id, int id_online, String title, String note, String date_added, String date_updated, int cat,String color,int isSynch) {
		super();
		this.title = title;
		this.note = note;
		this.date_added = date_added;
		this.id = id;
		this.id_online = id_online;
		this.cat = cat;
		this.color = color;
		this.date_updated = date_updated;
		this.isSynch = isSynch;
	}
	public Note(String title, String note,String color, int cat) {
		super();
		this.id = -1;
		this.title = title;
		this.note = note;
		this.cat = cat;
		this.color = color;
	}

	public int getIsSynch() {
		return isSynch;
	}
	public void setIsSynch(int isSynch) {
		this.isSynch = isSynch;
	}

	public int getId_online() {
		return id_online;
	}
	public void setId_online(int id_online) {
		this.id_online = id_online;
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
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", this.getTitle());
		infs.put("note", this.getNote());
		infs.put("cat", this.getCat()+"");
		infs.put("color", this.getColor()+"");
		//infs.put("date_updated", this.getDate_updated()+"");
		if(this.id_online==0) infs.put("id_online", this.getId_online()+"");
		dao.update(table_name, infs,this.getId());
		return 1; 
	}
	public int delete(Context cntxt) {
		DAO dao = new DAO(cntxt);
		int res = dao.delete(this.table_name, "id = "+this.getId()) ;
		return res;
	}
	
	public String toJSON(){
		String result = "";
		String _note="",_title="";
		int flags = Base64.NO_WRAP | Base64.URL_SAFE;
		try {
			_note =  Base64.encodeToString(note.getBytes("UTF-8"),flags);
			_title = Base64.encodeToString(title.getBytes("UTF-8"),flags);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = "{\"id\":\""+id+"\",\"id_online\":\""+id_online+"\",\"title\":\""+_title+"\",\"note\":\""+_note+"\",\"color\":\""+color+"\",\"cat\":\""+cat+"\",\"date_updated\":\""+date_updated+"\",\"date_added\":\""+date_added+"\"}";

		
		return result;
	}
	public Note(JSONObject nto) {
		super();
		int flags = Base64.NO_WRAP | Base64.URL_SAFE;

		try {
			byte[] bytes_title = Base64.decode((String) nto.get("title"), Base64.DEFAULT);
			byte[] bytes_note = Base64.decode((String) nto.get("note"), Base64.DEFAULT);
			
			String title = new String(bytes_title, "UTF-8");
			String note = new String(bytes_note, "UTF-8");
			
			this.title = title;
			this.note = note;
			this.date_added = (String) nto.get("date_added");
			//this.id = (String) nto.get(id);
			if(this.id_online==0) this.id_online= Integer.parseInt((String) nto.get("id_online"));
			this.cat = Integer.parseInt((String) nto.get("cat"));
			this.color = (String) nto.get("color");
			this.date_updated = (String) nto.get("date_updated");
			//this.update(cntxt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void Synch(JSONObject nto,Context cntxt){
		int flags = Base64.NO_WRAP | Base64.URL_SAFE;

		try {
			byte[] bytes_title = Base64.decode((String) nto.get("title"), Base64.DEFAULT);
			byte[] bytes_note = Base64.decode((String) nto.get("note"), Base64.DEFAULT);
			
			String title = new String(bytes_title, "UTF-8");
			String note = new String(bytes_note, "UTF-8");
			
			this.title = title;
			
			this.note = note;
			this.date_added = (String) nto.get("date_added");
			//this.id = (String) nto.get(id);
			if(this.id_online==0) this.id_online= Integer.parseInt((String) nto.get("id_online"));
			this.cat = Integer.parseInt((String) nto.get("cat"));
			this.color = (String) nto.get("color");
			this.date_updated = (String) nto.get("date_updated");
			if(this.date_added.equals("0000-00-00 00:00:00")){
				this.delete(cntxt);
			}
			//this.update(cntxt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
