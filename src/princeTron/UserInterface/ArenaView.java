package princeTron.UserInterface;

import princeTron.Engine.*;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Point;
import java.util.ArrayList;

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

	private princeTron.Engine.GameEngineThread engineThread;

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int RED_STAR = 1;
	private static final int YELLOW_STAR = 2;
	private static final int GREEN_STAR = 3;

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
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (!(ArenaView.this.mMode == LOSE)) {
				ArenaView.this.update();
				ArenaView.this.invalidate();
				sleep(100);
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};


	/*private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// advances all of the players
			if (ArenaView.this.engineThread.update()) {
				//handle collision event
			}
			// updates the UI
			ArenaView.this.update();
			ArenaView.this.invalidate();
			sleep(GameEngine.mMoveDelay);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};*/

	/**
	 * Constructs a ArenaView based on inflation from XML
	 * @param context
	 * @param attrs
	 */
	public ArenaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initArenaView();
		this.mContext = context;
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth(); // deprecated
		// THIS HAS BEEN MOVED
		this.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				Log.i("ArenaView 116", "in onTouch");
				float x;       
				if (mMode == NOT_READY) {
					Log.i("ArenaView", "not ready to play yet!");
				}
				if (mMode == READY) {
					/*
					 * At the beginning of the game, or the end of a previous one,
					 * we should start a new game.
					 */

					Log.w("onKeyDown", "initializing new game up");
					Log.w("onKeyDown", "game initialized");
					setMode(RUNNING);
					Log.w("onKeyDown", "running mode set");
					update();
					Log.w("onKeyDown", "updated");
					mRedrawHandler.sleep(100);
					return (true);
				}

				Log.i("ArenaView 137", "handling action");
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
				{
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

		resetTiles(4);
		loadTile(RED_STAR, r.getDrawable(R.drawable.orange));
		loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
		loadTile(GREEN_STAR, r.getDrawable(R.drawable.outline));

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
			try {
				mStatusText.setVisibility(View.INVISIBLE);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			update();
			return;
		}

		Resources res = getContext().getResources();
		CharSequence str = "";
		if (newMode == READY) {
			str = res.getText(R.string.mode_ready);
		}
		if (newMode == LOSE) {
			str = "Game Over";
			mRedrawHandler.sleep(100000);
			Log.i("ArenaView 326", "in newMode==LOSE");
		}

		mStatusText.setText(str);
		mStatusText.setVisibility(View.VISIBLE);
	}




	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's location.
	 */
	// THIS WILL BE CHANGED SLIGHTLY - updateWalls() and/or updateSnake() will 
	// be function calls into the GameEngine
	// Actually, this is all departing to the GameEngine
	public void update() {
		if (engineThread.update()) {
			setMode(LOSE);
		}
		Iterable<Player> players = engineThread.getPlayers();
		clearTiles();
		updateWalls();
		updateSnake(players);
		invalidate();
	}

	/**
	 * Draws some walls.
	 */
	// THIS WILL BE MOVED
	private void updateWalls() {
		for (int x = 0; x < mXTileCount; x++) {
			setTile(GREEN_STAR, x, 0);
			setTile(GREEN_STAR, x, mYTileCount - 1);
		}
		for (int y = 1; y < mYTileCount - 1; y++) {
			setTile(GREEN_STAR, 0, y);
			setTile(GREEN_STAR, mXTileCount - 1, y);
		}
	}


	/**
	 * Figure out which way the player is going, see if he's run into anything 
	 * If he's not going to die, we then add to the front and to increase the trail. 
	 */
	// THIS WILL BE MOVED MOSTLY - SEE BELOW
	private void updateSnake(Iterable<Player> players) {

		// grab the snake by the head
		/*Point head = players[playerID].playerTrail.get(0);
		Point newHead = new Point(1, 1);

		players[playerID].setDirection(players[playerID].getDirection());


		switch (players[playerID].getDirection()) {
		case EAST: {
			newHead = new Point(head.x + 1, head.y);
			break;
		}
		case WEST: {
			newHead = new Point(head.x - 1, head.y);
			break;
		}
		case NORTH: {
			newHead = new Point(head.x, head.y - 1);
			break;
		}
		case SOUTH: {
			newHead = new Point(head.x, head.y + 1);
			break;
		}
		}

		// Collision detection
		// For now we have a 1-square wall around the entire arena
		if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
				|| (newHead.y > mYTileCount - 2)) {
			setMode(LOSE);
			return;

		}

		// Look for self collisions
		int snakelength1 = players[playerID].getSize();
		for (int snakeindex = 0; snakeindex < snakelength1; snakeindex++) {
			Point c = players[playerID].playerTrail.get(snakeindex);
			Log.w("updateSnake coordinate: ", "" + c.toString());
			if (c.equals(newHead)) {
				setMode(LOSE);
				return;
			}
		}

		// Look for collisions with other players!!!!!!!!!!!!!!!!!!!!!!!!!


		// push a new head onto the ArrayList
		players[playerID].playerTrail.add(0, newHead);
		// -----INTERFACE-----

		int index = 0;
		// playerTrails = GameEngine.getTrails();
		// for (Player player : playerTrails) {
		//		for (Point p : player.playerTrail) {
		//			setTile(...);
		//		}
		// }*/
		for (Player player : players) {
			//Log.i("playerid", ""+player.getId());
			for (Point p : player.getPoints()) {
				try {
					if (player.getId() == 1) {
						//Log.i("x, y", p.x + ", " + p.y);
					}
					setTile(player.getId() + 1, p.x, p.y);
				}
				catch (ArrayIndexOutOfBoundsException e) {
					if (player.getId() == 0) {
						setMode(LOSE);
					}
				}
			}
		}
		/*int index = 0;
		for (Point c : players[playerID].playerTrail) {
			if (index == 0) {
				setTile(YELLOW_STAR, c.x, c.y);
			} else {
				setTile(RED_STAR, c.x, c.y);
			}
			index++;
		}*/

	}
}