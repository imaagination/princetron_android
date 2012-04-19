package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import princeTron.Engine.*;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

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
			case Arena.INVITED:
				
			case Arena.INVITATION_ACCEPTED:
				
			case Arena.IN_ARENA:
				
			case Arena.INVITATIONS_PENDING:
				
			case Arena.PLAYING:
				
			}
		}
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new StartHandler();
		Resources resource = this.getResources();
		ImageView imView = new ImageView(this);
		Drawable draw = resource.getDrawable(R.drawable.waitingroompic);
		imView.setImageDrawable(draw);
		setContentView(imView);
		Log.i("Arena", "about to instantiate GameEngine");
		engine = new GameEngineThread(handler);
		Log.i("Arena", "engine instantiated");
		setContentView(R.layout.arena_layout);
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
}