package com.imad.manotes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public Notes nts = new Notes(this);
    public int buttons_id ; 
    //public String BUTTON_BG = "label_";
    //public String EDITOR_BG = "note_editor_bg_";
    //public String BUTTON_BG = "label_";
    private Toast toastMesage ;
    public static int NEW_NOTE=1;
    public static int  UPDATE_NOTE=2;
    public static int  SHOW_NOTE=3;

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
		findViewById(R.id.Add_new_Btn).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scale(v);
				return false;
			}
		});
		
	}
	public void openNote(View v) {
		openNote(v,"new");
	}
	public void openNote(View v,String action){
		Intent intent = new Intent(this, NoteActivity.class);
		int request = NEW_NOTE;
		if(R.id.Add_new_Btn != v.getId() && !action.equals("edit")){
			Note n = nts.getNoteById(buttons_notes.get(v.getId()));
			intent.putExtra("note", n);
			request = UPDATE_NOTE;
		}else if(action.equals("edit")){
			Note n = nts.getNoteById(buttons_notes.get(v.getId()-1));
			intent.putExtra("note", n);
			request = UPDATE_NOTE;
		}
		intent.putExtra("action", action);
		startActivityForResult(intent,request);
		
	}
	public void onActivityResult(int requestCode, int resultCode,Intent data){
		i.i("onActivity", "requestcode = "+requestCode);
		i.i("onActivity", "resultCode = "+resultCode);
		if (data != null){
			Note note = (Note) data.getSerializableExtra("note");
			i.i("onActivity", note.getNote());
			if(requestCode == NEW_NOTE){
				if(resultCode == nts.NEW_NOTE_REQ)note.insert(this, nts);
				
			}else if (requestCode == UPDATE_NOTE){
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
	@SuppressWarnings("deprecation")
	public int addButton(Note note) {
		buttons_id++;//getLayoutInflater().inflate(R.layout.template_button, null);
		Button note_buttton = new Button(this);
		Button delete_buttton = new Button(this);
		Button edit_buttton = new Button(this);
		note_buttton.setGravity((int) 0.9);
		note_buttton.setText(note.getTitle());
		note_buttton.setId(buttons_id);
		buttons_id++;
		delete_buttton.setId(buttons_id);
		edit_buttton.setId(buttons_id);
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.lnrl);
		LinearLayout layout_note = new LinearLayout(this);
		layout_note.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout layout_up = new LinearLayout(this);
		layout_up.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		//int w = this.getWindowManager().getDefaultDisplay().getWidth();
		layout_up.setWeightSum(1f);
		
		TextView date_str = new TextView(this);
		date_str.setText(note.getFormatedDate(note.getDate_added()));
		date_str.setTextSize(12);
		date_str.setPadding(10, 1, 1, 1);
		//date_str.setGravity(date_str.getGravity() | Gravity.TOP);;
		//date_str.setBackgroundColor(Color.BLUE);
		//date_str.setText(note.getDate_added());
		
		LinearLayout.LayoutParams layout_date = new LinearLayout. LayoutParams(0,LayoutParams.MATCH_PARENT,0.99f);
		//LinearLayout.LayoutParams layout_note_btn = new LinearLayout. LayoutParams(0,LayoutParams.MATCH_PARENT,0.99f);
		LinearLayout.LayoutParams layout_note_btn = new LinearLayout. LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams layout_delete_btn = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.delete_btn),(int) getResources().getDimension(R.dimen.delete_btn));
		LayoutParams layout_note_all = new LayoutParams(LayoutParams.MATCH_PARENT,(int) getResources().getDimension(R.dimen.note_btn_h));
		LayoutParams layout_up_all = new LayoutParams(LayoutParams.MATCH_PARENT,(int) getResources().getDimension(R.dimen.delete_btn));
		LayoutParams layout_all_all = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		//layout_note_btn.weight(.9f);
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			note_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.null_img));
			delete_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.delete ));
			layout_all.setBackgroundDrawable(getResources().getDrawable(BUTTON_BG.get(note.getColor())));
			edit_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_));
		} else {
			note_buttton.setBackground(getResources().getDrawable(R.drawable.null_img));
			delete_buttton.setBackground(getResources().getDrawable(R.drawable.delete ));
			layout_all.setBackground(getResources().getDrawable(BUTTON_BG.get(note.getColor())));
			edit_buttton.setBackground(getResources().getDrawable(R.drawable.edit_));
		}
		ll.addView(layout_all, layout_all_all);
		layout_all.addView(layout_up, layout_up_all);
		layout_all.addView(layout_note, layout_note_all);
		layout_note.addView(note_buttton, layout_note_btn);
		layout_up.addView(edit_buttton, layout_delete_btn);
		layout_up.addView(date_str, layout_date);
		layout_up.addView(delete_buttton, layout_delete_btn);

		note_buttton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	openNote(v,"show");
		    }
		});
		delete_buttton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	//onClickanimation(v);
		    	deleteNote(v);
		    	rotate( v);
		    }
		});
		edit_buttton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	//onClickanimation(v);
		    	openNote(v,"edit");
		    }
		});
		/*
		delete_buttton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				scale(v);
				return false;
			}
		});
		note_buttton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				translate((View)v.getParent().getParent());
				return false;
			}
		});
		edit_buttton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				scale(v);
				return false;
			}
		});
		*/
		return note_buttton.getId();
	}
	public void onClick(View v) {
		
	}
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
	
	public void getcc(View v) throws IOException, ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nts.getNoteById(1).getDate_added());
		String formattedDate = new SimpleDateFormat("MMMM dd,E ,HH:mm").format(date);

		toast(nts.getNoteById(1).getFormatedDate(nts.getNoteById(1).getDate_added()));
		if(isNetworkAvailable()){
			NetworkOperation nt = new NetworkOperation();
			nt.setAct(this);
			nt.execute();
			
			return;
		}else{
			toast("there is no nonnection available !!!");
		}

	}
	public void onClickanimation(View v){
		Button btn = (Button) findViewById(v.getId());
		Drawable  bg = btn.getBackground();
		bg.setAlpha(10);
		btn.setBackground(bg);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
	    //dao = new DAO(this);
	    //dao.open();
		return true;
	}
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	

}
class NetworkOperation extends AsyncTask<Void,Void,String>{

	private String  output ;
	private MainActivity mact;
    public void onPreExecute() {
       //do something
    }

    @Override
	protected String doInBackground(Void... params) {
    	output="";
		URL yahoo = null;
		try {
			yahoo = new URL("http://imad-kh.com");

			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(yahoo.openStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
				// System.out.println(inputLine);
				output+=inputLine;
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
    public void setAct(MainActivity mact){
    	this.mact = mact;
    }
	public String getOutPut() {
		return output;
	}
    public void onPostExecute(String msg) {
                 //do something
    	mact.toast(output);

    }
    
}
