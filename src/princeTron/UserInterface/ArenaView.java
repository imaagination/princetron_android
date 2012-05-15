package princeTron.UserInterface;

import princeTron.Engine.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View.OnTouchListener;

/**
 * ArenaView: implementation of a simple game of Tron
 */
public class ArenaView extends TileView implements OnTouchListener {

	/**
	 * Current mode of application: READY to run, RUNNING, or you have already
	 * lost. static final ints are used instead of an enum for performance
	 * reasons.
	 */
	private int mMode = NOT_READY;
	public static final int NOT_READY = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;
	public static final int WIN = 4;

	public princeTron.Engine.GameEngine engine;

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int RED_STAR = 1;
	private static final int YELLOW_STAR = 2;
	private static final int GREEN_STAR = 3;
	private static final int BLUE_SQUARE = 4;
	private static final int GREEN_SQUARE = 5;
	private static final int PURPLE_SQUARE = 6;


	private Context mContext;
	private int width;
	private WindowManager wm;

	/**
	 * Create a simple handler that we can use to cause animation to happen.  We
	 * set ourselves as a target and we can use the sleep()
	 * function to cause an update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler;
	private Vibrator vibe;

	private boolean soundOn;
	private boolean vibrateOn;
	// handles timing
	class RefreshHandler extends Handler {
		
		private static final int INTERVAL = 100;
		
		private long startTime;
		
		public void initialize() {
			startTime = System.currentTimeMillis();
			sleep(10);
		}
		
		@Override
		public void handleMessage(Message msg) {
			//Log.i("ArenaView", "ticked!");
			if (!(ArenaView.this.mMode == LOSE || ArenaView.this.mMode == WIN) ) {
				if (System.currentTimeMillis() >= startTime) {
					ArenaView.this.update();
					startTime += INTERVAL;
				}
				sleep(10);
			}
			else {
				Log.i("ArenaView", "in mode LOSE");
			}
		}

		public void sleep(long delayMillis) {
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
		
		public void cancel() {
			this.removeMessages(0);
			ArenaView.this.mMode = LOSE;
			Log.i("ArenaView", ""+System.currentTimeMillis());
		}
	};
	
	
	// handle touch events
	public boolean onTouch(View v, MotionEvent event){
		if (engine == null) return true;
		Log.i("ArenaView 116", "in onTouch");
		float x;       
		if (mMode == NOT_READY) {
			Log.i("ArenaView", "not ready to play yet!");
		}

		Log.i("ArenaView 137", "handling action");

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			if (vibrateOn) vibe.vibrate(50);
			x = event.getX();    
			if (x >= width/2.0) { //right side of screen (favored bc has = sign)
				Log.i("turn direction", "right");
				engine.turn(false);
				return (true);
			}

			if (x < width/2.0) { //left side of screen
				Log.i("turn direction", "left");
				engine.turn(true);
				return (true);
			}					
			break;
		}
		}
		return true;
	}

	public ArenaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SharedPreferences settings = context.getSharedPreferences(PrinceTron.PREFS_NAME, 0);
		soundOn = settings.getBoolean("soundOn", true);
		vibrateOn = settings.getBoolean("vibrateOn", true);
		myId = 0;
		vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		Log.i("ArenaView", "IN CONSTRUCTOR!");
		initArenaView();
		this.mContext = context;
		mMode = 0;
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth(); // deprecated
		mRedrawHandler = new RefreshHandler();
	}

	public void initArenaView() {
		setFocusable(true);
		Resources r = this.getContext().getResources();
		Log.i("ArenaView", "initializing arena view!");
		resetTiles(7);
		loadTile(RED_STAR, r.getDrawable(R.drawable.orange));
		loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellow));
		loadTile(GREEN_STAR, r.getDrawable(R.drawable.outline));
		loadTile(BLUE_SQUARE, r.getDrawable(R.drawable.blue));
		loadTile(PURPLE_SQUARE, r.getDrawable(R.drawable.purple));
		loadTile(GREEN_SQUARE, r.getDrawable(R.drawable.green));
	}

	public void setGameEngine(GameEngine engine) {
		this.engine = engine;
	}

	// avoid double-sound effect
	public boolean toPlay = true;
	
	/**
	 * Updates the current mode of the application (RUNNING or LOSE or the like)
	 * as well as sets the visibility of textview for notification
	 * 
	 * @param newMode
	 */
	public void setMode(int newMode) {
		Log.i("setMode", ""+mMode + "\t" + newMode);
		int oldMode = mMode;
		mMode = newMode;

		if (newMode == RUNNING && oldMode != RUNNING) {
			// handle initialization, start timer
			initArenaView();
			toPlay = true;
			mRedrawHandler.initialize();
			update();
			return;
		}

		if (newMode == READY) {
			Log.i("setMode", "in newMode==READY");

		}
		if (newMode == LOSE || newMode == WIN) {
			// sound effect on crash!
			if (toPlay && soundOn) {
				try {
					toPlay = false;
					MediaPlayer mp = MediaPlayer.create(mContext, R.raw.metalcrash);
					AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
					int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
					int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
					float volume = ((float)currentVolume)/((float)maxVolume);
					mp.setVolume(volume, volume);
					mp.start();
					mp.setOnCompletionListener(new OnCompletionListener() {

						public void onCompletion(MediaPlayer mp) {
							mp.release();
						}

					});
				}
				catch (Exception e) {}
			}
			mRedrawHandler.cancel();
		}
	}




	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's location.
	 */
	public void update() {
		try {
			// update the game. called from ticking handler
			if (engine == null) {
				Log.i("ArenaView", "engine thread is null!");
				return;
			}
			Iterable<Player> players = engine.getPlayers();
			clearTiles();
			updateWalls();
			updateSnake(players);
			invalidate();
			engine.update(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Draws some walls.
	 */
	
	// set by engine
	public int myId;
	
	// draw the walls. 100 is the board size
	private void updateWalls() {
		for (int x = 0; x < princeTron.Engine.GameEngine.X_SCALE + 2; x++) {
			setTile(myId + 1, x, 0);
			setTile(myId + 1, x, princeTron.Engine.GameEngine.Y_SCALE + 1);
		}
		for (int y = 1; y < princeTron.Engine.GameEngine.Y_SCALE + 2; y++) {
			setTile(myId + 1, 0, y);
			setTile(myId + 1, princeTron.Engine.GameEngine.X_SCALE + 1, y);
		}
	}


	// draw paths
	private void updateSnake(Iterable<Player> players) {
		for (Player player : players) {
			for (Coordinate p : player.getPoints()) {
				try {
					// ids are 0-index, colors are 1-indexed
					setTile(player.getId() + 1, p.x, p.y);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
