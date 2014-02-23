package com.imad.manotes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    public Notes nts = new Notes(this);
    public int buttons_id ; 
    //public String BUTTON_BG = "label_";
    //public String EDITOR_BG = "note_editor_bg_";
    //public String BUTTON_BG = "label_";
    private Toast toastMesage ;
    public static int NEW_NOTCE=1;
    public static int  UPDATE_NOTCE=2;

	//private DAO dao;
	private Log i;
	public Map<Integer, Integer> buttons_notes = new HashMap<Integer, Integer>();
	
	public Map<String, Integer> BUTTON_BG = new HashMap<String, Integer>();
	
	;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BUTTON_BG.put("blue", R.drawable.label_blue);
		BUTTON_BG.put("red", R.drawable.label_red);
		BUTTON_BG.put("green", R.drawable.label_green);
		BUTTON_BG.put("yellow", R.drawable.label_yellow);
		
		nts.fillNotes();
		
	}

	public void openNote(View v){
		Intent intent = new Intent(this, NoteActivity.class);
		int request = NEW_NOTCE;
		if(R.id.Add_new_Btn != v.getId()){
			Note n = nts.getNoteById(buttons_notes.get(v.getId()));
			intent.putExtra("note", n);  
			request = UPDATE_NOTCE;
		}
		startActivityForResult(intent,request);
		
	}
	public void onActivityResult(int requestCode, int resultCode,Intent data){
		i.i("onActivity", "requestcode = "+requestCode);
		i.i("onActivity", "resultCode = "+resultCode);
		if (data != null){
			Note note = (Note) data.getSerializableExtra("note");
			i.i("onActivity", note.getNote());
			if(requestCode == NEW_NOTCE){
				if(resultCode == nts.NEW_NOTE_REQ)note.insert(this, nts);
				
			}else if (requestCode == UPDATE_NOTCE){
				if(resultCode == nts.DELETE_REQ)nts.deleteNote(note.getId());
				if(resultCode == nts.UPDATE_NOTE_REQ)nts.replace(note);
			}
			
		}
		
		
	}
	//private void 
	public void deleteNote(View v){
		 int id = buttons_notes.get(v.getId()-1);
		nts.deleteNote(id);
	}
	public int addButton(String title,String color){
		buttons_id++;//getLayoutInflater().inflate(R.layout.template_button, null);
		Button note_buttton = new Button(this);
		Button delete_buttton = new Button(this);
		//delete_buttton.setId(buttons_id);
		//LinearLayout myButton = new LinearLayout(this, null, R.style.note_button);
		note_buttton.setGravity(Gravity.LEFT);
		note_buttton.setText(title);
		note_buttton.setId(buttons_id);
		buttons_id++;
		delete_buttton.setId(buttons_id);
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.lnrl);
		LinearLayout layout_note = new LinearLayout(this);
		layout_note.setOrientation(LinearLayout.HORIZONTAL);
		int w = this.getWindowManager().getDefaultDisplay().getWidth();
		
		LayoutParams layout_note_btn = new LayoutParams((int) getResources().getDimension(R.dimen.note_btn_w),LayoutParams.MATCH_PARENT);
		LayoutParams layout_delete_btn = new LayoutParams((int) getResources().getDimension(R.dimen.delete_btn), (int) getResources().getDimension(R.dimen.delete_btn));
		LayoutParams layout_note_all = new LayoutParams(LayoutParams.MATCH_PARENT,(int) getResources().getDimension(R.dimen.note_btn_h));
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			note_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.null_img));
			delete_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.delete ));
		    layout_note.setBackgroundDrawable(getResources().getDrawable(BUTTON_BG.get(color)));
		} else {
			note_buttton.setBackground(getResources().getDrawable(R.drawable.null_img));
			delete_buttton.setBackground(getResources().getDrawable(R.drawable.delete ));
		    layout_note.setBackground(getResources().getDrawable(BUTTON_BG.get(color)));
		}
		//layout_note.setBackground(getResources().getDrawable(BUTTON_BG.get(color)));
		//delete_buttton.setGravity(Gravity.RIGHT);
		layout_note.setWeightSum((float) 0.43);
		ll.addView(layout_note, layout_note_all);
		layout_note.addView(note_buttton, layout_note_btn);
		layout_note.addView(delete_buttton, layout_delete_btn);

		note_buttton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	openNote(v);
		    }
		});
		delete_buttton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	deleteNote(v);
		    }
		});
		
		return note_buttton.getId();
	}
	private void setNote(int noteId,int buttonId){
		buttons_notes.put(buttonId, noteId);
	}
	/*
	private void fill(){
		String[] cols=new String[]{"id","title","note","cat","date_added"};
		List<Hashtable> notes = dao.getAll("notes", cols);
		for( Hashtable note : notes ) {
			Enumeration<String> enumKey = note.keys();
			int buttonId = addButton((String) note.get("id"));
			buttons_notes.put(buttonId, (Integer) note.get("id"));
		}
	}
	*/
	public void toast(String msg){
		
		if (toastMesage != null) {
		    toastMesage.setText(msg);
		    toastMesage.show();
		} else {
		    toastMesage = Toast.makeText(this,msg,Toast.LENGTH_LONG);
		    toastMesage.show();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
	    //dao = new DAO(this);
	    //dao.open();
		return true;
	}
	

}
