package princeTron.UserInterface;

import princeTron.Engine.GameEngine;
import princeTron.Network.NetworkIP;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Arena extends Activity {

	private GameEngine engine;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String accountName = getIntent().getStringExtra("userName");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		engine = new GameEngine(this, new NetworkIP());
		engine.logIn(accountName);
		
		openLobby();
	}
	
	public void newLobby(String[] newLobby) {
		TextView loggedIn = (TextView) findViewById(R.id.logged_in_players);
		loggedIn.setText("");
		for (int i = 0; i < newLobby.length; i++) {
			loggedIn.setText(loggedIn.getText() + "\n" + newLobby[i]);
		}
	}
	
	public void openLobby() {
		setContentView(R.layout.lobby_layout);
		
		Button readyButton = (Button) findViewById(R.id.ready_button);
		readyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String[] invites = new String[1];
				invites[0] = "peterman5";
				engine.sendInvites(invites);
				ArenaView av = new ArenaView(getApplicationContext());
				av.setGameEngine(engine);
				engine.setArenaView(av);
				engine.resetGameboard();
				setContentView(av);		
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
