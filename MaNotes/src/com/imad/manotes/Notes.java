package com.imad.manotes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		
		dao.open();
		String[] cols=new String[]{"id","title","note","cat","date_added","date_updated","color"};
		List<Hashtable> notes = dao.getAll("notes", cols);
		for( Hashtable note : notes ) {
			//Enumeration<String> enumKey = note.keys();
			//act.addButton((String) note.get("id"));
			
			Note n = new Note(
					Integer.parseInt((String) note.get("id")),
					(String) note.get("title"),
					(String) note.get("note"),
					(String) note.get("date_added"),
					(String) note.get("date_updated"),
					Integer.parseInt((String) note.get("cat")),
					(String) note.get("color")
					);
			this.notes.add(n);
			//i.i("where", n.getTitle());
			int buttonId = act.addButton( n);
			act.buttons_notes.put(buttonId, Integer.parseInt((String) note.get("id")) );
		}
		dao.close();
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
	
}
