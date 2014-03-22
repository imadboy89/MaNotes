package com.imad.manotes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public Notes nts = new Notes(this);
	public int buttons_id;
	// public String BUTTON_BG = "label_";
	// public String EDITOR_BG = "note_editor_bg_";
	// public String BUTTON_BG = "label_";
	private Toast toastMesage;
	public static int NEW_NOTE = 1;
	public static int UPDATE_NOTE = 2;
	public static int SHOW_NOTE = 3;
	public static Map<String, Typeface> fonts = new HashMap<String, Typeface>();
	// private DAO dao;
	Log i;
	public Map<Integer, Integer> buttons_notes = new HashMap<Integer, Integer>();

	public Map<String, Integer> BUTTON_BG = new HashMap<String, Integer>();
	public static Typeface font;
	private String PREFS_NAME = "MaNotes";
	 DisplayMetrics metrics = new DisplayMetrics();

	 
	 int buttonHeight; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BUTTON_BG.put("blue", R.drawable.label_blue);
		BUTTON_BG.put("red", R.drawable.label_red);
		BUTTON_BG.put("green", R.drawable.label_green);
		BUTTON_BG.put("yellow", R.drawable.label_yellow);

		fonts.put("DEFAULT1", Typeface.DEFAULT);
		fonts.put("DEFAULT2", Typeface.SANS_SERIF);
		fonts.put("DEFAULT3", Typeface.SERIF);
		fonts.put("font1", Typeface.createFromAsset(getAssets(), "fonts/font1.ttf"));
		fonts.put("font2", Typeface.createFromAsset(getAssets(), "fonts/font2.ttf"));
		fonts.put("font3", Typeface.createFromAsset(getAssets(), "fonts/font3.ttf"));
		fonts.put("font4", Typeface.createFromAsset(getAssets(), "fonts/font4.ttf"));
		fonts.put("font5", Typeface.createFromAsset(getAssets(), "fonts/font5.ttf"));
		fonts.put("font6", Typeface.createFromAsset(getAssets(), "fonts/font6.ttf"));
		fonts.put("font7",Typeface.createFromAsset(getAssets(), "fonts/font7.ttf"));
		
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		buttonHeight = metrics.heightPixels/4;
		
		loadSettings();
		nts.fillNotes();
		titleBar();
		buttonEffect(findViewById(R.id.Add_new_Btn));
		/*
		findViewById(R.id.Add_new_Btn).setOnTouchListener(
				new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						scale(v);
						return false;
					}
				});*/
		
		
		Intent intent = getIntent();
		if(!intent.getAction().equals(Intent.ACTION_MAIN)){
			Bundle extras = this.getIntent().getExtras();
			//String[] recipients = (String[]) extras.get(intent.EXTRA_SUBJECT);
			ShareCrtl sc = new ShareCrtl(intent,this);
			openNote(sc);
		}


	}
	public MenuItem synchItem;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Settings();
			return true;
		case R.id.menu_about:
			/*
			 * dlg dl = new dlg(this); dl.show(); //Intent intent = new
			 * Intent(this, dlg.class); Intent intent = getIntent(); P1 =
			 * intent.getStringExtra("p1"); P2 = intent.getStringExtra("p2");
			 */
			//getmail();
			About();
			return true;
		case R.id.menu_sync:
			synchItem = item;
			sync();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void titleBar() {
		// this.getWindow()..setBackgroundDrawable(getResources().getDrawable(Color.TRANSPARENT));
	}
	Dialog dialog ;
	private void About() {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle("About MaNotes V"+getString(R.string.app_version));

		Button dialogButton_ok = (Button) dialog.findViewById(R.id.about_yes);
		dialogButton_ok.setOnClickListener(dlgH);
		dialog.show();
	}
	Builder builder;
	AlertDialog Settings_dlg;
	private String font_name;
	public static float font_size;
	public static boolean font_isBold;
	View layout;

	public void Settings() {
		// LayoutInflater inflater = (LayoutInflater)
		// getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(R.layout.settings_dialog,(ViewGroup) findViewById(R.id.fonts_spinner));
		
		String[] list2 = new String[this.fonts.size()];

		int i = 0;
		for (String key : this.fonts.keySet()) {
			list2[i] = key;
			i++;
		}
		Arrays.sort(list2);
		final ToggleButton tbold = (ToggleButton) layout.findViewById(R.id.toggleBtn_bold);
		Button btn_yes = (Button) layout.findViewById(R.id.btn_yes);
		Button btn_no = (Button) layout.findViewById(R.id.btn_no);
		Button adv = (Button) layout.findViewById(R.id.settings_advanced);
		Button signUp = (Button) layout.findViewById(R.id.settings_signup_btn);
		Button update = (Button) layout.findViewById(R.id.settings_update);
		
		buttonEffect(btn_yes);
		buttonEffect(btn_no);
		
		adv.setOnClickListener(dlgH);
		btn_yes.setOnClickListener(dlgH);
		btn_no.setOnClickListener(dlgH);
		signUp.setOnClickListener(dlgH);
		update.setOnClickListener(dlgH);
		
		EditText un = (EditText) layout.findViewById(R.id.settings_username);
		EditText ps = (EditText) layout.findViewById(R.id.settings_password);
		EditText api = (EditText) layout.findViewById(R.id.settings_apiurl);
		
		un.setText(this.username);
		ps.setText(this.password);
		api.setText(this.API_URL);
		

	    
		final Spinner fonts = (Spinner) layout.findViewById(R.id.fonts_spinner);

		ArrayAdapter<CharSequence> adapter2 = new CustomArrayAdapter<CharSequence>(
				MainActivity.this, list2);
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>
		// (this, android.R.layout.simple_spinner_item,list);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		String ff = null;
		this.i.i("sss", "size fonts" + this.fonts.size());
		for (Map.Entry<String, Typeface> entry : this.fonts.entrySet()) {

			if (font == entry.getValue()) {
				ff = entry.getKey();
			}
		}
		final TextView tv = (TextView) layout.findViewById(R.id.text_tests);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, font_size);
		fonts.setAdapter(adapter2);
		fonts.setSelection(adapter2.getPosition(ff));
		fonts.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				Spinner fonts = (Spinner) layout
						.findViewById(R.id.fonts_spinner);
				tv.setTypeface(MainActivity.fonts.get(fonts.getSelectedItem()),
						(tbold.isChecked()) ? Typeface.BOLD : Typeface.NORMAL);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		final SeekBar sk = (SeekBar) layout.findViewById(R.id.seekBar1);
		sk.setProgress((int) font_size * 3 - 40);
		// sk.setMax(R.string.max-10);
		sk.setKeyProgressIncrement(4);
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

				tv.setTextSize((int) (progress + 40) / 3);

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
		});

		// boolean isBold = tbold.isChecked();
		tbold.setChecked(font_isBold);
		tbold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tv.setTypeface(MainActivity.fonts.get(fonts.getSelectedItem()),
						(isChecked) ? Typeface.BOLD : Typeface.NORMAL);
			}
		});
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("MaNotes Settings :");

		Settings_dlg = builder.create();
		Settings_dlg.show();

	}
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	View.OnClickListener dlgH = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_yes) {
				Spinner fonts = (Spinner) layout.findViewById(R.id.fonts_spinner);
				ToggleButton tbold = (ToggleButton) layout.findViewById(R.id.toggleBtn_bold);
				SeekBar sk = (SeekBar) layout.findViewById(R.id.seekBar1);
				EditText un = (EditText) layout.findViewById(R.id.settings_username);
				EditText ps = (EditText) layout.findViewById(R.id.settings_password);
				EditText api = (EditText) layout.findViewById(R.id.settings_apiurl);

				String ff = (String) fonts.getSelectedItem();
				String fs = "" + (int) (sk.getProgress() + 40) / 3;
				boolean isBold = tbold.isChecked();

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("font", ff);
				editor.putString("size", fs);
				editor.putString("isBold", "" + isBold);
				editor.putString("username", un.getText().toString());
				editor.putString("password", ps.getText().toString());
				editor.putString("API_URL", api.getText().toString());
				editor.commit();

				loadSettings();
			}else if(v.getId() == R.id.settings_advanced){
				LinearLayout advancedL = (LinearLayout) layout.findViewById(R.id.settings_advanced_layout);;
				advancedL.setVisibility((advancedL.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE);
				return ;
			}else if(v.getId() == R.id.settings_update){
				
				update();
			}else if(v.getId() == R.id.about_yes){
				dialog.dismiss();
				return;
			}else if(v.getId() == R.id.settings_signup_btn){
				EditText un = (EditText) layout.findViewById(R.id.settings_username);
				EditText ps = (EditText) layout.findViewById(R.id.settings_password);
				EditText api = (EditText) layout.findViewById(R.id.settings_apiurl);
				
				//EmailValidator.getInstance().isValid(emailAddressString);
				Pattern r = Pattern.compile(EMAIL_PATTERN);
				Matcher m = r.matcher(un.getText().toString());
				//toast(m.group(0));
				//return;
				i.i("eml",un.getText().toString());
				if(m.matches()){
					i.i("eml",m.group(0));
					//return;
					singUp(un.getText().toString(),ps.getText().toString(),api.getText().toString());
				}else{
					toast("Please enter a valide email !");
				}
				
				return;
			}
			Settings_dlg.dismiss();
		}


	};

	public void getEmail(){
	    final EditText input = new EditText(this);
	    boolean res = false;
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Duplecate note .")
		.setMessage("This note already exists !!")
		.setView(input)
		.setPositiveButton("ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						EditText un = (EditText) layout.findViewById(R.id.settings_username);
						EditText ps = (EditText) layout.findViewById(R.id.settings_password);
						EditText api = (EditText) layout.findViewById(R.id.settings_apiurl);
						//String email = input.getText().toString();
						Pattern r = Pattern.compile(EMAIL_PATTERN);
						Matcher m = r.matcher(un.toString());
						//toast(m.group(0));
						//return;
						if(m.matches()){
							//return;
							singUp(un.getText().toString(),ps.getText().toString(),api.getText().toString());
						}else{
							toast("Please enter a valide email !");
						}
						
						return;
					}

				})
				.show();
		
	}
	private void singUp(String un, String ps, String api) {
		NetworkOperation ntw = new NetworkOperation();
		ntw.setAct(this);
		ntw.task =3 ;
		ntw.api = api;
		ntw.un = un;
		ntw.ps = ps;
		ntw.execute();
		
	}
	String API_URL;
	String username;
	String password;

	private void loadSettings() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			font_name = settings.getString("font", "font1");
			font_size = Float.parseFloat(settings.getString("size", "18"));
			font_isBold = Boolean.parseBoolean(settings.getString("isBold", "false"));
			API_URL = settings.getString("API_URL", getString(R.string.MaNotesAPI_URL));
			username = settings.getString("username", "");
			password = settings.getString("password", "");
			font = fonts.get(font_name);
			for (int key : buttons_notes.keySet()) {
				Button btn = (Button) findViewById(key);
				btn.setTypeface(font, (font_isBold) ? Typeface.BOLD : Typeface.NORMAL);
				btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, font_size);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void openNote(View v) {
		openNote(v, "new");
	}
	public void openNote(ShareCrtl sc){
		Intent intent = new Intent(this, NoteActivity.class);
		int request = NEW_NOTE;
		Note n = new Note(sc.title, sc.note, "blue", 0);
		intent.putExtra("note", n);
		intent.putExtra("action", "newFromShareWith");
		startActivityForResult(intent, request);
	}
	public void openNote(View v, String action) {
		Intent intent = new Intent(this, NoteActivity.class);
		int request = NEW_NOTE;
		if (R.id.Add_new_Btn != v.getId() && !action.equals("edit")) {
			Note n = nts.getNoteById(buttons_notes.get(v.getId()));
			intent.putExtra("note", n);
			request = UPDATE_NOTE;
		} else if (action.equals("edit")) {
			Note n = nts.getNoteById(buttons_notes.get(v.getId() - 1));
			intent.putExtra("note", n);
			request = UPDATE_NOTE;
		}
		intent.putExtra("action", action);
		startActivityForResult(intent, request);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		i.i("onActivity", "requestcode = " + requestCode);
		i.i("onActivity", "resultCode = " + resultCode);
		if (data != null) {
			Note note = (Note) data.getSerializableExtra("note");
			i.i("onActivity", note.getNote());
			if (requestCode == NEW_NOTE) {
				if (resultCode == nts.NEW_NOTE_REQ)
					note.insert(this, nts);

			} else if (requestCode == UPDATE_NOTE) {
				if (resultCode == nts.DELETE_REQ)
					nts.deleteNote(note.getId());
				if (resultCode == nts.UPDATE_NOTE_REQ)
					nts.replace(note);
			}

		}

	}

	// private void
	public void deleteNote(View v) {
		int id = buttons_notes.get(v.getId() - 1);
		nts.deleteNote(id);
		buttons_notes.remove(v.getId() - 1);
		// i.i("deleted", "id = "+id);
	}


	 
	public int addButton(Note note) {
		buttons_id++;// getLayoutInflater().inflate(R.layout.template_button,
						// null);
		Button note_buttton = new Button(this);
		Button delete_buttton = new Button(this);
		Button edit_buttton = new Button(this);
		note_buttton.setGravity((int) 0.9);
		note_buttton.setText(note.getTitle());
		note_buttton.setTypeface(font, (font_isBold) ? Typeface.BOLD
				: Typeface.NORMAL);
		note_buttton.setTextSize(TypedValue.COMPLEX_UNIT_SP, font_size);
		note_buttton.setId(buttons_id);
		buttons_id++;
		delete_buttton.setId(buttons_id);
		edit_buttton.setId(buttons_id);

		LinearLayout ll = (LinearLayout) findViewById(R.id.lnrl);
		LinearLayout layout_note = new LinearLayout(this);
		layout_note.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout layout_up = new LinearLayout(this);
		layout_up.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		// int w = this.getWindowManager().getDefaultDisplay().getWidth();
		layout_up.setWeightSum(1f);
		
		note_buttton.setMaxHeight(buttonHeight);
		TextView date_str = new TextView(this);
		date_str.setText(note.getFormatedDate(note.getDate_added()));
		date_str.setTextSize(12);
		date_str.setPadding(10, 1, 1, 1);

		LinearLayout.LayoutParams layout_date = new LinearLayout.LayoutParams(
				0, LayoutParams.MATCH_PARENT, 0.99f);
		// LinearLayout.LayoutParams layout_note_btn = new LinearLayout.
		// LayoutParams(0,LayoutParams.MATCH_PARENT,0.99f);
		LinearLayout.LayoutParams layout_note_btn = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				android.widget.RadioGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams layout_delete_btn = new LinearLayout.LayoutParams(
				(int) getResources().getDimension(R.dimen.delete_btn),
				(int) getResources().getDimension(R.dimen.delete_btn));
		LayoutParams layout_note_all = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);// (int)
																		// getResources().getDimension(R.dimen.note_btn_h)
		LayoutParams layout_up_all = new LayoutParams(
				LayoutParams.MATCH_PARENT, (int) getResources().getDimension(
						R.dimen.delete_btn));
		LayoutParams layout_all_all = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// layout_note_btn.weight(.9f);

		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			// note_buttton.setBackgroundDrawable(getResources().getDrawable(R.drawable.null_img));
			note_buttton.setBackgroundDrawable(getResources().getDrawable(
					android.R.color.transparent));
			delete_buttton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.delete));
			layout_all.setBackgroundDrawable(getResources().getDrawable(
					BUTTON_BG.get(note.getColor())));
			edit_buttton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.edit_));
		} else {
			// note_buttton.setBackground(getResources().getDrawable(R.drawable.null_img));
			note_buttton.setBackground(getResources().getDrawable(
					android.R.color.transparent));
			delete_buttton.setBackground(getResources().getDrawable(
					R.drawable.delete));
			layout_all.setBackground(getResources().getDrawable(
					BUTTON_BG.get(note.getColor())));
			edit_buttton.setBackground(getResources().getDrawable(
					R.drawable.edit_));
		}

		//layout_note.setBackgroundColor(Color.BLUE);
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
				openNote(v, "show");
			}
		});
		delete_buttton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// onClickanimation(v);
				deleteNote(v);
				rotate(v);
			}
		});
		edit_buttton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// onClickanimation(v);
				openNote(v, "edit");
			}
		});
		/*
		 * delete_buttton.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub scale(v); return false; } });
		 * note_buttton.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub
		 * translate((View)v.getParent().getParent()); return false; } });
		 * edit_buttton.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub scale(v); return false; } });
		 */
		return note_buttton.getId();
	}

	public void onClick(View v) {

	}

	public void rotate(View v) {
		final Animation animRotate = AnimationUtils.loadAnimation(this,
				R.anim.rotat);
		v.startAnimation(animRotate);
	}
	public void rotate(MenuItem v) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(R.layout.synch_layout, null);
			iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.synch_icon));
			final Animation animRotate = AnimationUtils.loadAnimation(this,R.anim.rotat);
			animRotate.setRepeatCount(Animation.INFINITE);
			//v.startAnimation(animRotate);
			iv.startAnimation(animRotate);
			v.setActionView(iv);
		}
	}
	public void scale(View v) {
		final Animation animScale = AnimationUtils.loadAnimation(this,R.anim.scale);
		v.startAnimation(animScale);
	}

	public void translate(View v) {
		final Animation animTranslate = AnimationUtils.loadAnimation(this,R.anim.translate);
		v.startAnimation(animTranslate);
	}

	private void setNote(int noteId, int buttonId) {
		buttons_notes.put(buttonId, noteId);
	}

	/*
	 * private void fill(){ String[] cols=new
	 * String[]{"id","title","note","cat","date_added"}; List<Hashtable> notes =
	 * dao.getAll("notes", cols); for( Hashtable note : notes ) {
	 * Enumeration<String> enumKey = note.keys(); int buttonId =
	 * addButton((String) note.get("id")); buttons_notes.put(buttonId, (Integer)
	 * note.get("id")); } }
	 */
	public void toast(String msg) {

		if (toastMesage != null) {
			toastMesage.setText(msg);
			toastMesage.show();
		} else {
			toastMesage = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			toastMesage.show();
		}
	}

	public void getcc(View v) throws IOException, ParseException {
		/*
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nts
				.getNoteById(1).getDate_added());
		String formattedDate = new SimpleDateFormat("MMMM dd,E ,HH:mm")
				.format(date);

		toast(nts.getNoteById(1).getFormatedDate(
				nts.getNoteById(1).getDate_added()));
				*/
		if (isNetworkAvailable()) {
			NetworkOperation nt = new NetworkOperation();
			nt.setAct(this);
			nt.execute();

			return;
		} else {
			toast("there is no connection available !!!");
		}

	}
	public void getmail(){
		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		String gmail = null;

		for(Account account: list)
		{i.i( "androiAccount ",account.type+" : "+account.name);
		    if(account.type.equalsIgnoreCase("com.google"))
		    {
		        gmail = account.name;
		        break;
		    }
		}
		if(gmail!= null){
			this.toast(gmail);
		}else{
			this.toast("nothinkg");
		}
	}
	public AccountManagerCallback<Bundle> AuthTokenCallback() {
		return null;
		
	}
	EditText et ;
	private void sync(){
		List<Note> notes = nts.getNotes();
		//et.setText(nts.toJSON());
		if (isNetworkAvailable()) {
			rotate(synchItem);
			NetworkOperation ntw = new NetworkOperation();
			ntw.setAct(this);
			ntw.execute();
			return;
		} else {
			toast("there is no connection available !!!");
		}
		
	}
	private void update(){

		if (isNetworkAvailable()) {
			NetworkOperation ntw = new NetworkOperation();
			ntw.task=2;
			ntw.setAct(this);
			ntw.execute();
			return;
		} else {
			toast("there is no connection available !!!");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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