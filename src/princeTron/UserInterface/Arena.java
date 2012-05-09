package princeTron.UserInterface;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import princeTron.Engine.*;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class Arena extends Activity {
	
	public static final int IN_LOBBY = 0;
	public static final int INVITED = 1;
	public static final int INVITATION_ACCEPTED = 2;
	public static final int IN_ARENA = 3;
	public static final int INVITATIONS_PENDING = 4;
	public static final int PLAYING = 5;
	public static final int LOGGED_IN = 6;
	public static final int WIN = 7;
	public static final int LOSE = 8;
	public static final int CRASHED = 9;

	private GameEngineThread engine;
	// for the callback to start the game
	private StartHandler handler;

	class StartHandler extends Handler {
		
		private void logIn(Object msgobj) {
			Log.i("Arena", "in LOGGED_IN");
			String[] others = (String[]) msgobj;
			ListView logged_in_list = (ListView) findViewById(android.R.id.list);
			try {
				//logged_in_list = getListView();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ArrayAdapter<String> items = new ArrayAdapter<String>(Arena.this, R.layout.leaderboard, others);
				logged_in_list.setAdapter(items);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			logged_in_list.setOnItemClickListener(new OnItemClickListener() {
				
				boolean firsttime = true;
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked, show a toast with the TextView text
					try {
						String text = (String) ((TextView) view).getText();
						Arena.this.invitees.add(text);
						TextView tv = (TextView) findViewById(R.id.invitee_list);
						String names = tv.getText().toString();
						if (firsttime) {tv.setText(text); firsttime = false;}
						else tv.setText(text + "\n" + names);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		private Object loginObj;
		
		@Override
		public void handleMessage(Message msg) {
			Log.i("Arena", "" + msg.what);
			AlertDialog.Builder builder;
			switch (msg.what){
			case Arena.LOGGED_IN:
				Log.i("Arena", "in LOGGED_IN");
				if (mArenaView != null && mArenaView.engineThread == null) {
					Log.i("Arena", "engineThread is null2");
				}
				logIn(msg.obj);
				loginObj = msg.obj;
				break;
			case Arena.IN_LOBBY:
				try {
					if (mArenaView != null && mArenaView.engineThread == null) {
						Log.i("Arena", "engineThread is null");
					}
					builder = new AlertDialog.Builder(Arena.this);
					builder.setMessage("Do you want to go back to the lobby?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// figure out how to do shutdown properly
							//finish();
							Intent intent = getIntent();
							finish();
							startActivity(intent);
							//setContentView(R.layout.lobby_layout);
							//logIn(loginObj);
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
					builder.show();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Arena.INVITED:
				// tbd
				if (mArenaView != null && mArenaView.engineThread == null) {
					Log.i("Arena", "engineThread is null3");
				}
				builder = new AlertDialog.Builder(Arena.this);
				builder.setMessage("Do you want to accept the invitation from " + (String) msg.obj + "?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                Arena.this.engine.acceptInvitation();
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                
				           }
				       });
				builder.show();
				//Arena.this.engine.acceptInvitation();
				break;
			case Arena.INVITATION_ACCEPTED:
				// tbd
				break;
			case Arena.IN_ARENA:
				Arena.this.goToArena();
				if (mArenaView != null && mArenaView.engineThread == null) {
					Log.i("Arena", "engineThread is null4");
				}
				Toast toast = Toast.makeText(Arena.this, "About to play...", Toast.LENGTH_SHORT);
				toast.show();
				break;
			case Arena.INVITATIONS_PENDING:
				break;
			case Arena.PLAYING:
				startGame();
				break;
			case WIN:
				mArenaView.setMode(ArenaView.WIN);
				break;
			case LOSE:
				mArenaView.setMode(ArenaView.LOSE);
				break;
			case CRASHED:
				engine.userCrash((Coordinate) msg.obj, msg.arg1);
				break;
			}
		}
	}

	private String accountName = "";
	private ArenaView mArenaView;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			//ListView logged_in_list = (ListView) findViewById(android.R.id.list);
			mArenaView = (ArenaView) findViewById(R.id.arena);
			handler = new StartHandler();
			invitees = new ArrayList<String>();
			accountName = getIntent().getStringExtra("userName");
			Log.i("Arena", "about to instantiate GameEngine");
			try {
				setContentView(R.layout.lobby_layout);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			super.onCreate(savedInstanceState);
		}
	}
	
	public void onResume() {
		super.onResume();
		mArenaView = (ArenaView) findViewById(R.id.arena);
		if (engine == null) engine = new GameEngineThread(handler);
		//mArenaView.setGameEngine(engine);
		Log.i("Arena", "engine instantiated");
		if (!engine.isAlive()) {
			try {
				engine.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!engine.logIn(accountName)) {
			Toast toast = Toast.makeText(Arena.this, "Log in failed. Relaunch to try again.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		try {
			mArenaView.setMode(ArenaView.LOSE);
			engine.disconnect();
			engine.cancel();
		}
		catch (Exception e) {
			
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		engine.disconnect();
		engine.cancel();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		engine.disconnect();
		engine.cancel();
	}

	ArrayList<String> invitees;
	
	public void readyToPlay(View view) {
		//if (invitees.size() == 0) invitees.add("amy.ousterhout@gmail.com");
		engine.readyToPlay(invitees);
	}
	
	/*public void addInvitee(View view) {
		EditText et = (EditText) findViewById(R.id.invitee_edit_text);
		String email = et.getText().toString();
		TextView tv = (TextView) findViewById(R.id.invitee_list);
		String text = tv.getText().toString();
		if (text.equals("None")) tv.setText(email);	
		else tv.setText(text + "\n" + email);
		invitees.add(email);
	}*/
	
	public void goToArena(){
		Log.i("Arena", "going to arena");
		setContentView(R.layout.arena_layout);
		if (mArenaView != null && mArenaView.engineThread == null) {
			Log.i("Arena", "engineThread is null5");
		}
		if (mArenaView == null) {
			mArenaView = (ArenaView) findViewById(R.id.arena);
		}
		if (engine == null) {
			engine = new GameEngineThread(handler);
			engine.start();
			mArenaView.setGameEngine(engine);
		}
		if (!engine.isAlive()) {
			try {
				engine.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		mArenaView.setGameEngine(engine);
		//mArenaView.setTextView((TextView) findViewById(R.id.text));
		mArenaView.setMode(ArenaView.READY);
	}
	
	public void startGame() {
		mArenaView = (ArenaView) findViewById(R.id.arena);
		if (mArenaView != null && mArenaView.engineThread == null) {
			Log.i("Arena", "engineThread is null6");
			mArenaView.setGameEngine(engine);
		}
		try {
			mArenaView.setMode(ArenaView.RUNNING);
		}
		catch (Exception e) {
			//mArenaView.setMode(ArenaView.RUNNING);
		}
	}
}
