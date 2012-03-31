package princeTron.UserInterface;

import princeTron.Engine.Coordinate;
import princeTron.Engine.Player;

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

/**
 * ArenaView: implementation of a simple game of Tron
 */
public class ArenaView extends TileView {

	/**
	 * Current mode of application: READY to run, RUNNING, or you have already
	 * lost. static final ints are used instead of an enum for performance
	 * reasons.
	 */
	private int mMode = READY;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;

	public int playerID = 0;			//stores which player the user controls
	private int numPlayers = 1;  		//number of players
	private Player[] players;			//array of players to be drawn


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
	 * mMoveDelay: number of milliseconds between player movements.
	 * This essentially controls game speed
	 */
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
	 * @param context
	 * @param attrs
	 */
	public ArenaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initArenaView();
		this.mContext = context;
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth(); // deprecated  
		this.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				float x;       


				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
				{
					x = event.getX();    

					int direction = players[playerID].getCurDirection();

					if (x >= width/2.0) { //right side of screen (favored bc has = sign)
						if ((direction == SOUTH) || (direction == NORTH)) 
							players[playerID].setNextDirection(EAST);
						else if (direction == WEST)
							players[playerID].setNextDirection(NORTH);
						else
							players[playerID].setNextDirection(SOUTH);

						return (true);
					}

					if (x < width/2.0) { //left side of screen
						if ((direction == SOUTH) || (direction == NORTH)) 
							players[playerID].setNextDirection(WEST);
						else if(direction == WEST)
							players[playerID].setNextDirection(SOUTH);
						else
							players[playerID].setNextDirection(NORTH);

						return (true);
					}					
					break;
				}
				}
				return true;
			}
		});
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
		//clear all player lists

		for(int i = 0; i < numPlayers; i++){
			if(players != null)
				players[i].playerTrail.clear();
		}



		players = new Player[numPlayers];
		Player player;


		//should initialize based on screen height and width
		if(numPlayers == 1){
			player = new Player(new Coordinate(2, 20), EAST);
			players[0] = player;
		}

		else if(numPlayers == 2){
			player = new Player(new Coordinate(2, 20), EAST);
			players[0] = player;

			player = new Player(new Coordinate(40, 20), WEST);
			players[1] = player;
		}

		else if(numPlayers == 3){
			player = new Player(new Coordinate(2, 20), EAST);
			players[0] = player;

			player = new Player(new Coordinate(40, 20), WEST);
			players[1] = player;

			player = new Player(new Coordinate(19, 2), SOUTH);
			players[2] = player;
		}

		else if(numPlayers == 4){
			player = new Player(new Coordinate(2, 20), EAST);
			players[0] = player;

			player = new Player(new Coordinate(40, 20), WEST);
			players[1] = player;

			player = new Player(new Coordinate(19, 2), SOUTH);
			players[2] = player;

			player = new Player(new Coordinate(10, 40), NORTH);
			players[3] = player;
		}



	}





	/**
	 * handles key events in the game. Update the direction the player is traveling
	 * based on the DPAD and screen touch
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (mMode == READY | mMode == LOSE) {
				/*
				 * At the beginning of the game, or the end of a previous one,
				 * we should start a new game.
				 */

				Log.w("onKeyDown", "initializing new game up");
				initNewGame();
				Log.w("onKeyDown", "game initialized");
				setMode(RUNNING);
				Log.w("onKeyDown", "running mode set");
				update();
				Log.w("onKeyDown", "updated");
				return (true);
			}

			if (players[playerID].getCurDirection() != SOUTH) {
				players[playerID].setNextDirection(NORTH);
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			Log.w("onKeyDown", "down pressed");

			if (players[playerID].getCurDirection() != NORTH) {
				players[playerID].setNextDirection(SOUTH);
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			Log.w("KeyEvent", "left pressed");

			if (players[playerID].getCurDirection() != EAST) {
				players[playerID].setNextDirection(WEST);
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			Log.w("onKeyDown", "right pressed");
			if (players[playerID].getCurDirection() !=WEST) {
				players[playerID].setNextDirection(EAST);
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
			str = "Game Over";
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

			Log.w("update", "now");

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
	 * Figure out which way the player is going, see if he's run into anything 
	 * If he's not going to die, we then add to the front and to increase the trail. 
	 */
	private void updateSnake() {

		// grab the snake by the head
		Coordinate head = players[playerID].playerTrail.get(0);
		Coordinate newHead = new Coordinate(1, 1);

		players[playerID].setCurDirection(players[playerID].getNextDirection());


		switch (players[playerID].getCurDirection()) {
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

		// Look for self collisions
		int snakelength1 = players[playerID].getSize();
		for (int snakeindex = 0; snakeindex < snakelength1; snakeindex++) {
			Coordinate c = players[playerID].playerTrail.get(snakeindex);
			Log.w("updateSnake coordinate: ", "" + c.toString());
			if (c.equals(newHead)) {
				setMode(LOSE);
				return;
			}
		}

		// Look for collisions with other players!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
		// push a new head onto the ArrayList
		players[playerID].playerTrail.add(0, newHead);

		int index = 0;
		for (Coordinate c : players[playerID].playerTrail) {
			if (index == 0) {
				setTile(YELLOW_STAR, c.x, c.y);
			} else {
				setTile(RED_STAR, c.x, c.y);
			}
			index++;
		}

	}
}