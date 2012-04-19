package princeTron.UserInterface;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import princeTron.Engine.*;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.accounts.Account;
import android.accounts.AccountManager;

public class Arena extends Activity{
	
	public static final int IN_LOBBY = 0;
	public static final int INVITED = 1;
	public static final int INVITATION_ACCEPTED = 2;
	public static final int IN_ARENA = 3;
	public static final int INVITATIONS_PENDING = 4;
	public static final int PLAYING = 5;

	private GameEngineThread engine;
	// for the callback to start the game
	private StartHandler handler;

	class StartHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case Arena.IN_LOBBY:
				Toast toast = Toast.makeText(Arena.this, "In Lobby", Toast.LENGTH_SHORT);
				toast.show();
				break;
			case Arena.INVITED:
				// tbd
				toast = Toast.makeText(Arena.this, "Got invitation", Toast.LENGTH_SHORT);
				toast.show();
				Arena.this.engine.acceptInvitation();
				break;
			case Arena.INVITATION_ACCEPTED:
				// tbd
				break;
			case Arena.IN_ARENA:
				toast = Toast.makeText(Arena.this, "About to play...", Toast.LENGTH_SHORT);
				toast.show();
				break;
			case Arena.INVITATIONS_PENDING:
				
			case Arena.PLAYING:
				Arena.this.goToArena();
				break;
			}
		}
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new StartHandler();
		invitees = new ArrayList<String>();
		Resources resource = this.getResources();
		Log.i("Arena", "about to instantiate GameEngine");
		engine = new GameEngineThread(handler);
		Log.i("Arena", "engine instantiated");
		engine.start();
		Log.i("Arena", "engine started");
		Account[] accounts = AccountManager.get(this).getAccounts();
		String accountName = "";
		for (Account account : accounts) {
			if (account.name.contains("@gmail.com")) {
				accountName = account.name;
			}
			Log.i("Arena", "account name: " + accountName);
		}
		engine.logIn(accountName);
		setContentView(R.layout.lobby_layout);
	}

	ArrayList<String> invitees;
	
	public void readyToPlay(View view) {
		if (invitees.size() == 0) invitees.add("amy.ousterhout@gmail.com");
		engine.readyToPlay(invitees);
	}
	
	public void addInvitee(View view) {
		EditText et = (EditText) findViewById(R.id.invitee_edit_text);
		String email = et.getText().toString();
		// TODO: Validate email!
		TextView tv = (TextView) findViewById(R.id.invitee_list);
		String text = tv.getText().toString();
		if (text.equals("None")) tv.setText(email);	
		else tv.setText(text + "\n" + email);
		invitees.add(email);
	}
	
	public void goToArena(){
		Log.i("Arena", "going to arena");
		setContentView(R.layout.arena_layout);
		ArenaView mArenaView = (ArenaView) findViewById(R.id.arena);
		mArenaView.setGameEngine(engine);
		mArenaView.setTextView((TextView) findViewById(R.id.text));
		mArenaView.setMode(ArenaView.READY);
		try {
			Thread.sleep(1000); // give it time to inflate the view
			mArenaView.setMode(ArenaView.RUNNING);
		}
		catch (Exception e) {
			mArenaView.setMode(ArenaView.RUNNING);
		}
	}
}