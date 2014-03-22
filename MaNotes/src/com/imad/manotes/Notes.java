package com.imad.manotes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Notes {
	private List<Note> notes=new  ArrayList<Note>();
	private DAO dao;
	private Log i;
	private MainActivity act;
	public final static int NEW_NOTE_REQ = 1;
	public final static int UPDATE_NOTE_REQ = 2;
	public final static int DELETE_REQ = 3;
	public Notes(MainActivity act) {
		super();
		this.act = act;
		dao = new DAO(act);
	}

	public void addNote(Note note){
		this.notes.add(note);
	}
	public void selectAll(){
		
	}
	
	public List<Note> getNotes() {
		return this.notes;
	}
	public Note getNoteById(int id){
		for( Note note : this.notes ) {
			if (note.getId() == id){
				Log i = null;
				i.i("noteId", ""+note.getTitle());
				return note;
			}
		}
		return null;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	public int getBtnByNote(int NoteId){
		for (Map.Entry<Integer, Integer> entry : act.buttons_notes.entrySet()) {
		    int key = entry.getKey();
		    int value = entry.getValue();
		    if(value == NoteId)return key;
		}
		return -1;
	}

	/*
	public int insertNote(String table_name, Note note) {
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", note.getTitle());
		infs.put("note", note.getNote());
		infs.put("cat", note.getCat()+"");
		int id = dao.insert(table_name, infs);
		note.setId(id);
		this.notes.add(note);
		//i.i("where", n.getTitle());
		int buttonId = act.addButton( note.getTitle());
		act.buttons_notes.put(buttonId, note.getId());
		
		return id;
	}
	public int updateNote(String table_name, Note note) {
		Hashtable<String,String> infs = new Hashtable<String,String>();
		infs.put("title", note.getTitle());
		infs.put("note", note.getNote());
		infs.put("cat", note.getCat()+"");
		
		return dao.update(table_name, infs,note.getId());
	}
	
	*/
	public int replace(Note newNote){
		int id_note = newNote.getId();
		int res = 0;
		for (int i = 0; i < this.notes.size(); i++) {
			if (this.notes.get(i).getId() == id_note){
				this.notes.set(i, newNote);
				//int btn_id = this.act.buttons_notes.inverse().get(5);
				int btn_id = getKey(this.act.buttons_notes, id_note);
				Button btn = (Button) act.findViewById(btn_id);
				btn.setText(newNote.getTitle());
				int sdk = android.os.Build.VERSION.SDK_INT;
				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					LinearLayout ly = (LinearLayout) btn.getParent().getParent();
					ly.setBackgroundDrawable(act.getResources().getDrawable(act.BUTTON_BG.get(newNote.getColor())));
					
				} else {
					
					LinearLayout ly = (LinearLayout) btn.getParent().getParent();
					ly.setBackground(act.getResources().getDrawable(act.BUTTON_BG.get(newNote.getColor())));
				}
				res = 1;
				break;
			}
		}
		return res;
	}
	
	public void deleteNote(int id){

		Note n = this.getNoteById(id);
		for (int i = 0; i < this.notes.size(); i++) {
			if (this.notes.get(i).getId() == n.getId()){
				this.notes.remove(i);
			}
		}
		int r = n.delete(act);
		int btn_id = getBtnByNote(n.getId());
		View v = (View) act.findViewById(btn_id);
		((ViewManager)v.getParent().getParent().getParent()).removeView((View) v.getParent().getParent());
		if (r>0)
			act.toast("Deleted succefuly");
		else
			act.toast("note deleted");
	}
	public void fillNotes(){
		act.buttons_id = 0;
		
		String[] cols=new String[]{"id","title","note","cat","date_added","date_updated","color","id_online","isSynch"};
		List<Hashtable> notes = dao.getAll("notes", cols);
		for( Hashtable note : notes ) {
			//Enumeration<String> enumKey = note.keys();
			//act.addButton((String) note.get("id"));
			
			Note n = new Note(
					Integer.parseInt((String) note.get("id")),
					Integer.parseInt((String) note.get("id_online")),
					(String) note.get("title"),
					(String) note.get("note"),
					(String) note.get("date_added"),
					(String) note.get("date_updated"),
					Integer.parseInt((String) note.get("cat")),
					(String) note.get("color"),
					Integer.parseInt((String) note.get("isSynch"))
					);
			this.notes.add(n);
			//i.i("where", n.getTitle());
			int buttonId = act.addButton(n);
			act.buttons_notes.put(buttonId, Integer.parseInt((String) note.get("id")) );
		}
	}
	
    static Integer getKey(Map<Integer, Integer> map, int value) {
        Integer key = null;
        for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if(value == entry.getValue()){
            	key = entry.getKey();
            	break;
            }
        }
        return key;
    }
    public void addNote(JSONObject nto){
    	
		Note n = new Note(nto);
		for (int k = 0; k < this.notes.size(); k++) {
			if(n.getId_online()==this.notes.get(k).getId_online())
				return;
			if(n.getTitle().equals(this.notes.get(k).getTitle()) && n.getNote().equals(this.notes.get(k).getNote())){
				act.i.i("deplicae",n.getId()+"");
				this.notes.get(k).setColor(n.getColor());
				int id_on = this.notes.get(k).getId_online();
				this.notes.get(k).setId_online(n.getId_online());
				this.replace(this.notes.get(k));
				n.setId_online(id_on);
				n.delete(act);
				return;
			}
		}
		n.insert(this.act, this);
    }
    public String toJSON(){
    	String result = "[";
    	for( Note note : notes ) {
    		result += note.toJSON()+","; 
    	}
		if(result.length()>1)
			result = result.substring(0,result.length()-1)+"]";
		else
			result = "";
    	return result;
    }
    private String trashToJSON(){
    	String result ="[";
		String[] cols=new String[]{"id_online","date_updated"};
		List<Hashtable> notes = dao.getAll("notes_trash", cols);
		for( Hashtable note : notes ) {
			String id_online = (String) note.get("id_online");
			String date_updated = (String) note.get("date_updated");
			result += "{\"id_online\":\""+id_online+"\",\"date_updated\":\""+date_updated+"\"},";
		}
		this.i.i("params",""+result);
		if(result.length()>1)
			result = result.substring(0,result.length()-1)+"]";
		else
			result = "";
    	return result;
    }
    public String getParams(){
    	String params="";
		try {
			params = "?notes="+URLEncoder.encode(this.toJSON(),"utf-8");
			params += "&remove="+URLEncoder.encode(this.trashToJSON(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.i.i("params",""+params);
    	return params;
    }
	public boolean Synch(String json_str){i.w("Synch Err",json_str);
		JSONArray notesObject = null;
		try {
			notesObject = new JSONArray(json_str);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			try {
				JSONObject errObject = new JSONObject(json_str);
				this.act.toast(errObject.getString("error"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		try {
			
			for ( int i = 0; i < notesObject.length() ; i++)
			{
			   JSONObject object = notesObject.getJSONObject(i);
				Iterator<?> keys = object.keys();
				//Note note = (Note)object.;
				int id =0;
				try{
					id = Integer.parseInt((String)object.get("id"));
				}catch(Exception e){
					this.addNote(object);
					continue;
				}
				boolean isExcest = false;
				for (int k = 0; k < this.notes.size(); k++) {
					if ((this.notes.get(k).getId() == id)){
						isExcest = true;
						this.notes.get(k).Synch(object, this.act);
						//this.notes.get(k).update(this.act);
						if(this.notes.get(k).getDate_added().equals("0000-00-00 00:00:00")){
							//this.notes.get(k).delete(this.act);
							this.deleteNote(this.notes.get(k).getId());
							break;
						}
						
						DAO dao = new DAO(this.act);
						Hashtable<String,String> infs = new Hashtable<String,String>();
						infs.put("title", this.notes.get(k).getTitle());
						infs.put("note", this.notes.get(k).getNote());
						infs.put("cat", this.notes.get(k).getCat()+"");
						infs.put("color", this.notes.get(k).getColor()+"");
						infs.put("date_updated", this.notes.get(k).getDate_updated()+"");
						infs.put("id_online", this.notes.get(k).getId_online()+"");;
						 dao.update("notes", infs,this.notes.get(k).getId());
						
						
						
						this.replace(this.notes.get(k));
						break;
					}
				}
				if(!isExcest){
					this.addNote(object);
				}
				/*
		        while( keys.hasNext() ){
		             String key = (String)keys.next();
		             //if( object.getString(key) instanceof JSONObject ){
		             	
		            	 this.i.i("keys",""+object.get(key));
		             //}
		         }
		         */
			}
			dao.emptyTrash();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			i.e("Synch Err",e.getMessage());
			i.w("Synch Err",json_str);
			return false;
		}
		
		return true;
	}
}
