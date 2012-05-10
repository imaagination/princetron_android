package princeTron.UserInterface;

import princeTron.Engine.*;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;

/**
 * ArenaView: implementation of a simple game of Tron
 */
public class ArenaView extends TileView {

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

	public princeTron.Engine.GameEngineThread engineThread;

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int RED_STAR = 1;
	private static final int YELLOW_STAR = 2;
	private static final int GREEN_STAR = 3;
	private static final int BLUE_SQUARE = 4;
	private static final int GREEN_SQUARE = 5;
	private static final int PURPLE_SQUARE = 6;

	/**
	 * mLastMove: tracks the absolute time when the snake last moved, and is used
	 * to determine if a move should be made based on mMoveDelay.
	 */
	//	private long mLastMove;

	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;


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
	//private TicThread timer = new TicThread(100);
	
	class TicThread extends Thread {
		
		private long interval;
		private boolean toRun = false;
		
		public TicThread(int interval) {
			this.interval = interval;
		}
		
		public void run() {
			while (toRun) {
				try {
					sleep(interval);
					Message msg = mRedrawHandler.obtainMessage();
					msg.sendToTarget();
					yield();
				}
				catch (Exception e) {
					Message msg = mRedrawHandler.obtainMessage();
					msg.sendToTarget();
					yield();
				}
			}
		}
		
		public void cancel() {
			toRun = false;
			interrupt();
		}
	}

	class RefreshHandler extends Handler {
		
		private long[] timesToUpdate;
		private int count;
		
		public RefreshHandler() {
			timesToUpdate = new long[5000];
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < 5000; i++) {
				timesToUpdate[i] = startTime + 100*i;
			}
			count = 1;
		}
		
		@Override
		public void handleMessage(Message msg) {
			//Log.i("ArenaView", "ticked!");
			if (!(ArenaView.this.mMode == LOSE || ArenaView.this.mMode == WIN) ) {
				if (System.currentTimeMillis() >= timesToUpdate[count]) {
					ArenaView.this.update();
					count++;
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
	
	/**
	 * Constructs a ArenaView based on inflation from XML
	 * @param context
	 * @param attrs
	 */
	public ArenaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		Log.i("ArenaView", "IN CONSTRUCTOR!");
		initArenaView();
		this.mContext = context;
		mMode = 0;
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth(); // deprecated
		// THIS HAS BEEN MOVED
		this.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				if (engineThread == null) return true;
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
					vibe.vibrate(50);
					x = event.getX();    
					if (x >= width/2.0) { //right side of screen (favored bc has = sign)
						Log.i("turn direction", "right");
						engineThread.turn(false);
						return (true);
					}

					if (x < width/2.0) { //left side of screen
						Log.i("turn direction", "left");
						engineThread.turn(true);
						return (true);
					}					
					break;
				}
				}
				return true;
			}
		});
	}

	public void initArenaView() {
		setFocusable(true);
		Resources r = this.getContext().getResources();
		Log.i("ArenaView", "initializing arena view!");
		resetTiles(7);
		loadTile(RED_STAR, r.getDrawable(R.drawable.orange));
		loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
		loadTile(GREEN_STAR, r.getDrawable(R.drawable.outline));
		loadTile(BLUE_SQUARE, r.getDrawable(R.drawable.blue));
		loadTile(PURPLE_SQUARE, r.getDrawable(R.drawable.purple));
		loadTile(GREEN_SQUARE, r.getDrawable(R.drawable.green));
	}

	public void setGameEngine(GameEngineThread engineThread) {
		this.engineThread = engineThread;
	}

	/**
	 * Sets the TextView that will be used to give information (such as "Game
	 * Over" to the user.
	 * 
	 * @param newView
	 */
	public void setTextView(TextView newView) {
		mStatusText = newView;
	}

	/**
	 * Updates the current mode of the application (RUNNING or LOSE or the like)
	 * as well as sets the visibility of textview for notification
	 * 
	 * @param newMode
	 */
	// THIS STUFF MIGHT GET CHANGED
	public void setMode(int newMode) {
		Log.i("setMode", ""+mMode + "\t" + newMode);
		int oldMode = mMode;
		mMode = newMode;

		if (newMode == RUNNING && oldMode != RUNNING) {
			Log.i("setMode", "newMode==RUNNING && oldMode != RUNNING");
			try {
				//mStatusText.setVisibility(View.INVISIBLE);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			initArenaView();
			//timer.start();
			mRedrawHandler = new RefreshHandler();
			mRedrawHandler.sleep(10);
			return;
		}

		Resources res = getContext().getResources();
		String str = "";
		if (newMode == READY) {
			Log.i("setMode", "in newMode==READY");
			str = res.getText(R.string.mode_ready).toString();
		}
		if (newMode == LOSE || newMode == WIN) {
			str = "Game Over";
			if (newMode == LOSE) {
				str += "\nYou Lose!";
			}
			else {
				str += "\nYou Win!";
			}
			mRedrawHandler.cancel();
			//timer.cancel();
			Log.i("ArenaView 326", "in newMode==LOSE");
			return;
		}
	}




	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's location.
	 */
	public void update() {
		try {
			//Log.i("ArenaView", "updating!");
			if (engineThread == null) {
				Log.i("ArenaView", "engine thread is null!");
				return;
			}
			Iterable<Player> players = engineThread.getPlayers();
			clearTiles();
			updateWalls();
			updateSnake(players);
			invalidate();
			engineThread.update();
			return;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Draws some walls.
	 */
	// THIS WILL BE MOVED
	private void updateWalls() {
		for (int x = 0; x < 99; x++) {
			setTile(GREEN_STAR, x, 0);
			setTile(GREEN_STAR, x, 99);
		}
		for (int y = 1; y < 99; y++) {
			setTile(GREEN_STAR, 0, y);
			setTile(GREEN_STAR, 99, y);
		}
	}


	/**
	 * Figure out which way the player is going, see if he's run into anything 
	 * If he's not going to die, we then add to the front and to increase the trail. 
	 */
	// THIS WILL BE MOVED MOSTLY - SEE BELOW
	private void updateSnake(Iterable<Player> players) {
		for (Player player : players) {
			//Log.i("playerid", ""+player.getId());
			/*ArrayList<Coordinate> tempPoints = new ArrayList<Coordinate>();
			boolean cloned = true;
			for (Coordinate p : player.getPoints()) {
				Coordinate c = (Coordinate) p.clone();
				if (c != null) {
					tempPoints.add((Coordinate) p.clone());
				}
				else {
					cloned = false;
				}
			}
			if (!cloned) tempPoints = (ArrayList<Coordinate>) player.getPoints();*/
			for (Coordinate p : player.getPoints()) {
				try {
					//Log.i("x, y", p.x + ", " + p.y);
					setTile(player.getId() + 1, p.x, p.y);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}