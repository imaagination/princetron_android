package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Configuration;
import princeTron.Engine.*;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

public class Arena extends Activity{

	private GameEngineThread engine;
	// for the callback to start the game
	private StartHandler handler;
	
	class StartHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.i("Arena", "getting a message!");
			Arena.this.goToArena();
		}
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.waitingroom);
		handler = new StartHandler();
		Log.i("Arena", "about to instantiate GameEngine");
		engine = new GameEngineThread(handler);
		Log.i("Arena", "engine instantiated");
		engine.start();
		Log.i("Arena", "engine started");
	}

	public void goToArena(){
		Log.i("Arena", "going to arena");
		setContentView(R.layout.arena_layout);
		ArenaView mArenaView = (ArenaView) findViewById(R.id.arena);
		mArenaView.setGameEngine(engine);
		mArenaView.setTextView((TextView) findViewById(R.id.text));
		mArenaView.setMode(ArenaView.READY);
		try {
			Thread.sleep(1000);
			mArenaView.setMode(ArenaView.RUNNING);
		}
		catch (Exception e) {
			mArenaView.setMode(ArenaView.RUNNING);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // ignore orientation/keyboard change
	  super.onConfigurationChanged(newConfig);
	}
}
