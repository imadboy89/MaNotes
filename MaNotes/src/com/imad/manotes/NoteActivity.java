package com.imad.manotes;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends Activity {

	private Log l = null;
	private Notes nts;
	private Note note;
	private Toast toastMesage ;
	private EditText note_et ;
	private EditText title_et ;
	private Button color_changer;
	public Map<String, Integer> EDITOR_BG = new HashMap<String, Integer>();
	public Map<String, String> colors_bg = new HashMap<String, String>();
	private String[] colors=new String[]{"blue","red","yellow","green"};
	int color_i =-1;
	private int isNewNote = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		//l.i("noteact",""+(Integer) getIntent().getSerializableExtra("note"));
		EDITOR_BG.put("blue", R.drawable.note_editor_bg_blue);
		EDITOR_BG.put("red", R.drawable.note_editor_bg_red);
		EDITOR_BG.put("green", R.drawable.note_editor_bg_green);
		EDITOR_BG.put("yellow", R.drawable.note_editor_bg_yellow);
		
		colors_bg.put("blue", "#A9F5F2");
		colors_bg.put("red", "#F5A9A9");
		colors_bg.put("green", "#81F781");
		colors_bg.put("yellow", "#F3F781");
		
		note_et = (EditText) findViewById(R.id.note_editor);
		title_et = (EditText) findViewById(R.id.note_title);
		color_changer = (Button) findViewById(R.id.color_changer);
		Intent intent = getIntent();
		
		if(intent.hasExtra("note")){
			isNewNote = 0;
			note = (Note) intent.getSerializableExtra("note");
			title_et.setText(note.getTitle());
			note_et.setText(note.getNote());
			this.setTitle(note.getTitle());
			int sdk = android.os.Build.VERSION.SDK_INT;
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				note_et.setBackgroundDrawable(getResources().getDrawable(EDITOR_BG.get(note.getColor())));
				
			} else {
				note_et.setBackground(getResources().getDrawable(EDITOR_BG.get(note.getColor())));
			}
			color_changer.setBackgroundColor(Color.parseColor(colors_bg.get(note.getColor())));
			
		}else{
			note = new Note();
		}

		//l.i("new activity", "d"+note.getId());
	}
	public void back(View v){
		onBackPressed();
	}
	public void delete(View v){
		Intent intent = new Intent();
		intent.putExtra("note", note);
		setResult(nts.DELETE_REQ,intent);
		finish();
	}
	public void update_delete_Note(View v){
		//setResult(10);
		String title_new = title_et.getText().toString();
		String note_new = note_et.getText().toString();
		boolean cond = (note_et.getText().toString().equals("") || title_et.getText().toString().equals(""));
		if(cond){
			toast("please fill all title and note fields .");
			return ;
		}
		
		note.setNote(note_new);
		note.setTitle(title_new);
		
		Intent intent = new Intent();
		int requestCode;
		int res = 0;
		MainActivity mact ;
		if(note.getId()==-1){
			//res = note.insert(mact,mact.nts);
			if(color_i>0 && color_i<colors.length) note.setColor(colors[color_i]);
			else note.setColor(colors[0]);
			
			requestCode = nts.NEW_NOTE_REQ;
		}else{
			if(color_i>0 && color_i<colors.length) note.setColor(colors[color_i]);
			res = note.update(this);
			requestCode = nts.UPDATE_NOTE_REQ;
		}
		//////////////////////////////////////
		if (res>0 && note.getId()==-1)
			toast("inserted succefuly");
		else if(res==0 && note.getId()==-1)
			toast("note inserted succefuly");
		else if (res==0 && note.getId()!=-1)
			toast("not updated succefuly");
		else if (res>0 && note.getId()!=-1)
			toast("updated succefuly");
		//////////////////////////////////////
		
		intent.putExtra("note", note);
		setResult(requestCode,intent);
		finish();
	}
	
	public void change_color(View v){
		if(color_i==-1){
			for (int i =0;i<colors.length;i++) {
				if (colors[i].equals(note.getColor())){
					color_i=(i<colors.length-1)?i:0;
				}
			}
		}
		color_i=(color_i<colors.length-1)?color_i+1:0;
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			note_et.setBackgroundDrawable(getResources().getDrawable(EDITOR_BG.get(colors[color_i])));
			
		} else {
			note_et.setBackground(getResources().getDrawable(EDITOR_BG.get(colors[color_i])));
		}
		color_changer.setBackgroundColor(Color.parseColor(colors_bg.get(colors[color_i])));
	}
	private void toast(String msg){
		
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
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	
	@Override
	public void onBackPressed() {
		if((note_et.getText().toString().equals("") || title_et.getText().toString().equals("")) && isNewNote == 0){
			toast("please fill all title and note fields .");
			return ;
		}else if(((note_et.getText().toString().equals("") && title_et.getText().toString().equals("")) && isNewNote == 1)
				|| ( title_et.getText().toString().equals(note.getTitle()) && note_et.getText().toString().equals(note.getNote())) ){
			finish();
		}else{
			String msg =(isNewNote == 0)? "Save the updated ?" : "Save the new note ?";
		    new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Closing Activity")
		        .setMessage(msg)
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	update_delete_Note(null);
		            finish();
		        }
	
		    })
		    .setNegativeButton("No",  new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            finish();    
		        }
	
		    })
		    .show();
		}
	}
}
