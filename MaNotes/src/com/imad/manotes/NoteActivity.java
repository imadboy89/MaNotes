package com.imad.manotes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ShareCompat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoteActivity extends Activity {

	private ShareActionProvider mShareActionProvider;
	private Log l = null;
	private Notes nts;
	private Note note;
	private Toast toastMesage ;
	private EditText note_et ;
	private EditText title_et ;
	private Button color_changer;
	public Map<String, Integer> EDITOR_BG = new HashMap<String, Integer>();
	public Map<String, Drawable> colors_bg = new HashMap<String, Drawable>();
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
		/*
		colors_bg.put("blue", "#A9F5F2");
		colors_bg.put("red", "#F5A9A9");
		colors_bg.put("green", "#81F781");
		colors_bg.put("yellow", "#F3F781");
		*/
		colors_bg.put("blue", getResources().getDrawable(R.drawable.color_changer_blue));
		colors_bg.put("red",  getResources().getDrawable(R.drawable.color_changer_red));
		colors_bg.put("green",  getResources().getDrawable(R.drawable.color_changer_green));
		colors_bg.put("yellow",  getResources().getDrawable(R.drawable.color_changer_yellow));
		
		note_et = (EditText) findViewById(R.id.note_editor);
		title_et = (EditText) findViewById(R.id.note_title);
		color_changer = (Button) findViewById(R.id.color_changer);
		
		title_et.setTypeface(MainActivity.font,(MainActivity.font_isBold)?Typeface.BOLD:Typeface.NORMAL);
		note_et.setTypeface(MainActivity.font,(MainActivity.font_isBold)?Typeface.BOLD:Typeface.NORMAL);
		title_et.setTextSize(TypedValue.COMPLEX_UNIT_SP,MainActivity.font_size);
		note_et.setTextSize(TypedValue.COMPLEX_UNIT_SP,MainActivity.font_size);
		
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
				color_changer.setBackgroundDrawable(colors_bg.get(note.getColor()));
			} else {
				note_et.setBackground(getResources().getDrawable(EDITOR_BG.get(note.getColor())));
				color_changer.setBackground(colors_bg.get(note.getColor()));
			}
			//color_changer.setBackground(colors_bg.get(note.getColor()));
			if (intent.getStringExtra("action").equals("show")){
				title_et.setFocusable(false);
				note_et.setFocusable(false);
				note_et.setMovementMethod(LinkMovementMethod.getInstance());
			}else{
				isNewNote=2;
			}
		}else{
			note = new Note();
		}
		affectAnim();
		//l.i("new activity", "d"+note.getId());
	}
	private void affectAnim(){
		buttonEffect(findViewById(R.id.color_changer));
		buttonEffect(findViewById(R.id.back));
		buttonEffect(findViewById(R.id.delete_btn));
		buttonEffect(findViewById(R.id.delete_btn));
		buttonEffect(findViewById(R.id.save_btn));
		buttonEffect(findViewById(R.id.edit));
	}
	public void back(View v){
		onBackPressed();
	}
	public void delete(View v){
		if(note.getId() == -1){
			toast("this Note not saved yet !");
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("note", note);
		setResult(nts.DELETE_REQ,intent);
		finish();
	}
	public void edit(View v){
		toast("edit!");
		title_et.setFocusable(true);
		title_et.setFocusableInTouchMode(true);
		note_et.setFocusableInTouchMode(true);
		note_et.setFocusable(true);
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
		note.setDate_updated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		if (note.isDuplecate(this) && note.getId()==-1) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Duplecate note .")
					.setMessage("This note already exists !!")
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}

							})
							.show();
			return;
		}
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
			toast("Added succefuly");
		else if(res==0 && note.getId()==-1)
			toast("didn't Added succefuly");
		else if (res==0 && note.getId()!=-1)
			toast("didn't updated succefuly");
		else if (res>0 && note.getId()!=-1)
			toast("updated succefuly");
		//////////////////////////////////////
		
		intent.putExtra("note", note);
		setResult(requestCode,intent);
		finish();
	}
	
	public void change_color(View v){
		if(color_i==-1){
			color_i = (note.getId()!=-1) ?Arrays.asList(colors).indexOf(note.getColor()) : 0;
		}
		color_i=(color_i<colors.length-1)?color_i+1:0;
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			note_et.setBackgroundDrawable(getResources().getDrawable(EDITOR_BG.get(colors[color_i])));
			color_changer.setBackgroundDrawable(colors_bg.get(colors[color_i]));
			
		} else {
			note_et.setBackground(getResources().getDrawable(EDITOR_BG.get(colors[color_i])));
			color_changer.setBackground(colors_bg.get(colors[color_i]));
		}
		
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
		MenuItem item = menu.findItem(R.id.menu_item_share);
		item.setOnMenuItemClickListener(shareH);
		if(android.os.Build.VERSION.SDK_INT < 14)
			return true;
	    // Locate MenuItem with ShareActionProvider
	    
	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
	    //String yourShareText = note_et.getText().toString();
        //Intent shareIntent = ShareCompat.IntentBuilder.from(this).setType("text/plain").setText(yourShareText).getIntent();
        //mShareActionProvider.setShareIntent(shareIntent);
	    setShareIntent();
        
        
		return true;
	}
	private void setShareIntent(){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT,title_et.getText().toString());
	    shareIntent.putExtra(Intent.EXTRA_TEXT,note_et.getText().toString());
	    mShareActionProvider.setShareIntent(shareIntent);
	}
	OnMenuItemClickListener shareH = new MenuItem.OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
	        //String yourShareText = note_et.getText().toString();
	        //Intent shareIntent = ShareCompat.IntentBuilder.from(NoteActivity.this).setType("text/plain").setText(yourShareText).getIntent();
	        //mShareActionProvider.setShareIntent(shareIntent);
	        setShareIntent();
			return false;
		}
	};
	public void rotate(View v){
		final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotat);
		v.startAnimation(animRotate);
	}
	public void scale(View v){
		final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
		v.startAnimation(animScale);
	}
	public void translate(View v){
		final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.translate);
		v.startAnimation(animTranslate);
	}
	
	@Override
	public void onBackPressed() {
		if(((note_et.getText().toString().equals("") && title_et.getText().toString().equals("")) && isNewNote == 1)
				|| ( title_et.getText().toString().equals(note.getTitle()) && note_et.getText().toString().equals(note.getNote()) && isNewNote!=2) ){
			finish();
		}else if((note_et.getText().toString().equals("") || title_et.getText().toString().equals(""))){
		    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Back to the main .")
	        .setMessage("Please fill title and note fields .")
	        .setPositiveButton("Cancel any way", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            finish();
	        }

	    })
	    .setNegativeButton("back",  new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {  
	        }

	    })
	    .show();
			return ;
		}else{
			String msg =(isNewNote == 0)? "Save the updated ?" : "Save the new note ?";
		    new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Back to the main .")
		        .setMessage(msg)
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	update_delete_Note(null);
		            //finish();
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
	
	public static void buttonEffect(View button){
	    button.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN: {
	                    v.getBackground().setColorFilter(Color.BLACK,PorterDuff.Mode.SRC_ATOP);
	                    v.invalidate();
	                    break;
	                }
	                case MotionEvent.ACTION_UP: {
	                    v.getBackground().clearColorFilter();
	                    v.invalidate();
	                    break;
	                }
	            }
	            return false;
	        }
	    });
	}
}
