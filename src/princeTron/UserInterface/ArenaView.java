package princeTron.UserInterface;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ArenaView: implementation of a simple game of Snake
 * 
 * 
 */
public class ArenaView extends TileView {

	private static final String TAG = "ArenaView";

	/**
	 * Current mode of application: READY to run, RUNNING, or you have already
	 * lost. static final ints are used instead of an enum for performance
	 * reasons.
	 */
	private int mMode = READY;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;

	/**
	 * Current direction the snake is headed.
	 */
	private int mDirection1 = EAST;
	private int mNextDirection1 = EAST;
	private int mDirection2 = WEST;
	private int mNextDirection2 = WEST;

	private static final int NORTH = 1;
	private static final int SOUTH = 2;
	private static final int EAST = 3;
	private static final int WEST = 4;

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	private static final int RED_STAR = 1;
	private static final int YELLOW_STAR = 2;
	private static final int GREEN_STAR = 3;

	/**
	 * mScore: used to track the number of apples captured mMoveDelay: number of
	 * milliseconds between snake movements. This will decrease as apples are
	 * captured.
	 */
	private long mScore = 0;
	private static final long mMoveDelay = 100;

	/**
	 * mLastMove: tracks the absolute time when the snake last moved, and is used
	 * to determine if a move should be made based on mMoveDelay.
	 */
	private long mLastMove;

	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;

	/**
	 * mSnakeTrail1: a list of Coordinates that make up the snake's body
	 * mAppleList: the secret location of the juicy apples the snake craves.
	 */
	private ArrayList<Coordinate> mSnakeTrail1 = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mSnakeTrail2 = new ArrayList<Coordinate>();

	private Context mContext;
	private int width, height;
	private WindowManager wm;
	private Display display;

	/**
	 * Create a simple handler that we can use to cause animation to happen.  We
	 * set ourselves as a target and we can use the sleep()
	 * function to cause an update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			ArenaView.this.update();
			ArenaView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};


	/**
	 * Constructs a ArenaView based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public ArenaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initArenaView();
		this.mContext = context;

		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		width = display.getWidth();  // deprecated
		height = display.getHeight();  // deprecated

		this.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				Toast toast;
				float x;       
				CharSequence text;

				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
				{
					x = event.getX();    

					if(x > width/2.0)
						text = "Right button pressed!"; //call rightPressed Method
					else
						text = "Left button pressed!"; //call leftPressed Method


					if (x >= width/2.0) { //right side of screen (favored bc has = sign)
						if ((mDirection1 == SOUTH) || (mDirection1 == NORTH)) 
							mNextDirection1 = EAST;
						else
							mNextDirection1 = NORTH;
						
						return (true);
					}

					if (x < width/2.0) { //left side of screen
						if ((mDirection1 == SOUTH) || (mDirection1 == NORTH)) 
							mNextDirection1 = WEST;
						else
							mNextDirection1 = SOUTH;
						return (true);
					}					

					//					toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);  
					//					toast.show();

					break;
				}










				}
				return true;
			}
		});
	}

	public ArenaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initArenaView();
	}

	private void initArenaView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(4);
		loadTile(RED_STAR, r.getDrawable(R.drawable.orange));
		loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
		loadTile(GREEN_STAR, r.getDrawable(R.drawable.outline));

	}


	private void initNewGame() {
		mSnakeTrail1.clear();
		mSnakeTrail2.clear();

		// For now we're just going to load up a short default eastbound snake


		mSnakeTrail1.add(new Coordinate(2, 20));
		mSnakeTrail2.add(new Coordinate(2, 15));
		mNextDirection1 = EAST;


		mScore = 0;
	}





	/*
	 * handles key events in the game. Update the direction the player is traveling
	 * based on the DPAD. Ignore events that would cause the snake to immediately
	 * turn back on itself.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (mMode == READY | mMode == LOSE) {
				/*
				 * At the beginning of the game, or the end of a previous one,
				 * we should start a new game.
				 */
				initNewGame();
				setMode(RUNNING);
				update();
				return (true);
			}


			if (mDirection1 != SOUTH) {
				mNextDirection1 = NORTH;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (mDirection1 != NORTH) {
				mNextDirection1 = SOUTH;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (mDirection1 != EAST) {
				mNextDirection1 = WEST;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (mDirection1 != WEST) {
				mNextDirection1 = EAST;
			}
			return (true);
		}

		return super.onKeyDown(keyCode, msg);
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
	public void setMode(int newMode) {
		int oldMode = mMode;
		mMode = newMode;

		if (newMode == RUNNING & oldMode != RUNNING) {
			mStatusText.setVisibility(View.INVISIBLE);
			update();
			return;
		}

		Resources res = getContext().getResources();
		CharSequence str = "";
		if (newMode == READY) {
			str = res.getText(R.string.mode_ready);
		}
		if (newMode == LOSE) {
			str = res.getString(R.string.mode_lose_prefix) + mScore
					+ res.getString(R.string.mode_lose_suffix);
		}

		mStatusText.setText(str);
		mStatusText.setVisibility(View.VISIBLE);
	}




	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's location.
	 */
	public void update() {
		if (mMode == RUNNING) {
			long now = System.currentTimeMillis();

			if (now - mLastMove > mMoveDelay) {
				clearTiles();
				updateWalls();
				updateSnake();
				mLastMove = now;
			}
			mRedrawHandler.sleep(mMoveDelay);
		}

	}

	/**
	 * Draws some walls.
	 */
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
	 * Figure out which way the snake is going, see if he's run into anything (the
	 * walls, himself). If he's not going to die, we then add to the
	 * front and to increase the trail. 
	 */
	private void updateSnake() {

		// grab the snake by the head
		Coordinate head = mSnakeTrail1.get(0);
		Coordinate newHead = new Coordinate(1, 1);

		mDirection1 = mNextDirection1;

		switch (mDirection1) {
		case EAST: {
			newHead = new Coordinate(head.x + 1, head.y);
			break;
		}
		case WEST: {
			newHead = new Coordinate(head.x - 1, head.y);
			break;
		}
		case NORTH: {
			newHead = new Coordinate(head.x, head.y - 1);
			break;
		}
		case SOUTH: {
			newHead = new Coordinate(head.x, head.y + 1);
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

		// Look for collisions with itself
		int snakelength1 = mSnakeTrail1.size();
		for (int snakeindex = 0; snakeindex < snakelength1; snakeindex++) {
			Coordinate c = mSnakeTrail1.get(snakeindex);
			if (c.equals(newHead)) {
				setMode(LOSE);
				return;
			}



		}

		// push a new head onto the ArrayList
		mSnakeTrail1.add(0, newHead);


		int index = 0;
		for (Coordinate c : mSnakeTrail1) {
			if (index == 0) {
				setTile(YELLOW_STAR, c.x, c.y);
			} else {
				setTile(RED_STAR, c.x, c.y);
			}
			index++;
		}

	}

	/**
	 * Simple class containing two integer values and a comparison function.
	 * There's probably something I should use instead, but this was quick and
	 * easy to build.
	 * 
	 */
	private class Coordinate {
		public int x;
		public int y;

		public Coordinate(int newX, int newY) {
			x = newX;
			y = newY;
		}

		public boolean equals(Coordinate other) {
			return (x == other.x && y == other.y);
		}

		@Override
		public String toString() {
			return "Coordinate: [" + x + "," + y + "]";
		}
	}

}