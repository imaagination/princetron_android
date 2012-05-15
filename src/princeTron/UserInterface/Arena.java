package princeTron.UserInterface;

/* Activity that controls the actual gameplay. Owns two layouts - lobby and arena.
 * Workflow: (1) onCreate - instantiates components, connects network manager
 *     (2) either responds to an invitation (via Handler) or sends (via readyToPlay)
 *     (3) goToArena() called. Inflats XML view
 *     (4) startGame() called. Timer starts ticking, etc. C
 */


import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ListView;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.Arrays;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import princeTron.Engine.*;
import princeTron.Network.NetworkIP;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;

public class Arena extends Activity {

	private static final int TRUE = 1;

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
	public static final int LOBBY_UPDATE = 10;
	public static final int PLAYER_CRASH = 11;

	private GameEngine engine;
	private NetworkIP network;
	// for the callback to start the game
	private StartHandler handler;
	public ArrayList<String> invitees;
	private boolean toIgnore;
	private String accountName = "";
	private ArenaView mArenaView;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			try {
				setContentView(R.layout.lobby_layout);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			toIgnore = false; // for ignoring invites while playing
			mArenaView = (ArenaView) findViewById(R.id.arena);
			handler = new StartHandler();
			invitees = new ArrayList<String>();
			accountName = getIntent().getStringExtra("userName");
			// initialize components
			network = new NetworkIP();
			network.connect();
			engine = new GameEngine(handler, network);
			network.setGameEngine(engine);
			try {
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			}
			catch (Exception e) {
				
			}
			// so that when the user changes the volume, it affects the sound fx
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
		if (engine == null) engine = new GameEngine(handler, network);
		Log.i("Arena", "engine instantiated");
		if (!network.logIn(accountName)) {
			Toast toast = Toast.makeText(Arena.this, "Log in failed. Relaunch to try again.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mArenaView.setMode(ArenaView.LOSE);
			network.disconnect();
		}
		catch (Exception e) {

		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		network.disconnect();
	}

	public void readyToPlay(View view) {
		network.readyToPlay(invitees);
	}

	public void goToArena(){
		toIgnore = true;
		Log.i("Arena", "going to arena");
		setContentView(R.layout.arena_layout);
		if (mArenaView != null && mArenaView.engine == null) {
			Log.i("Arena", "engineThread is null5");
		}
		if (mArenaView == null) {
			mArenaView = (ArenaView) findViewById(R.id.arena);
		}
		if (engine == null) {
			engine = new GameEngine(handler, network);
		}
		mArenaView.setGameEngine(engine);
	}

	public void startGame() {
		mArenaView.setMode(ArenaView.READY);
		mArenaView = (ArenaView) findViewById(R.id.arena);
		// tells Arena to listen for left/right touches
		mArenaView.setOnTouchListener(mArenaView);
		// to handle activity lifecycle issues
		if (mArenaView != null && mArenaView.engine == null) {
			Log.i("Arena", "engineThread is null6");
			mArenaView.setGameEngine(engine);
		}
		try {
			// starts timer ticking, etc
			mArenaView.setMode(ArenaView.RUNNING);
		}
		catch (Exception e) {
		}
	}

	class StartHandler extends Handler {

		private void handleLogIn(Object msgobj) {
			try {
				Log.i("Arena", "in LOGGED_IN");
				String[] others = (String[]) msgobj;
				Arrays.sort(others);
				ListView logged_in_list = (ListView) findViewById(android.R.id.list);
				try {
					ArrayAdapter<String> items = new ArrayAdapter<String>(Arena.this, R.layout.invite_item, others);
					logged_in_list.setAdapter(items);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				logged_in_list.setOnItemClickListener(new OnItemClickListener() {

					boolean firsttime = true;
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// add an invitee
						String text = (String) ((TextView) view).getText();
						try {
							if(!invitees.contains(text) && !text.equals(accountName)){
								Arena.this.invitees.add(text);
								TextView tv = (TextView) findViewById(R.id.invitee_list);
								String names = tv.getText().toString();
								if (firsttime) {tv.setText(text); firsttime = false;}
								else tv.setText(text + "\n" + names);}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Object loginObj;

		@Override
		public void handleMessage(Message msg) {
			Log.i("Arena", "" + msg.what);
			AlertDialog.Builder builder;
			switch (msg.what){
			case Arena.LOGGED_IN:
				Log.i("Arena", "in LOGGED_IN");
				if (mArenaView != null && mArenaView.engine == null) {
					Log.i("Arena", "engineThread is null2");
				}
				handleLogIn(msg.obj);
				loginObj = msg.obj;
				break;
			case Arena.IN_LOBBY:
				toIgnore = false;
				mArenaView.toPlay = false;
				try {
					if (mArenaView != null && mArenaView.engine == null) {
						Log.i("Arena", "engineThread is null");
					}
					builder = new AlertDialog.Builder(Arena.this);
					String report = (String) msg.obj;
					builder.setMessage(report + "Do you want to go back to the lobby?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = getIntent();
							finish();
							startActivity(intent);
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
				if (toIgnore) break;
				if (mArenaView != null && mArenaView.engine == null) {
					Log.i("Arena", "engineThread is null3");
				}
				builder = new AlertDialog.Builder(Arena.this);
				builder.setMessage("Do you want to accept the invitation from " + (String) msg.obj + "?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Arena.this.network.acceptInvitation();
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
				if (mArenaView != null && mArenaView.engine == null) {
					Log.i("Arena", "engineThread is null4");
				}
				mArenaView.myId = msg.arg1;
				Toast toast = Toast.makeText(Arena.this, "About to play...", (int) Math.floor(Toast.LENGTH_SHORT/8));
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
				network.userCrash((Coordinate) msg.obj, msg.arg1);
				break;
			case LOBBY_UPDATE:
				if (msg.arg1 == TRUE) {
					if (!toIgnore) {
						//toast = Toast.makeText(Arena.this, msg.obj + " has entered the lobby", Toast.LENGTH_SHORT/3);
						//toast.show();
					}
					String[] otherUsers = new String[((String[])loginObj).length + 1];
					for (int i = 0; i < otherUsers.length - 1; i++) {
						otherUsers[i] = ((String[]) loginObj)[i];
					}
					otherUsers[otherUsers.length - 1] = (String) msg.obj;
					try {
						handleLogIn(otherUsers);
					}
					catch (Exception e) {

					}
					loginObj = otherUsers;
				}
				else {
					if (!toIgnore) {
						//toast = Toast.makeText(Arena.this, msg.obj + " has left the lobby", Toast.LENGTH_SHORT/3);
						//toast.show();
					}
					String[] otherUsers = new String[((String[])loginObj).length - 1];
					int i = 0;
					int j = 0;
					while (i < otherUsers.length) {
						if (((String[])loginObj)[j].equals(msg.obj)) {
							j++;
						}
						else {
							otherUsers[i] = ((String[])loginObj)[j];
							i++;
							j++;
						}
					}
					try {
						handleLogIn(otherUsers);
					}
					catch (Exception e) {

					}
					loginObj = otherUsers;
				}
				break;
			case PLAYER_CRASH:
				SharedPreferences settings = getSharedPreferences(PrinceTron.PREFS_NAME, 0);
				boolean soundOn = settings.getBoolean("soundOn", true);
				if (soundOn) {
					try {
						MediaPlayer mp = MediaPlayer.create(Arena.this, R.raw.metalcrash);
						mp.setVolume(0.1f, 0.1f);
						mp.start();
						mp.setOnCompletionListener(new OnCompletionListener() {

							public void onCompletion(MediaPlayer mp) {
								mp.release();
							}

						});
					}
					catch (Exception e) {}
				}
			}
		}
	}
}